package phantom;

import java.util.concurrent.atomic.AtomicInteger;

abstract class AbstractParallelContext implements ParallelContext
{
	private final AtomicInteger subtaskCompletionCountdown;
	
	public AbstractParallelContext(int numSubtasks)
	{
		subtaskCompletionCountdown = new AtomicInteger(numSubtasks);
	}
 
	protected void decrementCountdown()
	{
		if(subtaskCompletionCountdown.decrementAndGet() == 0) dispatchNextTask();
	}

	protected abstract void dispatchNextTask();
}