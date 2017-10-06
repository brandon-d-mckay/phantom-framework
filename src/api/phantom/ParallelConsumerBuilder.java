package phantom;

import static util.Objects.assertNonNull;

public interface ParallelConsumerBuilder<I> extends ConsumerBuilder<I>
{
	default ParallelConsumerBuilder<I> and(ConsumerLambda<? super I> lambda) { return and(assertNonNull(lambda).unmask()); }
	default ParallelConsumerBuilder<I> and(byte metadata, ConsumerLambda<? super I> lambda) { return and(assertNonNull(lambda).unmask(metadata)); }
	default ParallelConsumerBuilder<I> and(ConsumerBuilder<? super I> builder) { return and(assertNonNull(builder).unmask()); }
	ParallelConsumerBuilder<I> and(ConsumerBuilderImpl<? super I> builder);
}
