package util;

import java.lang.reflect.Array;
import java.util.Arrays;

public class VolatileArray<E>
{
	private static final int base = VeryUnsafe.getArrayBaseOffset(Object[].class);
	private static final int shift = 31 - Integer.numberOfLeadingZeros(VeryUnsafe.getArrayIndexScale(Object[].class));
	
	private final Object[] array;
	
	public VolatileArray(int length)
	{
		this.array = new Object[length];
	}
	
	public VolatileArray(E[] array)
	{
		this.array = Arrays.copyOf(array, array.length, Object[].class);
	}
	
	private static long byteOffset(int i)
	{
		return ((long) i << shift) + base;
	}
	
	private long checkedByteOffset(int i)
	{
		if(i < 0 || i >= array.length) throw new IndexOutOfBoundsException("index " + i);
		
		return byteOffset(i);
	}
	
	public final int length()
	{
		return array.length;
	}
	
	@SuppressWarnings("unchecked")
	public final E get(int index)
	{
		return (E) VeryUnsafe.getIndexVolatile(array, checkedByteOffset(index));
	}
	
	public final void set(int index, E value)
	{
		VeryUnsafe.putIndexVolatile(array, checkedByteOffset(index), value);
	}
	
	public final Object[] toArray()
	{
		Object[] copy = new Object[array.length];
		System.arraycopy(array, 0, copy, 0, array.length);
		return copy;
	}
	
	@SuppressWarnings("unchecked")
	public final E[] toArray(Class<E> elementClass)
	{
		E[] copy = (E[]) Array.newInstance(elementClass, array.length);
		System.arraycopy(array, 0, copy, 0, array.length);
		return copy;
	}
	
	public final void copyArray(int sourceIndex, E[] destination, int destinationIndex, int length)
	{
		System.arraycopy(array, sourceIndex, destination, destinationIndex, length);
	}
}
