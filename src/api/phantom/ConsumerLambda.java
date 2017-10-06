package phantom;

import static util.Objects.assertNonNull;

@FunctionalInterface
public interface ConsumerLambda<I> extends ConsumerBuilder<I>
{
	void invoke(I input);
	
	default ConsumerLambda<I> pipeRight(ProcedureLambda after) { return (I i) -> { invoke(i); assertNonNull(after).invoke(); }; }
	
	default <T> FunctionLambda<I, T> pipeRight(ProducerLambda<? extends T> after) { return (I i) -> { invoke(i); return assertNonNull(after).invoke(); }; }
	
	default <T> ConsumerLambda<T> pipeLeft(FunctionLambda<? super T, ? extends I> before) { return (T t) -> invoke(assertNonNull(before).invoke(t)); }
	
	default ProcedureLambda pipeLeft(ProducerLambda<? extends I> before) { return () -> invoke(assertNonNull(before).invoke()); }
	
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
