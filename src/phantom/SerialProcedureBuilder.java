package phantom;

public interface SerialProcedureBuilder extends ProcedureBuilder
{
	default SerialProcedureBuilder then(ProcedureLambda lambda) { return then(lambda.unmask()); }
	default SerialProcedureBuilder then(byte metadata, ProcedureLambda lambda) { return then(lambda.unmask(metadata)); }
	default SerialProcedureBuilder then(ProcedureBuilder builder) { return then(builder.unmask()); }
	SerialProcedureBuilder then(ProcedureBuilderImpl builder);
	
	default <T> SerialProducerBuilder<T> then(ProducerLambda<? extends T> lambda) { return then(lambda.unmask()); }
	default <T> SerialProducerBuilder<T> then(byte metadata, ProducerLambda<? extends T> lambda) { return then(lambda.unmask(metadata)); }
	default <T> SerialProducerBuilder<T> then(ProducerBuilder<? extends T> builder) { return then(builder.unmask()); }
	<T> SerialProducerBuilder<T> then(ProducerBuilderImpl<? extends T> builder);
}
