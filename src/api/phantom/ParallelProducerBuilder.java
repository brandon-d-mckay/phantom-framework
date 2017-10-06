package phantom;

import static util.Objects.assertNonNull;

public interface ParallelProducerBuilder<O> extends ProducerBuilder<O[]>
{
	default ParallelProducerBuilder<O> and(ProducerLambda<? extends O> lambda) { return and(assertNonNull(lambda).unmask()); }
	default ParallelProducerBuilder<O> and(byte metadata, ProducerLambda<? extends O> lambda) { return and(assertNonNull(lambda).unmask(metadata)); }
	default ParallelProducerBuilder<O> and(ProducerBuilder<? extends O> builder) { return and(assertNonNull(builder).unmask()); }
	ParallelProducerBuilder<O> and(ProducerBuilderImpl<? extends O> builder);
}
