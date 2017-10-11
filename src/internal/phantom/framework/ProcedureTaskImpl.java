package phantom.framework;

import phantom.framework.Phantom;
import phantom.framework.ProcedureTask;

interface ProcedureTaskImpl extends ProcedureTask, NonInputTaskImpl, NonOutputTaskImpl
{
	@Override
	default ProcedureTaskImpl unmask() { return this; }
	
	@Override
	default void start() { Phantom.dispatch(createNewJob()); }
}
