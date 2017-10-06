package phantom;

import util.characteristics.Builder;
import util.characteristics.Mask;

public interface ProcedureBuilder extends Mask<ProcedureBuilderImpl>, Builder<ProcedureTask>
{
	default void start() { build().start(); }
}
