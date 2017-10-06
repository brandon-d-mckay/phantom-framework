package phantom;

abstract class AbstractOutputTaskImpl<O> implements OutputTaskImpl<O>
{
	protected final InputTaskImpl<? super O> nextTask;

	public AbstractOutputTaskImpl(InputTaskImpl<? super O> nextTask)
	{
		this.nextTask = nextTask;
	}
}
