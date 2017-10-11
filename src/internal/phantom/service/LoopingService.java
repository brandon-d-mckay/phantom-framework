package phantom.service;

/** A <code>Service</code> that continuously loops an action during its main operational state.
 * 
 * @see Service
 * @see BlockingService
 * @author Brandon D. McKay */
public abstract class LoopingService extends AbstractService
{
	/** Synchronization mechanism for notifying this <code>Service</code> of a pause() or resume() invocation. */
	final Object pauseMonitor = new Object();
	
	/** Indicates whether the <code>Service</code> should currently be paused or not. */
	private volatile boolean pauseFlag = false;
	
	/** Pauses this <code>Service</code>. It will finish performing any loop action that has already
	 * started, and then block until the {@link #resume() resume()} method is invoked. This method
	 * has no effect if the <code>Service</code> is currently paused. */
	public final void pause()
	{
		pauseFlag = true;
	}
	
	/** Unpauses this <code>Service</code> so that it may continue executing. This method has no
	 * effect if the <code>Service</code> is not currently paused.
	 * 
	 * @see #pause() */
	public final void resume()
	{
		synchronized(pauseMonitor)
		{
			pauseFlag = false;
			pauseMonitor.notify();
		}
	}
	
	@Override
	public void interrupt()
	{
		super.interrupt();
	}
	 
	@Override
	protected void shutdown()
	{
		super.shutdown();
	}
	
	@Override
	public final void run()
	{
		setStatus(Status.STARTUP);
		onStartup();
		setStatus(Status.RUNNING);
		
		while(!interruptFlag)
		{
			onPause();
			
			while(!pauseFlag) { onLoop(); }
		}
		
		setStatus(Status.INTERRUPTED);
		onInterrupted();
		
		while(!shutdownFlag)
		{
			onPause();
			
			while(!pauseFlag) { onLoop(); }
		}
		
		setStatus(Status.SHUTDOWN);
		onShutdown();
		setStatus(Status.TERMINATED);
	}
	
	private void onPause()
	{
		synchronized(pauseMonitor)
		{
			while(pauseFlag)
			{
				try { pauseMonitor.wait(); }
				catch(InterruptedException e) { Thread.currentThread().interrupt(); }
			}
		}
	}
	
	protected abstract void onLoop();
}
