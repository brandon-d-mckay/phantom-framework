package phantom.concurrent;

/** Implemented by classes that can be interrupted during the course of an activity. 
 * 
 * @see Awaitable
 * @see Completable */
public interface Interruptible
{
	/** Interrupts this <code>Object</code>'s activity. There is no guarantee or contract as to the effects of this action, but it
	 * is generally implied that the <code>Object</code> should attempt to cease functioning. */
	void interrupt();
}
