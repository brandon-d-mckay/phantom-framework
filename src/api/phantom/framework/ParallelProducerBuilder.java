package phantom.framework;

import java.util.List;
import phantom.framework.ProducerBuilderImpl;

public interface ParallelProducerBuilder<O> extends ProducerBuilder<List<O>>
{
	default ParallelProducerBuilder<O> and(ProducerLambda<? extends O> lambda) { return and(lambda.unmask()); }
	default ParallelProducerBuilder<O> and(byte metadata, ProducerLambda<? extends O> lambda) { return and(lambda.unmask(metadata)); }
	default ParallelProducerBuilder<O> and(ProducerBuilder<? extends O> builder) { return and(builder.unmask()); }
	ParallelProducerBuilder<O> and(ProducerBuilderImpl<? extends O> builder);
}
 