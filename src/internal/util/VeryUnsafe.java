package util;

import java.lang.reflect.Field;
import java.util.function.BinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class VeryUnsafe
{
	public static final Unsafe unsafe;
	
	static
	{
		try
		{
			Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			unsafe = (Unsafe) f.get(null);
		}
		catch(NoSuchFieldException | IllegalAccessException e)
		{
			throw new RuntimeException("Error: Could not retrieve Unsafe object.", e);
		}
	}
	
	public static long getFieldOffset(Class<?> objectClass, String fieldName)
	{
		try
		{
			return unsafe.objectFieldOffset(objectClass.getDeclaredField(fieldName));
		}
		catch(NoSuchFieldException e)
		{
			throw new RuntimeException("Error: No field with the specified name exists in the specified class.", e);
		}
	}
	
	public static int getArrayBaseOffset(Class<?> arrayClass)
	{
		return unsafe.arrayBaseOffset(arrayClass);
	}

	public static int getArrayIndexScale(Class<?> arrayClass)
	{
		return unsafe.arrayIndexScale(arrayClass);
	}
	
	public static Object getIndexVolatile(Object[] array, long byteOffset)
	{
		return unsafe.getObjectVolatile(array, byteOffset);
	}
	
	public static void putIndexVolatile(Object[] array, long byteOffset, Object value)
	{
		unsafe.putObjectVolatile(array, byteOffset, value);
	}
	
	public static void lazySetObject(Object object, long fieldOffset, Object newValue)
	{
		unsafe.putOrderedObject(object, fieldOffset, newValue);
	}
	
	public static <V> boolean compareAndSetObject(Object object, long fieldOffset, V expect, V update)
	{
		return unsafe.compareAndSwapObject(object, fieldOffset, expect, update);
	}
	
	public static Object getAndSetObject(Object object, long fieldOffset, Object newValue)
	{
		return unsafe.getAndSetObject(object, fieldOffset, newValue);
	}
	
	public static <V> Object getAndUpdateObject(Object object, long fieldOffset, Supplier<V> supplier, UnaryOperator<V> updateFunction)
	{
		V prev, next;
		
		do
		{
			prev = supplier.get();
			next = updateFunction.apply(prev);
		}
		while(!compareAndSetObject(object, fieldOffset, prev, next));
		
		return prev;
	}
	
	public static <V> Object updateAndGetObject(Object object, long fieldOffset, Supplier<V> supplier, UnaryOperator<V> updateFunction)
	{
		V prev, next;
		
		do
		{
			prev = supplier.get();
			next = updateFunction.apply(prev);
		}
		while(!compareAndSetObject(object, fieldOffset, prev, next));
		
		return next;
	}
	
	public static <V> Object getAndAccumulateObject(Object object, long fieldOffset, Supplier<V> supplier, V x, BinaryOperator<V> accumulatorFunction)
	{
		V prev, next;
		
		do
		{
			prev = supplier.get();
			next = accumulatorFunction.apply(prev, x);
		}
		while(!compareAndSetObject(object, fieldOffset, prev, next));
		
		return prev;
	}
	
	public static <V> Object accumulateAndGetObject(Object object, long fieldOffset, Supplier<V> supplier, V x, BinaryOperator<V> accumulatorFunction)
	{
		V prev, next;
		
		do
		{
			prev = supplier.get();
			next = accumulatorFunction.apply(prev, x);
		}
		while(!compareAndSetObject(object, fieldOffset, prev, next));
		
		return next;
	}
	
	public static void lazySetInt(Object object, long fieldOffset, int newValue)
	{
		unsafe.putOrderedInt(object, fieldOffset, newValue);
	}
	
	public static int getAndSetInt(Object object, long fieldOffset, int newValue)
	{
		return unsafe.getAndSetInt(object, fieldOffset, newValue);
	}
	
	public static boolean compareAndSetInt(Object object, long fieldOffset, int expect, int update)
	{
		return unsafe.compareAndSwapInt(object, fieldOffset, expect, update);
	}
	
	public static int getAndIncrementInt(Object object, long fieldOffset)
	{
		return unsafe.getAndAddInt(object, fieldOffset, 1);
	}
	
	public static int getAndDecrementInt(Object object, long fieldOffset)
	{
		return unsafe.getAndAddInt(object, fieldOffset, -1);
	}
	
	public static int getAndAddInt(Object object, long fieldOffset, int delta)
	{
		return unsafe.getAndAddInt(object, fieldOffset, delta);
	}
	
	public static int incrementAndGetInt(Object object, long fieldOffset)
	{
		return unsafe.getAndAddInt(object, fieldOffset, 1) + 1;
	}
	
	public static int decrementAndGetInt(Object object, long fieldOffset)
	{
		return unsafe.getAndAddInt(object, fieldOffset, -1) - 1;
	}
	
	public static int addAndGetInt(Object object, long fieldOffset, int delta)
	{
		return unsafe.getAndAddInt(object, fieldOffset, delta) + delta;
	}
	
	public static int getAndUpdateInt(Object object, long fieldOffset, Supplier<Integer> supplier, IntUnaryOperator updateFunction)
	{
		int prev, next;
		
		do
		{
			prev = supplier.get();
			next = updateFunction.applyAsInt(prev);
		}
		while(!compareAndSetInt(object, fieldOffset, prev, next));
		
		return prev;
	}
	
	public static int updateAndGetInt(Object object, long fieldOffset, Supplier<Integer> supplier, IntUnaryOperator updateFunction)
	{
		int prev, next;
		
		do
		{
			prev = supplier.get();
			next = updateFunction.applyAsInt(prev);
		}
		while(!compareAndSetInt(object, fieldOffset, prev, next));
		
		return next;
	}
	
	public static int getAndAccumulateInt(Object object, long fieldOffset, Supplier<Integer> supplier, int x, IntBinaryOperator accumulatorFunction)
	{
		int prev, next;
		
		do
		{
			prev = supplier.get();
			next = accumulatorFunction.applyAsInt(prev, x);
		}
		while(!compareAndSetInt(object, fieldOffset, prev, next));
		
		return prev;
	}
	
	public static int accumulateAndGetInt(Object object, long fieldOffset, Supplier<Integer> supplier, int x, IntBinaryOperator accumulatorFunction)
	{
		int prev, next;
		
		do
		{
			prev = supplier.get();
			next = accumulatorFunction.applyAsInt(prev, x);
		}
		while(!compareAndSetInt(object, fieldOffset, prev, next));
		
		return next;
	}
	
	public static void lazySetBoolean(Object object, long fieldOffset, boolean newValue)
	{
		int v = newValue ? 1 : 0;
		unsafe.putOrderedInt(object, fieldOffset, v);
	}
	
	public static boolean compareAndSetBoolean(Object object, long fieldOffset, boolean expect, boolean update)
	{
		int e = expect ? 1 : 0;
		int u = update ? 1 : 0;
		return unsafe.compareAndSwapInt(object, fieldOffset, e, u);
	}
	
	public static boolean getAndSetBoolean(Object object, long fieldOffset, Supplier<Boolean> supplier, boolean newValue)
	{
		boolean prev;
		
		do
		{
			prev = supplier.get();
		}
		while(!compareAndSetBoolean(object, fieldOffset, prev, newValue));
		
		return prev;
	}
	
	public static void lazySetLong(Object object, long fieldOffset, long newValue)
	{
		unsafe.putOrderedLong(object, fieldOffset, newValue);
	}
	
	public static long getAndSetLong(Object object, long fieldOffset, long newValue)
	{
		return unsafe.getAndSetLong(object, fieldOffset, newValue);
	}
	
	public static boolean compareAndSetLong(Object object, long fieldOffset, long expect, long update)
	{
		return unsafe.compareAndSwapLong(object, fieldOffset, expect, update);
	}
	
	public static boolean weakCompareAndSetLong(Object object, long fieldOffset, long expect, long update)
	{
		return unsafe.compareAndSwapLong(object, fieldOffset, expect, update);
	}
	
	public static long getAndIncrementLong(Object object, long fieldOffset)
	{
		return unsafe.getAndAddLong(object, fieldOffset, 1L);
	}
	
	public static long getAndDecrementLong(Object object, long fieldOffset)
	{
		return unsafe.getAndAddLong(object, fieldOffset, -1L);
	}
	
	public static long getAndAddLong(Object object, long fieldOffset, long delta)
	{
		return unsafe.getAndAddLong(object, fieldOffset, delta);
	}
	
	public static long incrementAndGetLong(Object object, long fieldOffset)
	{
		return unsafe.getAndAddLong(object, fieldOffset, 1L) + 1L;
	}
	
	public static long decrementAndGetLong(Object object, long fieldOffset)
	{
		return unsafe.getAndAddLong(object, fieldOffset, -1L) - 1L;
	}
	
	public static long addAndGetLong(Object object, long fieldOffset, long delta)
	{
		return unsafe.getAndAddLong(object, fieldOffset, delta) + delta;
	}
	
	public static long getAndUpdateLong(Object object, long fieldOffset, Supplier<Long> supplier, LongUnaryOperator updateFunction)
	{
		long prev, next;
		
		do
		{
			prev = supplier.get();
			next = updateFunction.applyAsLong(prev);
		}
		while(!compareAndSetLong(object, fieldOffset, prev, next));
		
		return prev;
	}
	
	public static long updateAndGetLong(Object object, long fieldOffset, Supplier<Long> supplier, LongUnaryOperator updateFunction)
	{
		long prev, next;
		
		do
		{
			prev = supplier.get();
			next = updateFunction.applyAsLong(prev);
		}
		while(!compareAndSetLong(object, fieldOffset, prev, next));
		
		return next;
	}
	
	public static long getAndAccumulate(Object object, long fieldOffset, Supplier<Long> supplier, long x, LongBinaryOperator accumulatorFunction)
	{
		long prev, next;
		
		do
		{
			prev = supplier.get();
			next = accumulatorFunction.applyAsLong(prev, x);
		}
		while(!compareAndSetLong(object, fieldOffset, prev, next));
		
		return prev;
	}
	
	public static long accumulateAndGetLong(Object object, long fieldOffset, Supplier<Long> supplier, long x, LongBinaryOperator accumulatorFunction)
	{
		long prev, next;
		
		do
		{
			prev = supplier.get();
			next = accumulatorFunction.applyAsLong(prev, x);
		}
		while(!compareAndSetLong(object, fieldOffset, prev, next));
		
		return next;
	}
}
