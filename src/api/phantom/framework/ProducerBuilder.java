package phantom.framework;

import phantom.framework.ProducerBuilderImpl;
import phantom.util.Builder;
import phantom.util.Proxy;

public interface ProducerBuilder<O> extends Proxy<ProducerBuilderImpl<O>>, Builder<ProducerTask<O>> {}