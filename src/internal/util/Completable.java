package util;

/** Implemented by classes that will eventually finish some activity.
 * 
 * @see Awaitable
 * @see Interruptible */
public interface Completable extends Awaitable
{
	/** Returns <code>true</code> if this <code>Object</code> has completed its activity or has reached some non-reversible state.
	 * Nothing is promised as to the outcome of the activity&#8212;only that it has been finished. */
	boolean isCompleted();
}
