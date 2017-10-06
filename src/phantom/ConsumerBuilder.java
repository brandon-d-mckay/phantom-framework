package phantom;

import util.characteristics.Builder;
import util.characteristics.Mask;

public interface ConsumerBuilder<I> extends Mask<ConsumerBuilderImpl<I>>, Builder<ConsumerTask<I>>
{
	default void start(I input) { build().start(input); }
}
