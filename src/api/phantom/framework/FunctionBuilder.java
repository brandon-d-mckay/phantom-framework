package phantom.framework;

import phantom.framework.FunctionBuilderImpl;
import phantom.util.Builder;
import phantom.util.Proxy;

public interface FunctionBuilder<I, O> extends Proxy<FunctionBuilderImpl<I, O>>, Builder<FunctionTask<I, O>> {}