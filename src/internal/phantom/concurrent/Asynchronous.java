package phantom.concurrent;

/** Implemented by <code>Runnable</code> classes that can be ran asynchronously.
 * 
 * @author Brandon D. McKay*/
public interface Asynchronous extends Runnable, Startable
{
	/** Asynchronously starts running this <code>Runnable</code> in a different
	 * <code>Thread</code>. */
	@Override
	default void start() { new Thread(this).start(); }
}
