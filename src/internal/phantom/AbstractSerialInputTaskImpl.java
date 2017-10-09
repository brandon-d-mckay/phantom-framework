package phantom;

abstract class AbstractSerialInputTaskImpl<I> implements InputTaskImpl<I>
{
	protected final InputTaskImpl<? super I> head;
	
	public AbstractSerialInputTaskImpl(InputTaskImpl<? super I> head)
	{
		this.head = head;
	}

	@Override
	public Job createNewJob(I input)
	{
		return head.createNewJob(input);
	}

	@Override
	public Job createNewSubParallelJob(I input, Context context)
	{
		return head.createNewSubParallelJob(input, context);
	}
}
