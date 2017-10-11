package phantom.framework;

import phantom.framework.Job;
import phantom.framework.Phantom;
import phantom.framework.ProcedureLambda;

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
	
	private class LeafProcedureTaskImpl extends AbstractNonOutputTaskImpl implements ProcedureTaskImpl
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
		public Job createNewSubParallelJob(Context context)
		{
			return new SubParallelProcedureJob(context, metadata);
		}
		
		private class ProcedureJob extends AbstractJob
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
		
		private class SubParallelProcedureJob extends AbstractSubParallelJob
		{
			public SubParallelProcedureJob(Context context, byte metadata)
			{
				super(context, metadata);
			}

			@Override
			public void run()
			{
				lambda.invoke();
				
				if(nextTask != null) Phantom.dispatch(nextTask.createNewSubParallelJob(context));
				else ((NonOutputContext) context).complete();
			}
		}
	}
}
