package phantom;

abstract class AbstractSubParallelJob extends AbstractJob
{
	protected final ParallelTaskContext context;
	
	public AbstractSubParallelJob(ParallelTaskContext context, byte metadata)
	{
		super(metadata);
		this.context = context;
	}
}