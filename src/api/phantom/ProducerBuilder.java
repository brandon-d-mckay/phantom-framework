package phantom;

import util.Builder;
import util.Mask;

public interface ProducerBuilder<O> extends Mask<ProducerBuilderImpl<O>>, Builder<ProducerTask<O>> {}