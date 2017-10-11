package phantom.framework;

import phantom.framework.Job;

abstract class AbstractSerialNonInputTaskImpl implements NonInputTaskImpl
{
	protected final NonInputTaskImpl head;
	
	public AbstractSerialNonInputTaskImpl(NonInputTaskImpl head)
	{
		this.head = head;
	}

	@Override
	public Job createNewJob()
	{
		return head.createNewJob();
	}

	@Override
	public Job createNewSubParallelJob(Context context)
	{
		return head.createNewSubParallelJob(context);
	}
}
