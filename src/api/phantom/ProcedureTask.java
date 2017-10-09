package phantom;

import util.Mask;

public interface ProcedureTask extends Mask<ProcedureTaskImpl>
{
	void start();
}
