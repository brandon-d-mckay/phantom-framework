package phantom.concurrent;

/** Implemented by classes that can start some activity.
 * 
 * @author Brandon D. McKay */
public interface Startable
{
	/** Initiates this <code>Startable</code>. */
	void start();
}