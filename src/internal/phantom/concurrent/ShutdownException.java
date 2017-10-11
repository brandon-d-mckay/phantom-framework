package phantom.concurrent;

/** A <code>RuntimeException</code> thrown to interrupt some activity further up the call stack.
 * 
 * @author Brandon D. McKay */
public class ShutdownException extends RuntimeException
{
	/** Passes the given arguments to the associated <code>RuntimeException</code> super constructor. */
	public ShutdownException(){}
	
	/** Passes the given arguments to the associated <code>RuntimeException</code> super constructor. */
	public ShutdownException(String message)
	{
		super(message);
	}
	
	/** Passes the given arguments to the associated <code>RuntimeException</code> super constructor. */
	public ShutdownException(Throwable cause)
	{
		super(cause);
	}
	
	/** Passes the given arguments to the associated <code>RuntimeException</code> super constructor. */
	public ShutdownException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/** Passes the given arguments to the associated <code>RuntimeException</code> super constructor. */
	protected ShutdownException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	/** Serialization ID. */
	private static final long serialVersionUID = -1685596536034483377L;
}