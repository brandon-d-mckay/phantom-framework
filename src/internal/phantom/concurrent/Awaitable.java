package phantom.concurrent;

/** Implemented by classes that can be waited on for a finite amount of time.
 * 
 * @see Completable
 * @see Interruptible */
public interface Awaitable
{
	/** Waits for an eventual notification from this <code>Object</code>. */
	void await() throws InterruptedException;
}
