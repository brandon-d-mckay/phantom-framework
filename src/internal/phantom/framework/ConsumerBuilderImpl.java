package phantom.framework;

import phantom.framework.ConsumerBuilder;
import phantom.framework.ConsumerTask;
import phantom.util.Constructor;

interface ConsumerBuilderImpl<I> extends ConsumerBuilder<I>, Constructor<ConsumerTaskImpl<I>>
{
	InputTaskImpl<I> construct(NonInputTaskImpl nextTask);

	@Override
	default ConsumerBuilderImpl<I> unmask() { return this; }
	
	@Override
	default ConsumerTask<I> build() { return construct(); }
}
