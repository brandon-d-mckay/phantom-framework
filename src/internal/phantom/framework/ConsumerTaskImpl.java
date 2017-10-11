package phantom.framework;

import phantom.framework.ConsumerTask;
import phantom.framework.Phantom;

interface ConsumerTaskImpl<I> extends ConsumerTask<I>, InputTaskImpl<I>, NonOutputTaskImpl
{
	@Override
	default ConsumerTaskImpl<I> unmask() { return this; }
	
	@Override
	default void start(I input) { Phantom.dispatch(createNewJob(input)); }
}
