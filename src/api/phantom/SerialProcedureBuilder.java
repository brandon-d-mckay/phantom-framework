package phantom;

import static util.Objects.assertNonNull;

public interface SerialProcedureBuilder extends ProcedureBuilder
{
	default SerialProcedureBuilder then(ProcedureLambda lambda) { return then(assertNonNull(lambda).unmask()); }
	default SerialProcedureBuilder then(byte metadata, ProcedureLambda lambda) { return then(assertNonNull(lambda).unmask(metadata)); }
	default SerialProcedureBuilder then(ProcedureBuilder builder) { return then(assertNonNull(builder).unmask()); }
	SerialProcedureBuilder then(ProcedureBuilderImpl builder);
	
	default <T> SerialProducerBuilder<T> then(ProducerLambda<? extends T> lambda) { return then(assertNonNull(lambda).unmask()); }
	default <T> SerialProducerBuilder<T> then(byte metadata, ProducerLambda<? extends T> lambda) { return then(assertNonNull(lambda).unmask(metadata)); }
	default <T> SerialProducerBuilder<T> then(ProducerBuilder<? extends T> builder) { return then(assertNonNull(builder).unmask()); }
	<T> SerialProducerBuilder<T> then(ProducerBuilderImpl<? extends T> builder);
}
