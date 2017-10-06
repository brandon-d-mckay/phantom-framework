package phantom;

public interface ProducerLambda<O> extends ProducerBuilder<O>
{
	O invoke();
	
	default ProcedureLambda pipeRight(ConsumerLambda<? super O> after) { return () -> after.invoke(invoke()); }
	
	default <T> ProducerLambda<T> pipeRight(FunctionLambda<? super O, ? extends T> after) { return () -> after.invoke(invoke()); }
	
	default <T> FunctionLambda<T, O> pipeLeft(ConsumerLambda<? super T> before) { return (T t) -> { before.invoke(t); return invoke(); }; }
	
	default ProducerLambda<O> pipeLeft(ProcedureLambda before) { return () -> { before.invoke(); return invoke(); }; }
	
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
