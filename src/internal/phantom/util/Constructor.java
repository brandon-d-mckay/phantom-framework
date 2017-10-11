package phantom.util;

/** Implemented by classes that partially build or participate in the construction of an <code>Object</code>.
 *
 * @see Builder
 * @author Brandon D. McKay */
public interface Constructor<T>
{
	/** Returns a partially constructed <code>Object</code>. */
	T construct();
}
