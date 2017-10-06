package phantom;

class LeafProcedureBuilderImpl implements ProcedureBuilderImpl
{
	private final ProcedureLambda lambda;
	private final byte metadata;
	
	public LeafProcedureBuilderImpl(ProcedureLambda lambda, byte metadata)
	{
		this.lambda = lambda;
		this.metadata = metadata;
	}
	
	@Override
	public NonInputTaskImpl construct(NonInputTaskImpl nextTask)
	{
		return new LeafProcedureTaskImpl(nextTask);
	}
	
	@Override
	public ProcedureTaskImpl construct()
	{
		return new LeafProcedureTaskImpl(null);
	}
	
	class LeafProcedureTaskImpl extends AbstractNonOutputTaskImpl implements ProcedureTaskImpl
	{
		public LeafProcedureTaskImpl(NonInputTaskImpl nextTask)
		{
			super(nextTask);
		}
		
		@Override
		public Job createNewJob()
		{
			return new ProcedureJob(metadata);
		}
		
		@Override
		public Job createNewSubParallelJob(ParallelContext context)
		{
			return new SubParallelProcedureJob(context, metadata);
		}
		
		protected class ProcedureJob extends AbstractJob
		{
			public ProcedureJob(byte metadata)
			{
				super(metadata);
			}

			@Override
			public void run()
			{
				lambda.invoke();
				
				if(nextTask != null) Phantom.dispatch(nextTask.createNewJob());
			}
		}
		
		protected class SubParallelProcedureJob extends AbstractSubParallelJob
		{
			public SubParallelProcedureJob(ParallelContext context, byte metadata)
			{
				super(context, metadata);
			}

			@Override
			public void run()
			{
				lambda.invoke();
				
				if(nextTask != null) Phantom.dispatch(nextTask.createNewSubParallelJob(context));
				else ((ParallelNonOutputContext) context).completeSubtask();
			}
		}
	}
}
