package phantom;

import util.Builder;
import util.Mask;

public interface ConsumerBuilder<I> extends Mask<ConsumerBuilderImpl<I>>, Builder<ConsumerTask<I>>
{
	default void start(I input) { build().start(input); }
}
