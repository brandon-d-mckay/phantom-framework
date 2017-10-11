package phantom.framework;

import phantom.framework.ConsumerLambda;
import phantom.framework.Job;
import phantom.framework.Phantom;

class LeafConsumerBuilderImpl<I> implements ConsumerBuilderImpl<I>
{
	private final ConsumerLambda<I> lambda;
	private final byte metadata;
	
	public LeafConsumerBuilderImpl(ConsumerLambda<I> lambda, byte metadata)
	{
		this.lambda = lambda;
		this.metadata = metadata;
	}
	
	@Override
	public InputTaskImpl<I> construct(NonInputTaskImpl nextTask)
	{
		return new LeafConsumerTaskImpl(nextTask);
	}
	
	@Override
	public ConsumerTaskImpl<I> construct()
	{
		return new LeafConsumerTaskImpl(null);
	}
	
	private class LeafConsumerTaskImpl extends AbstractNonOutputTaskImpl implements ConsumerTaskImpl<I>
	{
		public LeafConsumerTaskImpl(NonInputTaskImpl nextTask)
		{
			super(nextTask);
		}

		@Override
		public Job createNewJob(I input)
		{
			return new ConsumerJob(input, metadata);
		}
		
		@Override
		public Job createNewSubParallelJob(I input, Context context)
		{
			return new SubParallelConsumerJob(input, context, metadata);
		}
		
		private class ConsumerJob extends AbstractJob
		{
			private final I input;
			
			public ConsumerJob(I input, byte metadata)
			{
				super(metadata);
				this.input = input;
			}

			@Override
			public void run()
			{
				lambda.invoke(input);
				
				if(nextTask != null) Phantom.dispatch(nextTask.createNewJob());
			}
		}
		
		private class SubParallelConsumerJob extends AbstractSubParallelJob
		{
			private final I input;
			
			public SubParallelConsumerJob(I input, Context context, byte metadata)
			{
				super(context, metadata);
				this.input = input;
			}

			@Override
			public void run()
			{
				lambda.invoke(input);
				
				if(nextTask != null) Phantom.dispatch(nextTask.createNewSubParallelJob(context));
				else ((NonOutputContext) context).complete();
			}
		}
	}
}
