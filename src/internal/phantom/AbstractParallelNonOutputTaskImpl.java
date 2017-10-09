package phantom;

abstract class AbstractParallelNonOutputTaskImpl extends AbstractNonOutputTaskImpl
{
	protected final int numSubtasks;
	
	public AbstractParallelNonOutputTaskImpl(int numSubtasks, NonInputTaskImpl nextTask)
	{
		super(nextTask);
		this.numSubtasks = numSubtasks;
	}
	
	protected class ParallelNonOutputContext extends AbstractParallelContext implements NonOutputContext
	{
		public ParallelNonOutputContext()
		{
			super(numSubtasks);
		}
		
		@Override
		public void complete()
		{
			decrementCountdown();
		}
		
		@Override
		protected void dispatchNextTask()
		{
			if(nextTask != null) Phantom.dispatch(nextTask.createNewJob());
		}
	}
	
	protected class ParallelNonOutputChildContext extends ParallelNonOutputContext
	{
		private final NonOutputContext parentContext;
		
		public ParallelNonOutputChildContext(Context parentContext)
		{
			this.parentContext = (NonOutputContext) parentContext;
		}
		
		@Override
		protected void dispatchNextTask()
		{
			if(nextTask != null) Phantom.dispatch(nextTask.createNewSubParallelJob(parentContext));
			else parentContext.complete();
		}
	}
}
