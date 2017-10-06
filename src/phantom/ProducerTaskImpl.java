package phantom;

interface ProducerTaskImpl<O> extends ProducerTask<O>, NonInputTaskImpl, OutputTaskImpl<O>
{
	@Override
	default ProducerTaskImpl<O> unmask() { return this; }
}
