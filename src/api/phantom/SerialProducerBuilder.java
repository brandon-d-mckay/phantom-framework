package phantom;

import static util.Objects.assertNonNull;

public interface SerialProducerBuilder<O> extends ProducerBuilder<O>
{
	default SerialProcedureBuilder then(ConsumerLambda<? super O> lambda) { return then(assertNonNull(lambda).unmask()); }
	default SerialProcedureBuilder then(byte metadata, ConsumerLambda<? super O> lambda) { return then(assertNonNull(lambda).unmask(metadata)); }
	default SerialProcedureBuilder then(ConsumerBuilder<? super O> builder) { return then(assertNonNull(builder).unmask()); }
	SerialProcedureBuilder then(ConsumerBuilderImpl<? super O> builder);
	
	default <T> SerialProducerBuilder<T> then(FunctionLambda<? super O, ? extends T> lambda) { return then(assertNonNull(lambda).unmask()); }
	default <T> SerialProducerBuilder<T> then(byte metadata, FunctionLambda<? super O, ? extends T> lambda) { return then(assertNonNull(lambda).unmask(metadata)); }
	default <T> SerialProducerBuilder<T> then(FunctionBuilder<? super O, ? extends T> builder) { return then(assertNonNull(builder).unmask()); }
	<T> SerialProducerBuilder<T> then(FunctionBuilderImpl<? super O, ? extends T> builder);
}
