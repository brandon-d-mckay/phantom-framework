package phantom;

import util.characteristics.Builder;
import util.characteristics.Mask;

public interface ProducerBuilder<O> extends Mask<ProducerBuilderImpl<O>>, Builder<ProducerTask<O>> {}