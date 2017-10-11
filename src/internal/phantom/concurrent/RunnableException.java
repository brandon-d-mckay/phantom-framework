package phantom.concurrent;

/** A <code>RuntimeException</code> that wraps a checked <code>Exception</code> rethrown from a
 * <code>Runnable.run()</code> call. This allows the caller to indirectly catch checked
 * <code>Exception</code>s while being able to differentiate from other unrelated
 * <code>RuntimeException</code>s that are thrown. However, there is still no obligation for the
 * caller to catch the <code>RunnableException</code>, as with all
 * <code>RuntimeException</code>s. This is an alternative to <code>Callable&lt;Void&gt;</code>
 * and also provides legacy support. Example:
 * <p>
 * <blockquote>
 * 
 * <pre>
 * try
 * {
 * 	runnable.run();
 * }
 * catch(RunnableException e)
 * {
 * 	Throwable cause = e.getCause();
 * 	// Handle the Exception
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @author Brandon D. McKay */
public class RunnableException extends RuntimeException
{
	/** Passes the given arguments to the associated <code>RuntimeException</code> super
	 * constructor. */
	public RunnableException(){}
	
	/** Passes the given arguments to the associated <code>RuntimeException</code> super
	 * constructor. */
	public RunnableException(String message)
	{
		super(message);
	}
	
	/** Passes the given arguments to the associated <code>RuntimeException</code> super
	 * constructor. */
	public RunnableException(Throwable cause)
	{
		super(cause);
	}
	
	/** Passes the given arguments to the associated <code>RuntimeException</code> super
	 * constructor. */
	public RunnableException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/** Passes the given arguments to the associated <code>RuntimeException</code> super
	 * constructor. */
	protected RunnableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	/** Serialization ID. */
	private static final long serialVersionUID = -7758112495684397874L;
}