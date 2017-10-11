package phantom.framework;

import phantom.framework.ConsumerBuilderImpl;
import phantom.util.Builder;
import phantom.util.Proxy;

public interface ConsumerBuilder<I> extends Proxy<ConsumerBuilderImpl<I>>, Builder<ConsumerTask<I>>
{
	default void start(I input) { build().start(input); }
}
