package phantom.framework;

import phantom.framework.ProducerTaskImpl;
import phantom.util.Proxy;

public interface ProducerTask<O> extends Proxy<ProducerTaskImpl<O>>{}