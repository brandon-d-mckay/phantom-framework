package phantom.util;

/** Implemented by classes that may serve as a proxy to an <code>Object</code> of a different type. This
 * is most typically used to control access (APIs) or for lazy instantiation.
 * 
 * @author Brandon D. McKay */
public interface Proxy<T>
{
	/** Returns the true <code>Object</code> being delegated to. */
	T unmask();
}
