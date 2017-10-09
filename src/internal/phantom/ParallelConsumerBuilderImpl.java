package phantom;

import java.util.function.IntFunction;
import util.AbstractCachedBuilder;

class ParallelConsumerBuilderImpl<I> extends AbstractCachedBuilder<ConsumerTaskImpl<I>> implements ParallelConsumerBuilder<I>, ConsumerBuilderImpl<I>
{
	private final IntFunction<ConsumerTaskImpl<? super I>[]> subtasksCollector;
	
	@SuppressWarnings("unchecked")
	public ParallelConsumerBuilderImpl(ConsumerBuilderImpl<? super I> builder)
	{
		this(
			subtaskIndex -> {
				ConsumerTaskImpl<? super I>[] subtasks = new ConsumerTaskImpl[1 + subtaskIndex];
				subtasks[subtaskIndex] = builder.construct();
				return subtasks;
			}
		);
	}
	
	private ParallelConsumerBuilderImpl(IntFunction<ConsumerTaskImpl<? super I>[]> subtasksCollector)
	{
		super(() -> new ParallelConsumerTaskImpl<>(subtasksCollector.apply(0), null));
		this.subtasksCollector = subtasksCollector;
	}
	
	@Override
	public ParallelConsumerBuilderImpl<I> and(ConsumerBuilderImpl<? super I> builder)
	{
		return new ParallelConsumerBuilderImpl<>(
			subtaskIndex -> {
				ConsumerTaskImpl<? super I>[] subtasks = subtasksCollector.apply(1 + subtaskIndex);
				subtasks[subtaskIndex] = builder.construct();
				return subtasks;
			}
		);
	}
	
	@Override
	public InputTaskImpl<I> construct(NonInputTaskImpl nextTask)
	{
		return new ParallelConsumerTaskImpl<>(subtasksCollector.apply(0), nextTask);
	}
	
	@Override
	public ConsumerTaskImpl<I> construct()
	{
		return getFromCache();
	}
	
	private static class ParallelConsumerTaskImpl<I> extends AbstractParallelNonOutputTaskImpl implements ConsumerTaskImpl<I>
	{
		private final ConsumerTaskImpl<? super I>[] subtasks;
		
		public ParallelConsumerTaskImpl(ConsumerTaskImpl<? super I>[] subtasks, NonInputTaskImpl nextTask)
		{
			super(subtasks.length, nextTask);
			this.subtasks = subtasks;
		}
		
		@Override
		public Job createNewJob(I input)
		{
			return new ParallelConsumerJob(input, new ParallelNonOutputContext());
		}
		
		@Override
		public Job createNewSubParallelJob(I input, Context context)
		{
			return new ParallelConsumerJob(input, new ParallelNonOutputChildContext(context));
		}
		
		private class ParallelConsumerJob extends AbstractJob
		{
			private final I input;
			private final ParallelNonOutputContext context;
			
			public ParallelConsumerJob(I input, ParallelNonOutputContext context)
			{
				super(Meta.ExecuteOnSupplyingThread);
				this.input = input;
				this.context = context;
			}
			
			@Override
			
			public void run()
			{
				Job[] jobs = new Job[numSubtasks];
			
				for(int i = 0; i < subtasks.length; i++)
				{
					jobs[i] = subtasks[i].createNewSubParallelJob(input, context);
				}
				
				Phantom.dispatch(jobs);
			}
		}
	}
}
