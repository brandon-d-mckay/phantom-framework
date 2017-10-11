package phantom.service;

import phantom.service.Service.Status;

/** Base class that implements most of the functionality of a <code>Service</code>. Extending classes must implement
 * their own runtime behavior.
 * 
 * @see Service
 * @see LoopingService
 * @see BlockingService
 * @author Brandon D. McKay */
public abstract class AbstractService implements Service
{
	/** This <code>Service</code>'s current <code>Status</code>. */
	private volatile Status status = Status.INITIALIZED;
	
	/** Synchronization mechanism for changing and awaiting this <code>Service</code>'s <code>Status</code>. */
	private final Object statusMonitor = new Object();
	
	/** Indicates whether the <code>Service</code> has been requested to stop running. */
	volatile boolean interruptFlag = false;
	
	/** Indicates whether the <code>Service</code> has been internally requested to shut down by the extending class. */
	volatile boolean shutdownFlag = false;
	
	@Override
	public final Status getStatus()
	{
		return status;
	}
	
	/** Sets the <code>Status</code> of this <code>Service</code> and returns the previous value.
	 *
	 * @param status the new <code>Status</code> */
	protected final void setStatus(Status status)
	{
		synchronized(statusMonitor)
		{
			if(this.status.next() != status) throw new IllegalStateException("This Status has already been reached.");
			this.status = status;
			statusMonitor.notifyAll();
		}
	}
	
	@Override
	public final boolean isCompleted()
	{
		return status.compareTo(Status.TERMINATED) >= 0;
	}
	
	@Override
	public final void await() throws InterruptedException
	{
		await(Status.TERMINATED);
	}
	
	@Override
	public final void await(Status status) throws InterruptedException
	{
		synchronized(statusMonitor)
		{
			while(this.status.isBefore(status)) statusMonitor.wait();
		}
	}
	
	@Override
	public void interrupt()
	{
		interruptFlag = true;
	}
	 
	/** Shuts down this <code>Service</code>. This method <b>must</b> be invoked for this
	 * <code>Service</code> to transition to its {@link Status#SHUTDOWN SHUTDOWN} stage. There will
	 * be no effect if previously called.
	 * 
	 * @see #interrupt() */
	protected void shutdown()
	{
		shutdownFlag = interruptFlag = true;
	}
	
	protected void onStartup() {}
	
	protected abstract void onInterrupted();
	
	protected void onShutdown() {}
}
