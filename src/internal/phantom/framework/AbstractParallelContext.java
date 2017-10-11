package phantom.framework;

import static phantom.util.VeryUnsafe.decrementAndGetInt;
import static phantom.util.VeryUnsafe.objectFieldOffset;

abstract class AbstractParallelContext implements Context
{
	@SuppressWarnings("unused") private volatile int subtaskCompletionCountdown;
	private static final long subtaskCompletionCountdownFieldOffset = objectFieldOffset(AbstractParallelContext.class, "subtaskCompletionCountdown");
	
	public AbstractParallelContext(int numSubtasks)
	{
		this.subtaskCompletionCountdown = numSubtasks;
	}
 
	protected final void decrementCountdown()
	{
		if(decrementAndGetInt(this, subtaskCompletionCountdownFieldOffset) == 0) dispatchNextTask();
	}

	protected abstract void dispatchNextTask();
}