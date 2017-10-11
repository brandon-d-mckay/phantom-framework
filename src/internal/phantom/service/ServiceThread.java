package phantom.service;

import java.lang.Thread.UncaughtExceptionHandler;
import phantom.util.IDable;
import phantom.util.Nameable;
import phantom.util.Undocumented;

@Undocumented
public abstract class ServiceThread extends LoopingService implements IDable, Nameable
{
	private Thread thread = new Thread(this::run);
	 
	{ thread.setDaemon(true); }
	
	@Override
	public int getID() { return Math.toIntExact(thread.getId()); }
	
	@Override
	public String getName() { return thread.getName(); }
	
	public ServiceThread setName(String name) { thread.setName(name); return this; }
	
	@Override
	public String toString() { return "Service" + thread.toString(); }
	
	public int getPriority() { return thread.getPriority(); }
	
	public ServiceThread setPriority(int newPriority) { thread.setPriority(newPriority); return this; }
	
	public boolean isDaemon() { return thread.isDaemon(); }
	
	public ServiceThread setDaemon(boolean on) { thread.setDaemon(on); return this; }
	
	public UncaughtExceptionHandler getUncaughtExceptionHandler() { return thread.getUncaughtExceptionHandler(); }
	
	public ServiceThread setUncaughtExceptionHandler(UncaughtExceptionHandler eh) { thread.setUncaughtExceptionHandler(eh); return this; }
	
	public ThreadGroup getThreadGroup() { return thread.getThreadGroup(); }
	
	@Override
	public void start() { thread.start(); }
	
	public void join() throws InterruptedException { thread.join(); }
	
	public void join(long millis) throws InterruptedException { thread.join(millis); }
	
	public void join(long millis, int nanos) throws InterruptedException { thread.join(millis, nanos); }
	
	public final void checkAccess() { thread.checkAccess(); }
	
	public StackTraceElement[] getStackTrace() { return thread.getStackTrace(); }
	
	public ClassLoader getContextClassLoader() { return thread.getContextClassLoader(); }
	
	public ServiceThread setContextClassLoader(ClassLoader cl) { thread.setContextClassLoader(cl); return this; }
}
