package phantom.util;

import java.lang.reflect.Field;
import java.security.ProtectionDomain;
import java.util.function.BinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import sun.misc.Unsafe;

@Undocumented
@SuppressWarnings("restriction")
public class VeryUnsafe
{
	private static final Unsafe unsafe;
	
	static
	{
		try
		{
			Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			unsafe = (Unsafe) f.get(null);
			f.setAccessible(false);
		}
		catch(NoSuchFieldException | IllegalAccessException e)
		{
			throw new RuntimeException("Error: Could not retrieve Unsafe object.", e);
		}
	}
	
	public static long objectFieldOffset(Class<?> objectClass, String fieldName)
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
	
	/////////////////////////////////////////////////
	// java.util.concurrent.atomic.AtomicReference //
	/////////////////////////////////////////////////
	
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
	
	///////////////////////////////////////////////
	// java.util.concurrent.atomic.AtomicInteger //
	///////////////////////////////////////////////
	
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
	
	////////////////////////////////////////////
	// java.util.concurrent.atomic.AtomicLong //
	////////////////////////////////////////////
	
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
	
	public static long getAndAccumulateLong(Object object, long fieldOffset, Supplier<Long> supplier, long x, LongBinaryOperator accumulatorFunction)
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
	
	///////////////////////////////////////////////
	// java.util.concurrent.atomic.AtomicBoolean //
	///////////////////////////////////////////////
	
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
	
	/////////////////////
	// sun.misc.Unsafe //
	/////////////////////
	
	/** See {@link sun.misc.Unsafe#getInt(Object, long)} */
	public static int getInt(Object o, long offset)
	{
		return unsafe.getInt(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putInt(Object, long, int)} */
	public static void putInt(Object o, long offset, int x)
	{
		unsafe.putInt(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getObject(Object, long)} */
	public static Object getObject(Object o, long offset)
	{
		return unsafe.getObject(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putObject(Object, long, Object)} */
	public static void putObject(Object o, long offset, Object x)
	{
		unsafe.putObject(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getBoolean(Object, long)} */
	public static boolean getBoolean(Object o, long offset)
	{
		return unsafe.getBoolean(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putBoolean(Object, long, boolean)} */
	public static void putBoolean(Object o, long offset, boolean x)
	{
		unsafe.putBoolean(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getByte(Object, long)} */
	public static byte getByte(Object o, long offset)
	{
		return unsafe.getByte(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putByte(Object, long, byte)} */
	public static void putByte(Object o, long offset, byte x)
	{
		unsafe.putByte(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getShort(Object, long)} */
	public static short getShort(Object o, long offset)
	{
		return unsafe.getShort(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putShort(Object, long, short)} */
	public static void putShort(Object o, long offset, short x)
	{
		unsafe.putShort(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getChar(Object, long)} */
	public static char getChar(Object o, long offset)
	{
		return unsafe.getChar(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putChar(Object, long, char)} */
	public static void putChar(Object o, long offset, char x)
	{
		unsafe.putChar(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getLong(Object, long)} */
	public static long getLong(Object o, long offset)
	{
		return unsafe.getLong(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putLong(Object, long, long)} */
	public static void putLong(Object o, long offset, long x)
	{
		unsafe.putLong(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getFloat(Object, long)} */
	public static float getFloat(Object o, long offset)
	{
		return unsafe.getFloat(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putFloat(Object, long, float)} */
	public static void putFloat(Object o, long offset, float x)
	{
		unsafe.putFloat(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getDouble(Object, long)} */
	public static double getDouble(Object o, long offset)
	{
		return unsafe.getDouble(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putDouble(Object, long, double)} */
	public static void putDouble(Object o, long offset, double x)
	{
		unsafe.putDouble(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getInt(Object, int)} */
	@Deprecated
	public static int getInt(Object o, int offset)
	{
		return unsafe.getInt(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putInt(Object, int, int)} */
	@Deprecated
	public static void putInt(Object o, int offset, int x)
	{
		unsafe.putInt(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getObject(Object, int)} */
	@Deprecated
	public static Object getObject(Object o, int offset)
	{
		return unsafe.getObject(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putObject(Object, int, Object)} */
	@Deprecated
	public static void putObject(Object o, int offset, Object x)
	{
		unsafe.putObject(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getBoolean(Object, int)} */
	@Deprecated
	public static boolean getBoolean(Object o, int offset)
	{
		return unsafe.getBoolean(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putBoolean(Object, int, boolean)} */
	@Deprecated
	public static void putBoolean(Object o, int offset, boolean x)
	{
		unsafe.putBoolean(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getByte(Object, int)} */
	@Deprecated
	public static byte getByte(Object o, int offset)
	{
		return unsafe.getByte(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putByte(Object, int, byte)} */
	@Deprecated
	public static void putByte(Object o, int offset, byte x)
	{
		unsafe.putByte(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getShort(Object, int)} */
	@Deprecated
	public static short getShort(Object o, int offset)
	{
		return unsafe.getShort(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putShort(Object, int, short)} */
	@Deprecated
	public static void putShort(Object o, int offset, short x)
	{
		unsafe.putShort(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getChar(Object, int)} */
	@Deprecated
	public static char getChar(Object o, int offset)
	{
		return unsafe.getChar(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putChar(Object, int, char)} */
	@Deprecated
	public static void putChar(Object o, int offset, char x)
	{
		unsafe.putChar(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getLong(Object, int)} */
	@Deprecated
	public static long getLong(Object o, int offset)
	{
		return unsafe.getLong(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putLong(Object, int, long)} */
	@Deprecated
	public static void putLong(Object o, int offset, long x)
	{
		unsafe.putLong(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getFloat(Object, int)} */
	@Deprecated
	public static float getFloat(Object o, int offset)
	{
		return unsafe.getFloat(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putFloat(Object, int, float)} */
	@Deprecated
	public static void putFloat(Object o, int offset, float x)
	{
		unsafe.putFloat(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getDouble(Object, int)} */
	@Deprecated
	public static double getDouble(Object o, int offset)
	{
		return unsafe.getDouble(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putDouble(Object, int, double)} */
	@Deprecated
	public static void putDouble(Object o, int offset, double x)
	{
		unsafe.putDouble(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getByte(long)} */
	public static byte getByte(long address)
	{
		return unsafe.getByte(address);
	}
	
	/** See {@link sun.misc.Unsafe#putByte(long, byte)} */
	public static void putByte(long address, byte x)
	{
		unsafe.putByte(address, x);
	}
	
	/** See {@link sun.misc.Unsafe#getShort(long)} */
	public static short getShort(long address)
	{
		return unsafe.getShort(address);
	}
	
	/** See {@link sun.misc.Unsafe#putShort(long, short)} */
	public static void putShort(long address, short x)
	{
		unsafe.putShort(address, x);
	}
	
	/** See {@link sun.misc.Unsafe#getChar(long)} */
	public static char getChar(long address)
	{
		return unsafe.getChar(address);
	}
	
	/** See {@link sun.misc.Unsafe#putChar(long, char)} */
	public static void putChar(long address, char x)
	{
		unsafe.putChar(address, x);
	}
	
	/** See {@link sun.misc.Unsafe#getInt(long)} */
	public static int getInt(long address)
	{
		return unsafe.getInt(address);
	}
	
	/** See {@link sun.misc.Unsafe#putInt(long, int)} */
	public static void putInt(long address, int x)
	{
		unsafe.putInt(address, x);
	}
	
	/** See {@link sun.misc.Unsafe#getLong(long)} */
	public static long getLong(long address)
	{
		return unsafe.getLong(address);
	}
	
	/** See {@link sun.misc.Unsafe#putLong(long, long)} */
	public static void putLong(long address, long x)
	{
		unsafe.putLong(address, x);
	}
	
	/** See {@link sun.misc.Unsafe#getFloat(long)} */
	public static float getFloat(long address)
	{
		return unsafe.getFloat(address);
	}
	
	/** See {@link sun.misc.Unsafe#putFloat(long, float)} */
	public static void putFloat(long address, float x)
	{
		unsafe.putFloat(address, x);
	}
	
	/** See {@link sun.misc.Unsafe#getDouble(long)} */
	public static double getDouble(long address)
	{
		return unsafe.getDouble(address);
	}
	
	/** See {@link sun.misc.Unsafe#putDouble(long, double)} */
	public static void putDouble(long address, double x)
	{
		unsafe.putDouble(address, x);
	}
	
	/** See {@link sun.misc.Unsafe#getAddress(long)} */
	public static long getAddress(long address)
	{
		return unsafe.getAddress(address);
	}
	
	/** See {@link sun.misc.Unsafe#putAddress(long, long)} */
	public static void putAddress(long address, long x)
	{
		unsafe.putAddress(address, x);
	}
	
	/** See {@link sun.misc.Unsafe#allocateMemory(long)} */
	public static long allocateMemory(long bytes)
	{
		return unsafe.allocateMemory(bytes);
	}
	
	/** See {@link sun.misc.Unsafe#reallocateMemory(long, long)} */
	public static long reallocateMemory(long address, long bytes)
	{
		return unsafe.reallocateMemory(address, bytes);
	}
	
	/** See {@link sun.misc.Unsafe#setMemory(long, long, byte)} */
	public static void setMemory(long address, long bytes, byte value)
	{
		unsafe.setMemory(address, bytes, value);
	}
	
	/** See {@link sun.misc.Unsafe#copyMemory(long, long, long)} */
	public static void copyMemory(long srcAddress, long destAddress, long bytes)
	{
		unsafe.copyMemory(srcAddress, destAddress, bytes);
	}
	
	/** See {@link sun.misc.Unsafe#freeMemory(long)} */
	public static void freeMemory(long address)
	{
		unsafe.freeMemory(address);
	}
	
	/** See {@link sun.misc.Unsafe#fieldOffset(Field)} */
	@Deprecated
	public static int fieldOffset(Field f)
	{
		return unsafe.fieldOffset(f);
	}
	
	/** See {@link sun.misc.Unsafe#staticFieldBase(Class)} */
	@Deprecated
	public static Object staticFieldBase(Class<?> c)
	{
		return unsafe.staticFieldBase(c);
	}
	
	/** See {@link sun.misc.Unsafe#staticFieldOffset(Field)} */
	public static long staticFieldOffset(Field f)
	{
		return unsafe.staticFieldOffset(f);
	}
	
	/** See {@link sun.misc.Unsafe#objectFieldOffset(Field)} */
	public static long objectFieldOffset(Field f)
	{
		return unsafe.objectFieldOffset(f);
	}
	
	/** See {@link sun.misc.Unsafe#staticFieldBase(Field)} */
	public static Object staticFieldBase(Field f)
	{
		return unsafe.staticFieldBase(f);
	}
	
	/** See {@link sun.misc.Unsafe#ensureClassInitialized(Class)} */
	public static void ensureClassInitialized(Class<?> c)
	{
		unsafe.ensureClassInitialized(c);
	}
	
	/** See {@link sun.misc.Unsafe#arrayBaseOffset(Class)} */
	public static int arrayBaseOffset(Class<?> arrayClass)
	{
		return unsafe.arrayBaseOffset(arrayClass);
	}
	
	/** See {@link sun.misc.Unsafe#arrayIndexScale(Class)} */
	public static int arrayIndexScale(Class<?> arrayClass)
	{
		return unsafe.arrayIndexScale(arrayClass);
	}
	
	/** See {@link sun.misc.Unsafe#addressSize()} */
	public static int addressSize()
	{
		return unsafe.addressSize();
	}
	
	/** See {@link sun.misc.Unsafe#pageSize()} */
	public static int pageSize()
	{
		return unsafe.pageSize();
	}
	
	/** See
	 * {@link sun.misc.Unsafe#defineClass(String, byte[], int, int, ClassLoader, ProtectionDomain)} */
	public static Class<?> defineClass(String name, byte[] b, int off, int len, ClassLoader loader, ProtectionDomain protectionDomain)
	{
		return unsafe.defineClass(name, b, off, len, loader, protectionDomain);
	}
	
	/** See {@link sun.misc.Unsafe#allocateInstance(Class)} */
	public static Object allocateInstance(Class<?> cls) throws InstantiationException
	{
		return unsafe.allocateInstance(cls);
	}
	
	/** See {@link sun.misc.Unsafe#monitorEnter(Object)} */
	@Deprecated
	public static void monitorEnter(Object o)
	{
		unsafe.monitorEnter(o);
	}
	
	/** See {@link sun.misc.Unsafe#monitorExit(Object)} */
	@Deprecated
	public static void monitorExit(Object o)
	{
		unsafe.monitorExit(o);
	}
	
	/** See {@link sun.misc.Unsafe#tryMonitorEnter(Object)} */
	@Deprecated
	public static boolean tryMonitorEnter(Object o)
	{
		return unsafe.tryMonitorEnter(o);
	}
	
	/** See {@link sun.misc.Unsafe#throwException(Throwable)} */
	public static void throwException(Throwable ee)
	{
		unsafe.throwException(ee);
	}
	
	/** See {@link sun.misc.Unsafe#getObjectVolatile(Object, long)} */
	public static Object getObjectVolatile(Object o, long offset)
	{
		return unsafe.getObjectVolatile(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putObjectVolatile(Object, long, Object)} */
	public static void putObjectVolatile(Object o, long offset, Object x)
	{
		unsafe.putObjectVolatile(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getIntVolatile(Object, long)} */
	public static int getIntVolatile(Object o, long offset)
	{
		return unsafe.getIntVolatile(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putIntVolatile(Object, long, int)} */
	public static void putIntVolatile(Object o, long offset, int x)
	{
		unsafe.putIntVolatile(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getBooleanVolatile(Object, long)} */
	public static boolean getBooleanVolatile(Object o, long offset)
	{
		return unsafe.getBooleanVolatile(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putBooleanVolatile(Object, long, boolean)} */
	public static void putBooleanVolatile(Object o, long offset, boolean x)
	{
		unsafe.putBooleanVolatile(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getByteVolatile(Object, long)} */
	public static byte getByteVolatile(Object o, long offset)
	{
		return unsafe.getByteVolatile(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putByteVolatile(Object, long, byte)} */
	public static void putByteVolatile(Object o, long offset, byte x)
	{
		unsafe.putByteVolatile(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getShortVolatile(Object, long)} */
	public static short getShortVolatile(Object o, long offset)
	{
		return unsafe.getShortVolatile(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putShortVolatile(Object, long, short)} */
	public static void putShortVolatile(Object o, long offset, short x)
	{
		unsafe.putShortVolatile(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getCharVolatile(Object, long)} */
	public static char getCharVolatile(Object o, long offset)
	{
		return unsafe.getCharVolatile(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putCharVolatile(Object, long, char)} */
	public static void putCharVolatile(Object o, long offset, char x)
	{
		unsafe.putCharVolatile(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getLongVolatile(Object, long)} */
	public static long getLongVolatile(Object o, long offset)
	{
		return unsafe.getLongVolatile(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putLongVolatile(Object, long, long)} */
	public static void putLongVolatile(Object o, long offset, long x)
	{
		unsafe.putLongVolatile(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getFloatVolatile(Object, long)} */
	public static float getFloatVolatile(Object o, long offset)
	{
		return unsafe.getFloatVolatile(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putFloatVolatile(Object, long, float)} */
	public static void putFloatVolatile(Object o, long offset, float x)
	{
		unsafe.putFloatVolatile(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#getDoubleVolatile(Object, long)} */
	public static double getDoubleVolatile(Object o, long offset)
	{
		return unsafe.getDoubleVolatile(o, offset);
	}
	
	/** See {@link sun.misc.Unsafe#putDoubleVolatile(Object, long, double)} */
	public static void putDoubleVolatile(Object o, long offset, double x)
	{
		unsafe.putDoubleVolatile(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#putOrderedObject(Object, long, Object)} */
	public static void putOrderedObject(Object o, long offset, Object x)
	{
		unsafe.putOrderedObject(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#putOrderedInt(Object, long, int)} */
	public static void putOrderedInt(Object o, long offset, int x)
	{
		unsafe.putOrderedInt(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#putOrderedLong(Object, long, long)} */
	public static void putOrderedLong(Object o, long offset, long x)
	{
		unsafe.putOrderedLong(o, offset, x);
	}
	
	/** See {@link sun.misc.Unsafe#unpark(Object)} */
	public static void unpark(Object thread)
	{
		unsafe.unpark(thread);
	}
	
	/** See {@link sun.misc.Unsafe#park(boolean, long)} */
	public static void park(boolean isAbsolute, long time)
	{
		unsafe.park(isAbsolute, time);
	}
	
	/** See {@link sun.misc.Unsafe#getLoadAverage(double[], int)} */
	public static int getLoadAverage(double[] loadavg, int nelems)
	{
		return unsafe.getLoadAverage(loadavg, nelems);
	}
	
	/** See {@link sun.misc.Unsafe#setMemory(Object, long, long, byte)} */
	public static void setMemory(Object o, long offset, long bytes, byte value)
	{
		unsafe.setMemory(o, offset, bytes, value);
	}
	
	/** See {@link sun.misc.Unsafe#copyMemory(Object, long, Object, long, long)} */
	public static void copyMemory(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes)
	{
		unsafe.copyMemory(srcBase, srcOffset, destBase, destOffset, bytes);
	}
	
	/** See {@link sun.misc.Unsafe#shouldBeInitialized(Class<?>)} */
	public static boolean shouldBeInitialized(Class<?> c)
	{
		return unsafe.shouldBeInitialized(c);
	}
	
	/** See {@link sun.misc.Unsafe#defineAnonymousClass(Class<?>, byte[], Object[])} */
	public static Class<?> defineAnonymousClass(Class<?> hostClass, byte[] data, Object[] cpPatches)
	{
		return unsafe.defineAnonymousClass(hostClass, data, cpPatches);
	}
	
	/** See {@link sun.misc.Unsafe#compareAndSwapObject(Object, long, Object, Object)} */
	public static boolean compareAndSwapObject(Object o, long offset, Object expected, Object x)
	{
		return unsafe.compareAndSwapObject(o, offset, expected, x);
	}
	
	/** See {@link sun.misc.Unsafe#compareAndSwapInt(Object, long, int, int)} */
	public static boolean compareAndSwapInt(Object o, long offset, int expected, int x)
	{
		return unsafe.compareAndSwapInt(o, offset, expected, x);
	}
	
	/** See {@link sun.misc.Unsafe#compareAndSwapLong(Object, long, long, long)} */
	public static boolean compareAndSwapLong(Object o, long offset, long expected, long x)
	{
		return unsafe.compareAndSwapLong(o, offset, expected, x);
	}
	
	/** See {@link sun.misc.Unsafe#loadFence()} */
	public static void loadFence()
	{
		unsafe.loadFence();
	}
	
	/** See {@link sun.misc.Unsafe#storeFence()} */
	public static void storeFence()
	{
		unsafe.storeFence();
	}
	
	/** See {@link sun.misc.Unsafe#fullFence()} */
	public static void fullFence()
	{
		unsafe.fullFence();
	}
	
	private VeryUnsafe() {}
}
