package phantom;

import java.util.concurrent.atomic.AtomicInteger;

abstract class AbstractParallelTaskContext implements ParallelTaskContext
{
	private final AtomicInteger subtaskCompletionCountdown;
	
	public AbstractParallelTaskContext(int numSubtasks)
	{
		subtaskCompletionCountdown = new AtomicInteger(numSubtasks);
	}
 
	protected void decrementCountdown()
	{
		if(subtaskCompletionCountdown.decrementAndGet() == 0) dispatchNextTask();
	}

	protected abstract void dispatchNextTask();
}