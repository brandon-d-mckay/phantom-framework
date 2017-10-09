package util;

import java.util.function.Supplier;

public abstract class AbstractCachedBuilder<T>
{
	private volatile Cache<T> cache;
	
	public AbstractCachedBuilder(Supplier<T> supplier)
	{
		this.cache = new EmptyCache<>(supplier);
	}
	
	protected final T getFromCache()
	{
		return cache.get(this);
	}
	
	private void compareAndSwapCache(Cache<T> expect, Cache<T> update)
	{
		VeryUnsafe.compareAndSetObject(this, VeryUnsafe.getFieldOffset(AbstractCachedBuilder.class, "cache"), expect, update);
	}
	
	private interface Cache<T>
	{
		T get(AbstractCachedBuilder<T> builder);
	}
	
	private static class EmptyCache<T> implements Cache<T>
	{
		private final Supplier<T> supplier;
		
		public EmptyCache(Supplier<T> supplier)
		{
			this.supplier = supplier;
		}

		@Override
		public T get(AbstractCachedBuilder<T> builder)
		{
			T t = supplier.get();
			builder.compareAndSwapCache(this, b -> t);
			return builder.getFromCache();
		}
	}
}
