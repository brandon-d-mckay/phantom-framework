package phantom.framework;

import phantom.framework.ProducerTask;

interface ProducerTaskImpl<O> extends ProducerTask<O>, NonInputTaskImpl, OutputTaskImpl<O>
{
	@Override
	default ProducerTaskImpl<O> unmask() { return this; }
}
