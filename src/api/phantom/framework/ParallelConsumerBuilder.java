package phantom.framework;

import phantom.framework.ConsumerBuilderImpl;

public interface ParallelConsumerBuilder<I> extends ConsumerBuilder<I>
{
	default ParallelConsumerBuilder<I> and(ConsumerLambda<? super I> lambda) { return and(lambda.unmask()); }
	default ParallelConsumerBuilder<I> and(byte metadata, ConsumerLambda<? super I> lambda) { return and(lambda.unmask(metadata)); }
	default ParallelConsumerBuilder<I> and(ConsumerBuilder<? super I> builder) { return and(builder.unmask()); }
	ParallelConsumerBuilder<I> and(ConsumerBuilderImpl<? super I> builder);
}
 