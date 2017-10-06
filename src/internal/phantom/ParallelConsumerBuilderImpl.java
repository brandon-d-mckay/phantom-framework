package phantom;

import java.util.function.IntFunction;
import util.AbstractCachedBuilder;

class ParallelConsumerBuilderImpl<I> extends AbstractCachedBuilder<ConsumerTaskImpl<I>> implements ParallelConsumerBuilder<I>, ConsumerBuilderImpl<I>
{
	private final IntFunction<ConsumerTaskImpl<? super I>[]> subtasksCollector;
	
	@SuppressWarnings("unchecked")
	public ParallelConsumerBuilderImpl(ConsumerBuilderImpl<I> builder)
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
		super(() -> new ParallelConsumerTask<>(subtasksCollector.apply(0), null));
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
		return new ParallelConsumerTask<>(subtasksCollector.apply(0), nextTask);
	}
	
	@Override
	public ConsumerTaskImpl<I> construct()
	{
		return getFromCache();
	}
	
	public static class ParallelConsumerTask<I> extends AbstractParallelNonOutputTaskImpl implements ConsumerTaskImpl<I>
	{
		private final ConsumerTaskImpl<? super I>[] subtasks;
		
		public ParallelConsumerTask(ConsumerTaskImpl<? super I>[] subtasks, NonInputTaskImpl nextTask)
		{
			super(subtasks.length, nextTask);
			this.subtasks = subtasks;
		}
		
		@Override
		public Job createNewJob(I input)
		{
			return new ParallelConsumerJob(input, new Context());
		}
		
		@Override
		public Job createNewSubParallelJob(I input, ParallelContext context)
		{
			return new ParallelConsumerJob(input, new ChildContext(context));
		}
		
		protected class ParallelConsumerJob extends AbstractJob
		{
			private final I input;
			private final Context context;
			
			public ParallelConsumerJob(I input, Context context)
			{
				super(Meta.ExecuteOnSupplyingThread);
				this.input = input;
				this.context = context;
			}
			
			@Override
			public void run()
			{
				for(InputTaskImpl<? super I> subtask : subtasks)
				{
					Phantom.dispatch(subtask.createNewSubParallelJob(input, context));
				}
			}
		}
	}
}
