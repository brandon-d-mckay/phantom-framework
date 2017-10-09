package phantom;

import util.Constructor;

interface ProcedureBuilderImpl extends ProcedureBuilder, Constructor<ProcedureTaskImpl>
{
	NonInputTaskImpl construct(NonInputTaskImpl nextTask);

	@Override
	default ProcedureBuilderImpl unmask() { return this; }
	
	@Override
	default ProcedureTask build() { return construct(); }
}
