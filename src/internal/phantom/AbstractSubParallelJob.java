package phantom;

abstract class AbstractSubParallelJob extends AbstractJob
{
	protected final ParallelContext context;
	
	public AbstractSubParallelJob(ParallelContext context, byte metadata)
	{
		super(metadata);
		this.context = context;
	}
}