package phantom.framework;

import phantom.framework.FunctionLambda;
import phantom.framework.Job;
import phantom.framework.Phantom;

class LeafFunctionBuilderImpl<I, O> implements FunctionBuilderImpl<I, O>
{
	private final FunctionLambda<I, O> lambda;
	private final byte metadata;
	
	public LeafFunctionBuilderImpl(FunctionLambda<I, O> lambda, byte metadata)
	{
		this.lambda = lambda;
		this.metadata = metadata;
	}
	
	@Override
	public InputTaskImpl<I> construct(InputTaskImpl<? super O> nextTask)
	{
		return new LeafFunctionTaskImpl(nextTask);
	}
	
	@Override
	public FunctionTaskImpl<I, O> construct()
	{
		return new LeafFunctionTaskImpl(null);
	}
	
	private class LeafFunctionTaskImpl extends AbstractOutputTaskImpl<O> implements FunctionTaskImpl<I, O>
	{
		public LeafFunctionTaskImpl(InputTaskImpl<? super O> nextTask)
		{
			super(nextTask);
		}
		
		@Override
		public Job createNewJob(I input)
		{
			return new FunctionJob(input, metadata);
		}
		
		@Override
		public Job createNewSubParallelJob(I input, Context context)
		{
			return new SubParallelFunctionJob(input, context, metadata);
		}
		
		private class FunctionJob extends AbstractJob
		{
			private final I input;
			
			public FunctionJob(I input, byte metadata)
			{
				super(metadata);
				this.input = input;
			}

			@Override
			public void run()
			{
				Phantom.dispatch(nextTask.createNewJob(lambda.invoke(input)));
			}
		}
		
		private class SubParallelFunctionJob extends AbstractSubParallelJob
		{
			private final I input;

			public SubParallelFunctionJob(I input, Context context, byte metadata)
			{
				super(context, metadata);
				this.input = input;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void run()
			{
				O output = lambda.invoke(input);
				
				if(nextTask != null) Phantom.dispatch(nextTask.createNewSubParallelJob(output, context));
				else ((OutputContext<O>) context).complete(output);
			}
		}
	}
}
