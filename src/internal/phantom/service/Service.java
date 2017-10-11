package phantom.service;

import phantom.concurrent.Completable;
import phantom.concurrent.Interruptible;
import phantom.concurrent.Startable;

/** An executable object that follows an operational life cycle.
 * <p>
 * Abstracts the purpose of <code>Thread.interrupt()</code> away from a runtime construct and
 * instead separates it as a logical responsibility of the class being executed. This is a
 * convenient alternative to implementing a loop within <code>Runnable.run()</code> that iteratively
 * checks the <code>Thread</code>'s interrupt status.
 * <p>
 * The life cycle of a <code>Service</code> instance transitions from one {@link Status Status} to
 * the next until it is terminated, after which it cannot be ran a second time. Each
 * <code>Status</code> is transitioned in the following order:
 * <p>
 * <ul>
 * <li>{@link Status#INITIALIZED INITIALIZED}
 * <li>{@link Status#STARTUP STARTUP}
 * <li>{@link Status#RUNNING RUNNING}
 * <li>{@link Status#INTERRUPTED INTERRUPTED}
 * <li>{@link Status#SHUTDOWN SHUTDOWN}
 * <li>{@link Status#TERMINATED TERMINATED}
 * </ul>
 * <p>
 * The main operational actions of a <code>Service</code> are performed during its
 * <code>RUNNING</code> and <code>INTERRUPTED</code> stages. The <code>Service</code> will remain in
 * the <code>RUNNING</code> stage indefinitely until it is explicitly interrupted by an invocation
 * of its <code>public</code> {@link #interrupt() interrupt()} method. It will then typically remain
 * in the <code>INTERRUPTED</code> stage indefinitely until a <code>protected</code>
 * {@link AbstractService#shutdown() shutdown()} method is internally invoked by an extending class.
 * Every <code>Status</code> is encountered and assumed by the <code>Service</code> at some point
 * during its life regardless of when it is interrupted or shut down. A <code>Service</code>
 * interrupted before reaching its <code>RUNNING</code> stage will perform no main operational
 * actions, but will still trigger all stage transitions.
 * 
 * @see AbstractService
 * @see LoopingService
 * @see BlockingService
 * @author Brandon D. McKay */
public interface Service extends Runnable, Startable, Interruptible, Completable
{
	/** Indicates the current operational stage of a <code>Service</code>. */
	enum Status
	{
		/** The <code>Service</code> has been created but not yet started. */
		INITIALIZED,
		
		/** The <code>Service</code> has been started and is executing startup tasks. */
		STARTUP,
		
		/** The <code>Service</code> is running in its normal operational state. */
		RUNNING,
		
		/** The <code>Service</code> is running in its normal operational state but has been
		 * requested to stop running. */
		INTERRUPTED,
		
		/** The <code>Service</code> is no longer running in its normal operational state and is
		 * executing shutdown tasks. */
		SHUTDOWN,
		
		/** The <code>Service</code> is no longer executing. */
		TERMINATED;
		
		/** Array of the <code>enum</code> values to avoid unnecessary duplication by
		 * <code>enum.values()</code>. */
		private static final Status[] VALUES;
		
		/** Pad the array of <code>enum</code> values with <code>null</code>s for simpler
		 * computation of <code>previous()</code> and <code>next()</code> on the first and last
		 * <code>enum</code> values respectively. */
		static
		{
			Status[] values = values();
			VALUES = new Status[values.length + 2];
			System.arraycopy(values, 0, VALUES, 1, values.length);
		}
		
		/** Returns the <code>Status</code> that chronologically occurs immediately before this
		 * one. */
		public Status previous() { return VALUES[ordinal()]; }
		
		/** Returns the <code>Status</code> that chronologically occurs immediately after this
		 * one. */
		public Status next() { return VALUES[ordinal() + 2]; }
		
		/** Returns <code>true</code> if this <code>Status</code> chronologically occurs some time
		 * before the one specified. */
		public boolean isBefore(Status status) { return compareTo(status) < 0; }
		
		/** Returns <code>true</code> if this <code>Status</code> chronologically occurs some time
		 * before or is equal to the one specified. */
		public boolean isBeforeOrEquals(Status status) { return compareTo(status) <= 0; }
		
		/** Returns <code>true</code> if this <code>Status</code> chronologically occurs some time
		 * after or is equal to the one specified. */
		public boolean isAfterOrEquals(Status status) { return compareTo(status) >= 0; }
		
		/** Returns <code>true</code> if this <code>Status</code> chronologically occurs some time
		 * after the one specified. */
		public boolean isAfter(Status status) { return compareTo(status) > 0; }
	}
	
	/** Returns this <code>Service</code>'s current {@link Status Status}.
	 * 
	 * @see #isCompleted() */
	Status getStatus();
	
	/** Returns <code>true</code> if this <code>Service</code> has reached its
	 * {@link Status#TERMINATED TERMINATED} stage.
	 * 
	 * @see #getStatus() */
	@Override
	boolean isCompleted();
	
	/** Waits until this <code>Service</code> has reached its {@link Status#TERMINATED TERMINATED}
	 * stage.
	 * 
	 * @see #await(Status) */
	@Override
	void await() throws InterruptedException;
	
	/** Waits until this <code>Service</code> has reached the specified {@link Status Status}.
	 *
	 * @param status the <code>Status</code> to wait for
	 * @see #await() */
	void await(Status status) throws InterruptedException;
	
	/** Interrupts this <code>Service</code>. This method <b>must</b> be invoked for this
	 * <code>Service</code> to transition to its {@link Status#INTERRUPTED INTERRUPTED} stage. There
	 * will be no effect if previously called. */
	@Override
	void interrupt();
}
