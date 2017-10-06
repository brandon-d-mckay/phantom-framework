package phantom;

abstract class AbstractParallelOutputTaskImpl<O> extends AbstractOutputTaskImpl<O[]>
{
	private final int numSubtasks;
	
	public AbstractParallelOutputTaskImpl(int numSubtasks, InputTaskImpl<? super O[]> nextTask)
	{
		super(nextTask);
		this.numSubtasks = numSubtasks;
	}
	
	protected class Context extends AbstractParallelTaskContext
	{
		protected final O[] subtaskOutputs;
		
		@SuppressWarnings("unchecked")
		public Context()
		{
			super(numSubtasks);
			this.subtaskOutputs = (O[]) new Object[numSubtasks];
		}
		
		public ParallelOutputTaskContext<O> getSubContext(int subtaskOutputIndex)
		{
			return new SubContext(this, subtaskOutputIndex);
		}
		
		public void completeSubtask(int subtaskOutputIndex, O output)
		{
			subtaskOutputs[subtaskOutputIndex] = output;
			decrementCountdown();
		}
		
		@Override
		protected void dispatchNextTask()
		{
			Phantom.dispatch(nextTask.createNewJob(subtaskOutputs));
		}
	}
	
	protected class ChildContext extends Context
	{
		private final ParallelTaskContext parentContext;
		
		public ChildContext(ParallelTaskContext parentContext)
		{
			this.parentContext = parentContext;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected void dispatchNextTask()
		{
			if(nextTask != null) Phantom.dispatch(nextTask.createNewSubParallelJob(subtaskOutputs, parentContext));
			else ((ParallelOutputTaskContext<O[]>) parentContext).completeSubtask(subtaskOutputs);
		}
	}
	
	private class SubContext implements ParallelOutputTaskContext<O>
	{
		private final Context delegateContext;
		private final int subtaskOutputIndex;

		public SubContext(Context delegateContext, int subtaskOutputIndex)
		{
			this.delegateContext = delegateContext;
			this.subtaskOutputIndex = subtaskOutputIndex;
		}
		
		@Override
		public void completeSubtask(O output)
		{
			delegateContext.completeSubtask(subtaskOutputIndex, output);
		}
	}
}
