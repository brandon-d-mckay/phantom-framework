package phantom.framework;

import static phantom.util.Objects.assertNonNull;
import phantom.framework.LeafProducerBuilderImpl;
import phantom.framework.ProducerBuilderImpl;

public interface ProducerLambda<O> extends ProducerBuilder<O>
{
	O invoke();
	
	default ProcedureLambda pipeRight(ConsumerLambda<? super O> after) { assertNonNull(after); return () -> after.invoke(invoke()); }
	
	default <T> ProducerLambda<T> pipeRight(FunctionLambda<? super O, ? extends T> after) { assertNonNull(after); return () -> after.invoke(invoke()); }
	
	default <T> FunctionLambda<T, O> pipeLeft(ConsumerLambda<? super T> before) { assertNonNull(before); return (T t) -> { before.invoke(t); return invoke(); }; }
	
	default ProducerLambda<O> pipeLeft(ProcedureLambda before) { assertNonNull(before); return () -> { before.invoke(); return invoke(); }; }
	
	@Override
	default ProducerBuilderImpl<O> unmask()
	{
		return unmask(Meta.Default);
	}
	
	default LeafProducerBuilderImpl<O> unmask(byte metadata)
	{
		return new LeafProducerBuilderImpl<>(this, metadata);
	}
	
	@Override
	default ProducerTask<O> build()
	{
		return build(Meta.Default);
	}
	
	default ProducerTask<O> build(byte metadata)
	{
		return unmask(metadata).build();
	}
}
