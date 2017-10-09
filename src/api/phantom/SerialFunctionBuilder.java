package phantom;

public interface SerialFunctionBuilder<I, O> extends FunctionBuilder<I, O>
{
	default SerialConsumerBuilder<I> then(ConsumerLambda<? super O> lambda) { return then(lambda.unmask()); }
	default SerialConsumerBuilder<I> then(byte metadata, ConsumerLambda<? super O> lambda) { return then(lambda.unmask(metadata)); }
	default SerialConsumerBuilder<I> then(ConsumerBuilder<? super O> builder) { return then(builder.unmask()); }
	SerialConsumerBuilder<I> then(ConsumerBuilderImpl<? super O> builder);
	 
	default <T> SerialFunctionBuilder<I, T> then(FunctionLambda<? super O, ? extends T> lambda) { return then(lambda.unmask()); }
	default <T> SerialFunctionBuilder<I, T> then(byte metadata, FunctionLambda<? super O, ? extends T> lambda) { return then(lambda.unmask(metadata)); }
	default <T> SerialFunctionBuilder<I, T> then(FunctionBuilder<? super O, ? extends T> builder) { return then(builder.unmask()); }
	<T> SerialFunctionBuilder<I, T> then(FunctionBuilderImpl<? super O, ? extends T> builder);
}
 