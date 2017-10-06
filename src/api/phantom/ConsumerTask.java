package phantom;

import util.characteristics.Mask;

public interface ConsumerTask<I> extends Mask<ConsumerTaskImpl<I>>
{
	void start(I input);
}
