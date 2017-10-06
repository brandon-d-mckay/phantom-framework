package phantom;

abstract class AbstractParallelNonOutputTaskImpl extends AbstractNonOutputTaskImpl
{
	private final int numSubtasks;
	
	public AbstractParallelNonOutputTaskImpl(int numSubtasks, NonInputTaskImpl nextTask)
	{
		super(nextTask);
		this.numSubtasks = numSubtasks;
	}
	
	protected class Context extends AbstractParallelTaskContext implements ParallelNonOutputTaskContext
	{
		public Context()
		{
			super(numSubtasks);
		}
		
		@Override
		public void completeSubtask()
		{
			decrementCountdown();
		}
		
		@Override
		protected void dispatchNextTask()
		{
			if(nextTask != null) Phantom.dispatch(nextTask.createNewJob());
		}
	}
	
	protected class ChildContext extends Context
	{
		private final ParallelTaskContext parentContext;
		
		public ChildContext(ParallelTaskContext parentContext)
		{
			this.parentContext = parentContext;
		}
		
		@Override
		protected void dispatchNextTask()
		{
			if(nextTask != null) Phantom.dispatch(nextTask.createNewSubParallelJob(parentContext));
			else ((ParallelNonOutputTaskContext) parentContext).completeSubtask();
		}
	}
}
