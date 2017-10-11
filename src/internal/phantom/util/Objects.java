package phantom.util;

import java.util.Collection;
import java.util.function.Supplier;

/** Utility class for dealing with <code>Object</code>s and <code>null</code>ness.
 * 
 * @author Brandon D. McKay */
public class Objects
{
	/** Returns the <code>Object</code> pointed to by the specified reference if it is not
	 * <code>null</code>, otherwise returns a non-<code>null</code> <code>Object</code> given from
	 * the specified <code>Supplier</code>.
	 *
	 * @param <T> the <code>Object</code> reference's type
	 * @param object the <code>Object</code> reference to be checked for <code>null</code>ness
	 * @param supplier a <code>Supplier</code> of a non-<code>null</code> <code>Object</code>
	 * @return a non-<code>null</code> <code>Object</code> */
	public static <T> T getNonNull(T object, Supplier<T> supplier)
	{
		return getNonNull(object, supplier.get());
	}
	
	/** Returns the <code>Object</code> pointed to by the specified reference if it is not
	 * <code>null</code>, otherwise returns a specified default non-null <code>Object</code>.
	 *
	 * @param <T> the <code>Object</code> reference's type
	 * @param object the <code>Object</code> reference to be checked for <code>null</code>ness
	 * @param nonNull a default non-<code>null</code> <code>Object</code>
	 * @return a non-<code>null</code> <code>Object</code> */
	public static <T> T getNonNull(T object, T nonNull)
	{
		return object == null ? assertNonNull(nonNull, "The specified default non-null Object is null.") : object;
	}
	
	/** Asserts that the specified <code>Object</code> reference is <code>null</code>.
	 * 
	 * @param <T> the <code>Object</code> reference's type
	 * @param object the <code>Object</code> reference to be checked for <code>null</code>ness */
	public static <T> void assertNull(T object)
	{
		assertNull(object, null);
	}
	
	/** Asserts that the specified <code>Object</code> reference is <code>null</code> and specifies
	 * an error message.
	 * 
	 * @param <T> the <code>Object</code> reference's type
	 * @param object the <code>Object</code> reference to be checked for <code>null</code>ness
	 * @param message the error message */
	public static <T> void assertNull(T object, String message)
	{
		if(object != null) throw new IllegalStateException(message);
	}
	
	/** Asserts that the specified <code>Object</code> reference is not <code>null</code>.
	 * 
	 * @param <T> the <code>Object</code> reference's type
	 * @param object the <code>Object</code> reference to be checked for <code>null</code>ness
	 * @return the non-<code>null</code> <code>Object</code> */
	public static <T> T assertNonNull(T object)
	{
		return assertNonNull(object, null);
	}
	
	/** Asserts that the specified <code>Object</code> reference is not <code>null</code> and
	 * specifies an error message.
	 * 
	 * @param <T> the <code>Object</code> reference's type
	 * @param object the <code>Object</code> reference to be checked for <code>null</code>ness
	 * @param message the error message
	 * @return the non-<code>null</code> <code>Object</code> */
	public static <T> T assertNonNull(T object, String message)
	{
		if(object == null) throw new NullPointerException(message);
		return object;
	}
	
	/** Asserts that the specified array contains no <code>null</code> elements.
	 *
	 * @param <E> the element type
	 * @param array the array to be checked for <code>null</code> elements
	 * @return the array with no <code>null</code> elements */
	public static <E> E[] assertNonNullElements(E[] array)
	{
		return assertNonNullElements(array, null);
	}
	
	/** Asserts that the specified array contains no <code>null</code> elements and specifies an
	 * error message.
	 *
	 * @param <E> the element type
	 * @param array the array to be checked for <code>null</code> elements
	 * @param message the error message
	 * @return the array with no <code>null</code> elements */
	public static <E> E[] assertNonNullElements(E[] array, String message)
	{
		for(E e : assertNonNull(array, "The specified array is null."))
		{
			assertNonNull(e, message);
		}
		
		return array;
	}
	
	/** Asserts that the specified <code>Iterable</code> contains no <code>null</code> elements.
	 *
	 * @param <T> the <code>Iterable</code>'s type
	 * @param <E> the element type
	 * @param iterable the <code>Iterable</code> to be checked for <code>null</code> elements
	 * @return the <code>Iterable</code> with no <code>null</code> elements */
	public static <T extends Iterable<E>, E> T assertNonNullElements(T iterable)
	{
		return assertNonNullElements(iterable, null);
	}
	
	/** Asserts that the specified <code>Iterable</code> contains no <code>null</code> elements and
	 * specifies an error message.
	 *
	 * @param <T> the <code>Iterable</code>'s type
	 * @param <E> the element type
	 * @param iterable the <code>Iterable</code> to be checked for <code>null</code> elements
	 * @param message the error message
	 * @return the <code>Iterable</code> with no <code>null</code> elements */
	public static <T extends Iterable<E>, E> T assertNonNullElements(T iterable, String message)
	{
		assertNonNull(iterable, "The specified Iterable is null.").forEach(e -> assertNonNull(e, message));
		return iterable;
	}
	
	/** Asserts that the specified array contains at least one element.
	 *
	 * @param <E> the element type
	 * @param array the array to be checked for emptiness
	 * @return the non-empty array */
	public static <E> E[] assertNotEmpty(E[] array)
	{
		return assertNotEmpty(array, null);
	}
	
	/** Asserts that the specified array contains at least one element and specifies an error
	 * message.
	 *
	 * @param <E> the element type
	 * @param array the array to be checked for emptiness
	 * @param message the error message
	 * @return the non-empty array */
	public static <E> E[] assertNotEmpty(E[] array, String message)
	{
		if(assertNonNull(array, "The specified array is null.").length == 0) throw new IndexOutOfBoundsException(message);
		return array;
	}
	
	/** Asserts that the specified <code>Collection</code> contains at least one element.
	 *
	 * @param <T> the <code>Collection</code>'s type
	 * @param <E> the element type
	 * @param collection the <code>Collection</code> to be checked for emptiness
	 * @return the non-empty <code>Collection</code> */
	public static <T extends Collection<E>, E> T assertNotEmpty(T collection)
	{
		return assertNotEmpty(collection, null);
	}
	
	/** Asserts that the specified <code>Collection</code> contains at least one element and
	 * specifies an error message.
	 *
	 * @param <T> the <code>Collection</code>'s type
	 * @param <E> the element type
	 * @param collection the <code>Collection</code> to be checked for emptiness
	 * @param message the error message
	 * @return the non-empty <code>Collection</code> */
	public static <T extends Collection<E>, E> T assertNotEmpty(T collection, String message)
	{
		if(assertNonNull(collection, "The specified Collection is null.").size() == 0) throw new IndexOutOfBoundsException(message);
		return collection;
	}
	
	private Objects() {}
}
