package phantom;

import static util.Objects.assertNonNull;

public interface ParallelFunctionBuilder<I, O> extends FunctionBuilder<I, O[]>
{
	default ParallelFunctionBuilder<I, O> and(FunctionLambda<? super I, ? extends O> lambda) { return and(assertNonNull(lambda).unmask()); }
	default ParallelFunctionBuilder<I, O> and(byte metadata, FunctionLambda<? super I, ? extends O> lambda) { return and(assertNonNull(lambda).unmask(metadata)); }
	default ParallelFunctionBuilder<I, O> and(FunctionBuilder<? super I, ? extends O> builder) { return and(assertNonNull(builder).unmask()); }
	ParallelFunctionBuilder<I, O> and(FunctionBuilderImpl<? super I, ? extends O> builder);
}
