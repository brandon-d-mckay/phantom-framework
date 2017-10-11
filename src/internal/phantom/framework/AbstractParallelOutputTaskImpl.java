package phantom.framework;

import java.util.ArrayList;
import java.util.List;
import phantom.framework.Phantom;

abstract class AbstractParallelOutputTaskImpl<O> extends AbstractOutputTaskImpl<List<O>>
{
	protected final int numSubtasks;
	
	public AbstractParallelOutputTaskImpl(int numSubtasks, InputTaskImpl<? super List<O>> nextTask)
	{
		super(nextTask);
		this.numSubtasks = numSubtasks;
	}
	
	protected class ParallelOutputContext extends AbstractParallelContext
	{
		protected final ArrayList<O> subtaskOutputs = new ArrayList<>(numSubtasks);
		
		{ for(int i = 0; i < numSubtasks; i++) subtaskOutputs.add(null); }
		
		public ParallelOutputContext()
		{
			super(numSubtasks);
		}
		
		public class ParallelOutputSubContext implements OutputContext<O>
		{
			private final int subtaskOutputIndex;

			public ParallelOutputSubContext(int subtaskOutputIndex)
			{
				this.subtaskOutputIndex = subtaskOutputIndex;
			}
			
			@Override
			public void complete(O output)
			{
				ParallelOutputContext.this.completeSubtask(subtaskOutputIndex, output);
			}
		}
		
		public void completeSubtask(int subtaskOutputIndex, O output)
		{
			subtaskOutputs.set(subtaskOutputIndex, output);
			decrementCountdown();
		}
		
		@Override
		protected void dispatchNextTask()
		{
			Phantom.dispatch(nextTask.createNewJob(subtaskOutputs));
		}
	}

	protected class ParallelOutputChildContext extends ParallelOutputContext
	{
		private final OutputContext<List<O>> parentContext;
		
		@SuppressWarnings("unchecked")
		public ParallelOutputChildContext(Context parentContext)
		{
			this.parentContext = (OutputContext<List<O>>) parentContext;
		}
		
		@Override
		protected void dispatchNextTask()
		{
			if(nextTask != null) Phantom.dispatch(nextTask.createNewSubParallelJob(subtaskOutputs, parentContext));
			else parentContext.complete(subtaskOutputs);
		}
	}
}
