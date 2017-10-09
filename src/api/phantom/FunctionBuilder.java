package phantom;

import util.Builder;
import util.Mask;

public interface FunctionBuilder<I, O> extends Mask<FunctionBuilderImpl<I, O>>, Builder<FunctionTask<I, O>> {}