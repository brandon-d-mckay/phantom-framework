package phantom.framework;

interface OutputContext<O> extends Context
{
	void complete(O output);
}
