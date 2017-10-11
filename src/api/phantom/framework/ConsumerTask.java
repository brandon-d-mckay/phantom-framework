package phantom.framework;

import phantom.framework.ConsumerTaskImpl;
import phantom.util.Proxy;

public interface ConsumerTask<I> extends Proxy<ConsumerTaskImpl<I>>
{
	void start(I input);
}
