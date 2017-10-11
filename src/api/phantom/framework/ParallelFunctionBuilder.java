package phantom.framework;

import java.util.List;
import phantom.framework.FunctionBuilderImpl;

public interface ParallelFunctionBuilder<I, O> extends FunctionBuilder<I, List<O>>
{
	default ParallelFunctionBuilder<I, O> and(FunctionLambda<? super I, ? extends O> lambda) { return and(lambda.unmask()); }
	default ParallelFunctionBuilder<I, O> and(byte metadata, FunctionLambda<? super I, ? extends O> lambda) { return and(lambda.unmask(metadata)); }
	default ParallelFunctionBuilder<I, O> and(FunctionBuilder<? super I, ? extends O> builder) { return and(builder.unmask()); }
	ParallelFunctionBuilder<I, O> and(FunctionBuilderImpl<? super I, ? extends O> builder);
}
 