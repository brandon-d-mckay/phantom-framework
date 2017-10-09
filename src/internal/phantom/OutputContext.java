package phantom;

interface OutputContext<O> extends Context
{
	void complete(O output);
}
