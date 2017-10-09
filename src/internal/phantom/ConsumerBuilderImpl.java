package phantom;

import util.Constructor;

interface ConsumerBuilderImpl<I> extends ConsumerBuilder<I>, Constructor<ConsumerTaskImpl<I>>
{
	InputTaskImpl<I> construct(NonInputTaskImpl nextTask);

	@Override
	default ConsumerBuilderImpl<I> unmask() { return this; }
	
	@Override
	default ConsumerTask<I> build() { return construct(); }
}
