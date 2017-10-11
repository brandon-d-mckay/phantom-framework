package phantom.framework;

import phantom.framework.FunctionTaskImpl;
import phantom.util.Proxy;

public interface FunctionTask<I, O> extends Proxy<FunctionTaskImpl<I, O>> {}
