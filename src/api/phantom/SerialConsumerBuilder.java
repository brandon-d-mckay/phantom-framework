package phantom;

import static util.Objects.assertNonNull;

public interface SerialConsumerBuilder<I> extends ConsumerBuilder<I>
{
	default SerialConsumerBuilder<I> then(ProcedureLambda lambda) { return then(assertNonNull(lambda).unmask()); }
	default SerialConsumerBuilder<I> then(byte metadata, ProcedureLambda lambda) { return then(assertNonNull(lambda).unmask(metadata)); }
	default SerialConsumerBuilder<I> then(ProcedureBuilder builder) { return then(assertNonNull(builder).unmask()); }
	SerialConsumerBuilder<I> then(ProcedureBuilderImpl builder);
	
	default <T> SerialFunctionBuilder<I, T> then(ProducerLambda<? extends T> lambda) { return then(assertNonNull(lambda).unmask()); }
	default <T> SerialFunctionBuilder<I, T> then(byte metadata, ProducerLambda<? extends T> lambda) { return then(assertNonNull(lambda).unmask(metadata)); }
	default <T> SerialFunctionBuilder<I, T> then(ProducerBuilder<? extends T> builder) { return then(assertNonNull(builder).unmask()); }
	<T> SerialFunctionBuilder<I, T> then(ProducerBuilderImpl<? extends T> builder);
}
