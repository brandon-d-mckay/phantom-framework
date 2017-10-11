package phantom.concurrent;

import static phantom.util.VeryUnsafe.arrayBaseOffset;
import static phantom.util.VeryUnsafe.arrayIndexScale;
import static phantom.util.VeryUnsafe.compareAndSwapObject;
import static phantom.util.VeryUnsafe.getAndSetObject;
import static phantom.util.VeryUnsafe.getObjectVolatile;
import static phantom.util.VeryUnsafe.putObjectVolatile;
import static phantom.util.VeryUnsafe.putOrderedObject;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import phantom.util.Undocumented;
import phantom.util.VeryUnsafe;

@Undocumented
public class ConcurrentArray<E> implements Serializable
{
	private static final int base = arrayBaseOffset(Object[].class);
	private static final int shift = 31 - Integer.numberOfLeadingZeros(arrayIndexScale(Object[].class));
	
	private final Object[] array;
	private static final long arrayFieldOffset = VeryUnsafe.objectFieldOffset(ConcurrentArray.class, "array");
	
	public ConcurrentArray(E[] array)
	{
		this(array.length);
		System.arraycopy(array, 0, this.array, 0, array.length);
	}
	
	public ConcurrentArray(int length)
	{
		this.array = new Object[length];
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
	private E getRaw(long offset)
	{
		return (E) getObjectVolatile(array, offset);
	}
	
	@SuppressWarnings("unchecked")
	public final E getUnsafe(int index)
	{
		return (E) array[index];
	}
	
	@SuppressWarnings("unchecked")
	public final E getVolatile(int index)
	{
		return (E) getObjectVolatile(array, checkedByteOffset(index));
	}
	
	public final void setVolatile(int index, E value)
	{
		putObjectVolatile(array, checkedByteOffset(index), value);
	}
	
	public final void setLazy(int index, E newValue)
	{
		putOrderedObject(array, checkedByteOffset(index), newValue);
	}
	
	public final void setUnsafe(int index, E value)
	{
		array[index] = value;
	}
	
	
	@SuppressWarnings("unchecked")
	public final E getAndSet(int i, E newValue)
	{
		return (E) getAndSetObject(array, checkedByteOffset(i), newValue);
	}
	
	public final boolean compareAndSet(int i, E expect, E update)
	{
		return compareAndSetRaw(checkedByteOffset(i), expect, update);
	}
	
	private boolean compareAndSetRaw(long offset, E expect, E update)
	{
		return compareAndSwapObject(array, offset, expect, update);
	}
	
	public final E getAndUpdate(int i, UnaryOperator<E> updateFunction)
	{
		long offset = checkedByteOffset(i);
		E prev, next;
		
		do
		{
			prev = getRaw(offset);
			next = updateFunction.apply(prev);
		}
		while(!compareAndSetRaw(offset, prev, next));
		
		return prev;
	}
	
	public final E updateAndGet(int i, UnaryOperator<E> updateFunction)
	{
		long offset = checkedByteOffset(i);
		E prev, next;
		
		do
		{
			prev = getRaw(offset);
			next = updateFunction.apply(prev);
		}
		while(!compareAndSetRaw(offset, prev, next));
		
		return next;
	}
	
	public final E getAndAccumulate(int i, E x, BinaryOperator<E> accumulatorFunction)
	{
		long offset = checkedByteOffset(i);
		E prev, next;
		
		do
		{
			prev = getRaw(offset);
			next = accumulatorFunction.apply(prev, x);
		}
		while(!compareAndSetRaw(offset, prev, next));
		
		return prev;
	}
	
	public final E accumulateAndGet(int i, E x, BinaryOperator<E> accumulatorFunction)
	{
		long offset = checkedByteOffset(i);
		E prev, next;
		
		do
		{
			prev = getRaw(offset);
			next = accumulatorFunction.apply(prev, x);
		}
		while(!compareAndSetRaw(offset, prev, next));
		
		return next;
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

	@Override
	public String toString()
	{
		return array.toString();
	}
	
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException, java.io.InvalidObjectException
	{
		Object a = s.readFields().get("array", null);
		if(a == null || !a.getClass().isArray()) throw new java.io.InvalidObjectException("Not array type");
		if(a.getClass() != Object[].class) a = toArray();
		putObjectVolatile(this, arrayFieldOffset, a);
	}
	
	private static final long serialVersionUID = -6436767180348054354L;
}
