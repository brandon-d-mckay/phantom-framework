package phantom;

interface ProcedureTaskImpl extends ProcedureTask, NonInputTaskImpl, NonOutputTaskImpl
{
	@Override
	default ProcedureTaskImpl unmask() { return this; }
	
	@Override
	default void start() { Phantom.dispatch(createNewJob()); }
}
