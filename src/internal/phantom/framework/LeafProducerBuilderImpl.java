package phantom.framework;

import phantom.framework.Job;
import phantom.framework.Phantom;
import phantom.framework.ProducerLambda;

class LeafProducerBuilderImpl<O> implements ProducerBuilderImpl<O>
{
	private final ProducerLambda<O> lambda;
	private final byte metadata;
	
	public LeafProducerBuilderImpl(ProducerLambda<O> lambda, byte metadata)
	{
		this.lambda = lambda;
		this.metadata = metadata;
	}
	
	@Override
	public NonInputTaskImpl construct(InputTaskImpl<? super O> nextTask)
	{
		return new LeafProducerTaskImpl(nextTask);
	}
	
	@Override
	public ProducerTaskImpl<O> construct()
	{
		return new LeafProducerTaskImpl(null);
	}
	
	private class LeafProducerTaskImpl extends AbstractOutputTaskImpl<O> implements ProducerTaskImpl<O>
	{
		public LeafProducerTaskImpl(InputTaskImpl<? super O> nextTask)
		{
			super(nextTask);
		}
		
		@Override
		public Job createNewJob()
		{
			return new ProducerJob(metadata);
		}
		
		@Override
		public Job createNewSubParallelJob(Context context)
		{
			return new SubParallelProducerJob(context, metadata);
		}
		
		private class ProducerJob extends AbstractJob
		{
			public ProducerJob(byte metadata)
			{
				super(metadata);
			}

			@Override
			public void run()
			{
				Phantom.dispatch(nextTask.createNewJob(lambda.invoke()));
			}
		}
		
		private class SubParallelProducerJob extends AbstractSubParallelJob
		{
			public SubParallelProducerJob(Context context, byte metadata)
			{
				super(context, metadata);
			}

			@SuppressWarnings("unchecked")
			@Override
			public void run()
			{
				O output = lambda.invoke();
				
				if(nextTask != null) Phantom.dispatch(nextTask.createNewSubParallelJob(output, context));
				else ((OutputContext<O>) context).complete(output);
			}
		}
	}
}
