package phantom.framework;

import phantom.framework.ConsumerBuilderImpl;
import phantom.framework.FunctionBuilderImpl;

public interface SerialProducerBuilder<O> extends ProducerBuilder<O>
{
	default SerialProcedureBuilder then(ConsumerLambda<? super O> lambda) { return then(lambda.unmask()); }
	default SerialProcedureBuilder then(byte metadata, ConsumerLambda<? super O> lambda) { return then(lambda.unmask(metadata)); }
	default SerialProcedureBuilder then(ConsumerBuilder<? super O> builder) { return then(builder.unmask()); }
	SerialProcedureBuilder then(ConsumerBuilderImpl<? super O> builder);
	
	default <T> SerialProducerBuilder<T> then(FunctionLambda<? super O, ? extends T> lambda) { return then(lambda.unmask()); }
	default <T> SerialProducerBuilder<T> then(byte metadata, FunctionLambda<? super O, ? extends T> lambda) { return then(lambda.unmask(metadata)); }
	default <T> SerialProducerBuilder<T> then(FunctionBuilder<? super O, ? extends T> builder) { return then(builder.unmask()); }
	<T> SerialProducerBuilder<T> then(FunctionBuilderImpl<? super O, ? extends T> builder);
}
 