package phantom;

import util.Builder;
import util.Mask;

public interface ProcedureBuilder extends Mask<ProcedureBuilderImpl>, Builder<ProcedureTask>
{
	default void start() { build().start(); }
}
