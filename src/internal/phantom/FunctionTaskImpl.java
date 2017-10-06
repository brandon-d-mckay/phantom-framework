package phantom;

interface FunctionTaskImpl<I, O> extends FunctionTask<I, O>, InputTaskImpl<I>, OutputTaskImpl<O>
{
	@Override
	default FunctionTaskImpl<I, O> unmask() { return this; }
}
