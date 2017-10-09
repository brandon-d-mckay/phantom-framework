package phantom;

import java.util.function.IntFunction;
import util.AbstractCachedBuilder;
import util.VolatileArray;

class ParallelProducerBuilderImpl<O> extends AbstractCachedBuilder<ProducerTaskImpl<VolatileArray<O>>> implements ParallelProducerBuilder<O>, ProducerBuilderImpl<VolatileArray<O>>
{
	private final IntFunction<ProducerTaskImpl<? extends O>[]> subtasksCollector;
	
	@SuppressWarnings("unchecked")
	public ParallelProducerBuilderImpl(ProducerBuilderImpl<? extends O> builder)
	{
		this(
			subtaskIndex -> {
				ProducerTaskImpl<? extends O>[] subtasks = new ProducerTaskImpl[1 + subtaskIndex];
				subtasks[subtaskIndex] = builder.construct();
				return subtasks;
			}
		);
	}
	
	private ParallelProducerBuilderImpl(IntFunction<ProducerTaskImpl<? extends O>[]> subtasksCollector)
	{
		super(() -> new ParallelProducerTaskImpl<>(subtasksCollector.apply(0), null));
		this.subtasksCollector = subtasksCollector;
	}
	
	@Override
	public ParallelProducerBuilderImpl<O> and(ProducerBuilderImpl<? extends O> builder)
	{
		return new ParallelProducerBuilderImpl<>(
			subtaskIndex -> {
				ProducerTaskImpl<? extends O>[] subtasks = subtasksCollector.apply(1 + subtaskIndex);
				subtasks[subtaskIndex] = builder.construct();
				return subtasks;
			}
		);
	}
	
	@Override
	public NonInputTaskImpl construct(InputTaskImpl<? super VolatileArray<O>> nextTask)
	{
		return new ParallelProducerTaskImpl<>(subtasksCollector.apply(0), nextTask);
	}

	@Override
	public ProducerTaskImpl<VolatileArray<O>> construct()
	{
		return getFromCache();
	}
	
	private static class ParallelProducerTaskImpl<O> extends AbstractParallelOutputTaskImpl<O> implements ProducerTaskImpl<VolatileArray<O>>
	{
		private final ProducerTaskImpl<? extends O>[] subtasks;

		public ParallelProducerTaskImpl(ProducerTaskImpl<? extends O>[] subtasks, InputTaskImpl<? super VolatileArray<O>> nextTask)
		{
			super(subtasks.length, nextTask);
			this.subtasks = subtasks;
		}
		
		@Override
		public Job createNewJob()
		{
			return new ParallelProducerJob(new ParallelOutputContext());
		}

		@Override
		public Job createNewSubParallelJob(Context context)
		{
			return new ParallelProducerJob(new ParallelOutputChildContext(context));
		}
		
		private class ParallelProducerJob extends AbstractJob
		{
			private final ParallelOutputContext context;
			
			public ParallelProducerJob(ParallelOutputContext context)
			{
				super(Meta.ExecuteOnSupplyingThread);
				this.context = context;
			}

			@Override
			public void run()
			{
				Job[] jobs = new Job[numSubtasks];
				
				for(int i = 0; i < numSubtasks; i++)
				{
					jobs[i] = subtasks[i].createNewSubParallelJob(context.new ParallelOutputSubContext(i));
				}
				
				Phantom.dispatch(jobs);
			}
		}
	}
}
