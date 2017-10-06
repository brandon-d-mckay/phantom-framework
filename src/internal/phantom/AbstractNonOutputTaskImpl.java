package phantom;

abstract class AbstractNonOutputTaskImpl implements NonOutputTaskImpl
{
	protected final NonInputTaskImpl nextTask;
	
	public AbstractNonOutputTaskImpl(NonInputTaskImpl nextTask)
	{
		this.nextTask = nextTask;
	}
}
