package phantom.concurrent;

import phantom.util.Objects;

/** A promise for an <code>Object</code> that will be delivered at some time in the future. Can only be completed once.
 *
 * @param <T> the type of the promised <code>Object</code>
 * @see Awaitable
 * @see Completable */
public class Promise<T> implements Completable
{
	/** The promised <code>Object</code> to be delivered. */
	private volatile T item;
	
	/** Synchronization mechanism for accessing the promised <code>Object</code>. */
	private final Object itemMonitor = new Object();
	
	/** Attempts to deliver the promised <code>Object</code> and returns <code>true</code> if the operation was successful.
	 *
	 * @param object the promised <code>Object</code> */
	public boolean complete(T object)
	{
		Objects.assertNonNull(object);
		
		synchronized(itemMonitor)
		{
			if(isCompleted()) return false;
			
			this.item = object;
			itemMonitor.notifyAll();
			return true;
		}
	}
	
	/** Returns <code>true</code> if the promised <code>Object</code> has been delivered. */
	@Override
	public boolean isCompleted()
	{
		return item != null;
	}
	
	/** Waits until the promised <code>Object</code> has been delivered. */
 	@Override
	public void await() throws InterruptedException
	{
		synchronized(itemMonitor)
		{
			while(!isCompleted())
			{
				itemMonitor.wait();
			}
		}
	}
	
	/** Immediately returns the promised <code>Object</code> or <code>null</code> if it has not yet been delivered. */
	public T peek()
	{
		return item;
	}
	
	/** Waits until the promised <code>Object</code> has been delivered and then returns it. */
	public T get() throws InterruptedException
	{
		await();
		return item;
	}
}
