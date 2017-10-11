package phantom.framework;

import java.util.List;
import java.util.function.IntFunction;
import phantom.concurrent.ConcurrentCachedSupplier;
import phantom.framework.Job;
import phantom.framework.Meta;
import phantom.framework.ParallelFunctionBuilder;
import phantom.framework.Phantom;

class ParallelFunctionBuilderImpl<I, O> extends ConcurrentCachedSupplier<FunctionTaskImpl<I, List<O>>> implements ParallelFunctionBuilder<I, O>, FunctionBuilderImpl<I, List<O>>
{
	private final IntFunction<FunctionTaskImpl<? super I, ? extends O>[]> subtasksCollector;
	
	@SuppressWarnings("unchecked")
	public ParallelFunctionBuilderImpl(FunctionBuilderImpl<? super I, ? extends O> builder)
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
		super(() -> new ParallelFunctionTaskImpl<>(subtasksCollector.apply(0), null));
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
	public InputTaskImpl<I> construct(InputTaskImpl<? super List<O>> nextTask)
	{
		return new ParallelFunctionTaskImpl<>(subtasksCollector.apply(0), nextTask);
	}
	
	@Override
	public FunctionTaskImpl<I, List<O>> construct()
	{
		return getFromCache();
	}
	
	private static class ParallelFunctionTaskImpl<I, O> extends AbstractParallelOutputTaskImpl<O> implements FunctionTaskImpl<I, List<O>>
	{
		private final FunctionTaskImpl<? super I, ? extends O>[] subtasks;
		
		public ParallelFunctionTaskImpl(FunctionTaskImpl<? super I, ? extends O>[] subtasks, InputTaskImpl<? super List<O>> nextTask)
		{
			super(subtasks.length, nextTask);
			this.subtasks = subtasks;
		}
		
		@Override
		public Job createNewJob(I input)
		{
			return new ParallelFunctionJob(input, new ParallelOutputContext());
		}
		
		@Override
		public Job createNewSubParallelJob(I input, Context context)
		{
			return new ParallelFunctionJob(input, new ParallelOutputChildContext(context));
		}
		
		private class ParallelFunctionJob extends AbstractJob
		{
			private final I input;
			private final ParallelOutputContext context;
			
			public ParallelFunctionJob(I input, ParallelOutputContext context)
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
					jobs[i] = subtasks[i].createNewSubParallelJob(input, context.new ParallelOutputSubContext(i));
				}
				
				Phantom.dispatch(jobs);
			}
		}
	}
}
