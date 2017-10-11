package phantom.concurrent;

import static phantom.util.VeryUnsafe.compareAndSetObject;
import static phantom.util.VeryUnsafe.objectFieldOffset;
import java.util.function.Supplier;
import phantom.util.Undocumented;

@Undocumented
public class ConcurrentCachedSupplier<T>
{
	private volatile Cache<T> cache;
	private static final long cacheFieldOffset = objectFieldOffset(ConcurrentCachedSupplier.class, "cache");
	
	public ConcurrentCachedSupplier(Supplier<T> supplier)
	{
		this.cache = new EmptyCache<>(supplier);
	}
	
	protected final T getFromCache()
	{
		return cache.get(this);
	}
	
	private T compareAndSetCache(Cache<T> expect, Cache<T> update)
	{
		compareAndSetObject(this, cacheFieldOffset, expect, update);
		return getFromCache();
	}
	
	private interface Cache<T>
	{
		T get(ConcurrentCachedSupplier<T> builder);
	}
	
	private static class EmptyCache<T> implements Cache<T>
	{
		private final Supplier<T> supplier;
		
		public EmptyCache(Supplier<T> supplier)
		{
			this.supplier = supplier;
		}

		@Override
		public T get(ConcurrentCachedSupplier<T> builder)
		{
			T t = supplier.get();
			return builder.compareAndSetCache(this, s -> t);
		}
	}
}
