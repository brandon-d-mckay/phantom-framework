package phantom.util;

/** Implemented by classes that can build and produce an <code>Object</code>.
 * 
 * @see Constructor
 * @author Brandon D. McKay */
public interface Builder<T>
{
	/** Returns a built <code>Object</code>. */
	T build();
}
