package phantom.framework;

import phantom.util.Proxy;

public interface ProcedureTask extends Proxy<ProcedureTaskImpl>
{
	void start();
}
 