package phantom;

@FunctionalInterface
public interface FunctionLambda<I, O> extends FunctionBuilder<I, O>
{
	O invoke(I input);
	
	default ConsumerLambda<I> pipeRight(ConsumerLambda<? super O> after) { return (I i) -> after.invoke(invoke(i)); }
	
	default <T> FunctionLambda<I, T> pipeRight(FunctionLambda<? super O, ? extends T> after) { return (I i) -> after.invoke(invoke(i)); }
	
	default <T> FunctionLambda<T, O> pipeLeft(FunctionLambda<? super T, ? extends I> before) { return (T t) -> invoke(before.invoke(t)); }
	
	default ProducerLambda<O> pipeLeft(ProducerLambda<? extends I> before) { return () -> invoke(before.invoke()); }
	
	@Override
	default FunctionBuilderImpl<I, O> unmask()
	{
		return unmask(Meta.Default);
	}
	
	default FunctionBuilderImpl<I, O> unmask(byte metadata)
	{
		return new LeafFunctionBuilderImpl<>(this, metadata);
	}
	
	@Override
	default FunctionTask<I, O> build()
	{
		return build(Meta.Default);
	}
	
	default FunctionTask<I, O> build(byte metadata)
	{
		return unmask(metadata).build();
	}
}
