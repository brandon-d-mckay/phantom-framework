package phantom.framework;

import static phantom.util.Objects.assertNonNull;
import phantom.framework.FunctionBuilderImpl;
import phantom.framework.LeafFunctionBuilderImpl;

@FunctionalInterface
public interface FunctionLambda<I, O> extends FunctionBuilder<I, O>
{
	O invoke(I input);
	 
	default ConsumerLambda<I> pipeRight(ConsumerLambda<? super O> after) { assertNonNull(after); return (I i) -> after.invoke(invoke(i)); }
	
	default <T> FunctionLambda<I, T> pipeRight(FunctionLambda<? super O, ? extends T> after) { assertNonNull(after); return (I i) -> after.invoke(invoke(i)); }
	
	default FunctionLambda<I, O> wrap(ProcedureLambda wrapped) { assertNonNull(wrapped); return (I i) -> { wrapped.invoke(); return invoke(i); }; }
	
	default <T> FunctionLambda<T, O> pipeLeft(FunctionLambda<? super T, ? extends I> before) { assertNonNull(before); return (T t) -> invoke(before.invoke(t)); }
	
	default ProducerLambda<O> pipeLeft(ProducerLambda<? extends I> before) { assertNonNull(before); return () -> invoke(before.invoke()); }
	
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
