package phantom.framework;

import phantom.util.Builder;
import phantom.util.Proxy;

public interface ProcedureBuilder extends Proxy<ProcedureBuilderImpl>, Builder<ProcedureTask>
{
	default void start() { build().start(); }
}
