package phantom;

import util.characteristics.Constructor;

interface ConsumerBuilderImpl<I> extends ConsumerBuilder<I>, Constructor<ConsumerTaskImpl<I>>
{
	InputTaskImpl<I> construct(NonInputTaskImpl nextTask);

	@Override
	default ConsumerBuilderImpl<I> unmask() { return this; }
	
	@Override
	default ConsumerTask<I> build() { return construct(); }
}
