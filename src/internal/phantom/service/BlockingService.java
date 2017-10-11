package phantom.service;

/** A <code>Service</code> that blocks during its {@link phantom.service.Service.Status#RUNNING
 * RUNNING} and {@link phantom.service.Service.Status#INTERRUPTED INTERRUPTED} stages. Specifically,
 * the <code>Thread</code> executing the <code>run()</code> method is suspended during these two
 * stages and later awakened by invocations of the {@link phantom.service.Service#interrupt()
 * interrupt()} and {@link phantom.service.AbstractService#shutdown() shutdown()} methods
 * respectively.
 * 
 * @see Service
 * @see LoopingService
 * @author Brandon D. McKay */
public abstract class BlockingService extends LoopingService
{
	@Override
	protected final void onLoop() { pause(); }

	@Override
	public void interrupt() { super.interrupt(); resume(); }

	@Override
	protected void shutdown() { super.shutdown(); resume(); }
}
