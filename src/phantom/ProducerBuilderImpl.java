package phantom;

import util.characteristics.Constructor;

interface ProducerBuilderImpl<O> extends ProducerBuilder<O>, Constructor<ProducerTaskImpl<O>>
{
	NonInputTaskImpl construct(InputTaskImpl<? super O> nextTask);

	@Override
	default ProducerBuilderImpl<O> unmask() { return this; }
	
	@Override
	default ProducerTask<O> build() { return construct(); }
}
