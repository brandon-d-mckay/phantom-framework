package phantom;

import util.Constructor;

interface FunctionBuilderImpl<I, O> extends FunctionBuilder<I, O>, Constructor<FunctionTaskImpl<I, O>>
{
	InputTaskImpl<I> construct(InputTaskImpl<? super O> nextTask);

	@Override
	default FunctionBuilderImpl<I, O> unmask() { return this; }
	
	@Override
	default FunctionTask<I, O> build() { return construct(); }
}
