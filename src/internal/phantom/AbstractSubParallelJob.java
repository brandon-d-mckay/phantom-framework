package phantom;

abstract class AbstractSubParallelJob extends AbstractJob
{
	protected final Context context;
	
	public AbstractSubParallelJob(Context context, byte metadata)
	{
		super(metadata);
		this.context = context;
	}
}