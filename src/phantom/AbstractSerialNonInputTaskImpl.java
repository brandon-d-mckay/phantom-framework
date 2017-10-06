package phantom;

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
	public Job createNewSubParallelJob(ParallelTaskContext context)
	{
		return head.createNewSubParallelJob(context);
	}
}
