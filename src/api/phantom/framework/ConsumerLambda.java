package phantom.framework;

import static phantom.util.Objects.assertNonNull;
import phantom.framework.ConsumerBuilderImpl;
import phantom.framework.LeafConsumerBuilderImpl;

@FunctionalInterface
public interface ConsumerLambda<I> extends ConsumerBuilder<I>
{
	void invoke(I input);
	
	default ConsumerLambda<I> pipeRight(ProcedureLambda after) { assertNonNull(after); return (I i) -> { invoke(i); after.invoke(); }; }
	
	default <T> FunctionLambda<I, T> pipeRight(ProducerLambda<? extends T> after) { assertNonNull(after); return (I i) -> { invoke(i); return after.invoke(); }; }
	
	default <T> ConsumerLambda<T> pipeLeft(FunctionLambda<? super T, ? extends I> before) { assertNonNull(before); return (T t) -> invoke(before.invoke(t)); }
	
	default ProcedureLambda pipeLeft(ProducerLambda<? extends I> before) { assertNonNull(before); return () -> invoke(before.invoke()); }
	
	@Override
	default ConsumerBuilderImpl<I> unmask()
	{
		return unmask(Meta.Default);
	}
	
	default ConsumerBuilderImpl<I> unmask(byte metadata)
	{
		return new LeafConsumerBuilderImpl<>(this, metadata);
	}
	
	@Override
	default ConsumerTask<I> build()
	{
		return build(Meta.Default);
	}
	
	default ConsumerTask<I> build(byte metadata)
	{
		return unmask(metadata).build();
	}

	default void start(byte metadata, I input)
	{
		unmask(metadata).start(input);
	}
}
