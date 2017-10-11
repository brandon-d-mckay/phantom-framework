package phantom.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import phantom.util.Objects;

/** Utility class for <code>Runnable</code>s and executable classes.
 * 
 * @see Objects */
public class Runnables
{
	/** Default no-op <code>Runnable</code> instance. */
	public static final Runnable RUNNABLE = () -> {};
	
	/** Default no-op <code>Callable</code> instance. */
	public static final Callable<?> CALLABLE = () -> null;
	
	/** Default new-thread <code>Executor</code> instance. */
	public static final Executor EXECUTOR = Runnables::start;
	
	/** Returns a concatenated <code>Runnable</code> that runs the specified <code>Runnable</code>s
	 * in the order given.
	 *
	 * @param runnables the <code>Runnable</code>s to be joined */
	public static Runnable concatenate(Runnable... runnables)
	{
		Objects.assertNonNullElements(runnables);
		
		return () -> { for(Runnable runnable : runnables) { runnable.run(); } };
	}
	
	/** Returns a concatenated <code>Runnable</code> that runs the specified <code>Runnable</code>s
	 * in the order given.
	 *
	 * @param runnables the <code>Runnable</code>s to be joined */
	public static Runnable concatenate(Iterable<Runnable> runnables)
	{
		Objects.assertNonNullElements(runnables);
		
		return () -> runnables.forEach(Runnable::run);
	}
	
	/** Asynchronously runs the <code>Runnable</code> in a new <code>Thread</code>.
	 * 
	 * @param runnable the <code>Runnable</code> to be ran
	 * @see #start(Runnable, int) */
	public static void start(Runnable runnable)
	{
		new Thread(runnable).start();
	}
	
	/** Asynchronously sleeps for the specified amount of time and then runs the
	 * <code>Runnable</code> in a new <code>Thread</code>.
	 * 
	 * @param runnable the <code>Runnable</code> to be ran
	 * @param milliseconds the time to sleep in milliseconds
	 * @see #start(Runnable) */
	public static void start(Runnable runnable, int milliseconds)
	{
		new Thread(() -> {
			try { Thread.sleep(milliseconds); }
			catch(InterruptedException e) { Thread.currentThread().interrupt(); throwRunnable(e); }
			runnable.run();
		}).start();
	}
	
	/** Rethrows an unhandleable <code>Exception</code> as a <code>RuntimeException</code>.
	 *
	 * @param exception the <code>Exception</code> to rethrow
	 * @see #throwRunnable(Exception) */
	public static void throwRuntime(Exception exception)
	{
		if(exception instanceof RuntimeException) throw (RuntimeException) exception; // No need to wrap if it's already a RuntimeException
		else throw new RuntimeException(exception);
	}
	
	/** Rethrows an unhandleable <code>Throwable</code> as a
	 * {@link execution.Runnables.RunnableException RunnableException}.
	 *
	 * @param exception the <code>Exception</code> to rethrow
	 * @see #throwRuntime(Exception)
	 * @see RunnableException */
	public static void throwRunnable(Exception exception)
	{
		throw new RunnableException(exception);
	}
	
	/** Implemented to easily make runnable lists of <code>Runnable</code>s. */
	public interface RunnableList extends Runnable, List<Runnable>
	{
		@Override
		default public void run() { forEach(Runnable::run); }
	}
	
	/** Implementation of a runnable <code>ArrayList</code>. */
	public static class RunnableArrayList extends ArrayList<Runnable> implements RunnableList
	{
		/** Serialization ID. */
		private static final long serialVersionUID = -5550053227982438332L;
	}
	
	/** Implementation of a runnable <code>CopyOnWriteArrayList</code>. */
	public static class RunnableCopyOnWriteArrayList extends CopyOnWriteArrayList<Runnable> implements RunnableList
	{
		/** Serialization ID. */
		private static final long serialVersionUID = -2482301176387601098L;
	}
	
	private Runnables() {}
}
