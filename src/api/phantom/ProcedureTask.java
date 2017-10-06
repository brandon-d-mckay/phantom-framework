package phantom;

import util.characteristics.Mask;

public interface ProcedureTask extends Mask<ProcedureTaskImpl>
{
	void start();
}
