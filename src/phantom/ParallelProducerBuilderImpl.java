package phantom;

import java.util.function.IntFunction;
import util.AbstractCachedBuilder;

class ParallelProducerBuilderImpl<O> extends AbstractCachedBuilder<ProducerTaskImpl<O[]>> implements ParallelProducerBuilder<O>, ProducerBuilderImpl<O[]>
{
	private final IntFunction<ProducerTaskImpl<? extends O>[]> subtasksCollector;
	
	@SuppressWarnings("unchecked")
	public ParallelProducerBuilderImpl(ProducerBuilderImpl<O> builder)
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
		super(() -> new ParallelProducerTask<>(subtasksCollector.apply(0), null));
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
	public NonInputTaskImpl construct(InputTaskImpl<? super O[]> nextTask)
	{
		return new ParallelProducerTask<>(subtasksCollector.apply(0), nextTask);
	}

	@Override
	public ProducerTaskImpl<O[]> construct()
	{
		return getFromCache();
	}
	
	public static class ParallelProducerTask<O> extends AbstractParallelOutputTaskImpl<O> implements ProducerTaskImpl<O[]>
	{
		private final ProducerTaskImpl<? extends O>[] subtasks;

		public ParallelProducerTask(ProducerTaskImpl<? extends O>[] subtasks, InputTaskImpl<? super O[]> nextTask)
		{
			super(subtasks.length, nextTask);
			this.subtasks = subtasks;
		}
		
		@Override
		public Job createNewJob()
		{
			return new ParallelProducerJob(new Context());
		}

		@Override
		public Job createNewSubParallelJob(ParallelTaskContext context)
		{
			return new ParallelProducerJob(new ChildContext(context));
		}
		
		protected class ParallelProducerJob extends AbstractJob
		{
			private final Context context;
			
			public ParallelProducerJob(Context context)
			{
				super(Meta.ExecuteOnSupplyingThread);
				this.context = context;
			}

			@Override
			public void run()
			{
				for(int i = 0; i < subtasks.length; i++)
				{
					Phantom.dispatch(subtasks[i].createNewSubParallelJob(context.getSubContext(i)));
				}
			}
		}
	}
}
