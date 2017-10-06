package phantom;

import java.util.function.IntFunction;
import util.AbstractCachedBuilder;

class ParallelFunctionBuilderImpl<I, O> extends AbstractCachedBuilder<FunctionTaskImpl<I, O[]>> implements ParallelFunctionBuilder<I, O>, FunctionBuilderImpl<I, O[]>
{
	private final IntFunction<FunctionTaskImpl<? super I, ? extends O>[]> subtasksCollector;
	
	@SuppressWarnings("unchecked")
	public ParallelFunctionBuilderImpl(FunctionBuilderImpl<I, O> builder)
	{
		this(
			subtaskIndex -> {
				FunctionTaskImpl<? super I, ? extends O>[] subtasks = new FunctionTaskImpl[1 + subtaskIndex];
				subtasks[subtaskIndex] = builder.construct();
				return subtasks;
			}
		);
	}
	
	private ParallelFunctionBuilderImpl(IntFunction<FunctionTaskImpl<? super I, ? extends O>[]> subtasksCollector)
	{
		super(() -> new ParallelFunctionTask<>(subtasksCollector.apply(0), null));
		this.subtasksCollector = subtasksCollector;
	}
	
	@Override
	public ParallelFunctionBuilderImpl<I, O> and(FunctionBuilderImpl<? super I, ? extends O> builder)
	{
		return new ParallelFunctionBuilderImpl<>(
			subtaskIndex -> {
				FunctionTaskImpl<? super I, ? extends O>[] subtasks = subtasksCollector.apply(1 + subtaskIndex);
				subtasks[subtaskIndex] = builder.construct();
				return subtasks;
			}
		);
	}
	
	@Override
	public InputTaskImpl<I> construct(InputTaskImpl<? super O[]> nextTask)
	{
		return new ParallelFunctionTask<>(subtasksCollector.apply(0), nextTask);
	}
	
	@Override
	public FunctionTaskImpl<I, O[]> construct()
	{
		return getFromCache();
	}
	
	public static class ParallelFunctionTask<I, O> extends AbstractParallelOutputTaskImpl<O> implements FunctionTaskImpl<I, O[]>
	{
		private final FunctionTaskImpl<? super I, ? extends O>[] subtasks;
		
		public ParallelFunctionTask(FunctionTaskImpl<? super I, ? extends O>[] subtasks, InputTaskImpl<? super O[]> nextTask)
		{
			super(subtasks.length, nextTask);
			this.subtasks = subtasks;
		}
		
		@Override
		public Job createNewJob(I input)
		{
			return new ParallelFunctionJob(input, new Context());
		}
		
		@Override
		public Job createNewSubParallelJob(I input, ParallelContext context)
		{
			return new ParallelFunctionJob(input, new ChildContext(context));
		}
		
		protected class ParallelFunctionJob extends AbstractJob
		{
			private final I input;
			private final Context context;
			
			public ParallelFunctionJob(I input, Context context)
			{
				super(Meta.ExecuteOnSupplyingThread);
				this.input = input;
				this.context = context;
			}
			
			@Override
			public void run()
			{
				for(int i = 0; i < subtasks.length; i++)
				{
					Phantom.dispatch(subtasks[i].createNewSubParallelJob(input, context.getSubContext(i)));
				}
			}
		}
	}
}
