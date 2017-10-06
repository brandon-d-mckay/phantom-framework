package phantom;

import util.characteristics.Builder;
import util.characteristics.Mask;

public interface FunctionBuilder<I, O> extends Mask<FunctionBuilderImpl<I, O>>, Builder<FunctionTask<I, O>> {}