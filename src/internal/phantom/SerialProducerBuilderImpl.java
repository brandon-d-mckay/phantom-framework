package phantom;

import java.util.function.Function;
import java.util.function.Supplier;
import phantom.SerialProcedureBuilderImpl.SerialProcedureTaskImpl;
import util.AbstractCachedBuilder;

class SerialProducerBuilderImpl<O> extends AbstractCachedBuilder<ProducerTaskImpl<O>> implements SerialProducerBuilder<O>, ProducerBuilderImpl<O>
{
	private final Function<InputTaskImpl<? super O>, NonInputTaskImpl> taskConstructor;
	
	public SerialProducerBuilderImpl(ProducerBuilderImpl<O> builder)
	{
		this(builder::construct, builder::construct);
	}
	
	SerialProducerBuilderImpl(Function<InputTaskImpl<? super O>, NonInputTaskImpl> taskConstructor, Supplier<ProducerTaskImpl<O>> leafTaskBuilder)
	{
		super(leafTaskBuilder);
		this.taskConstructor = taskConstructor;
	}
	
	@Override
	public SerialProcedureBuilder then(ConsumerBuilderImpl<? super O> builder)
	{
		return new SerialProcedureBuilderImpl(taskConstructor.compose(builder::construct),
			() -> {
				ConsumerTaskImpl<? super O> tail = builder.construct();
				return new SerialProcedureTaskImpl(taskConstructor.apply(tail), tail);
			}
		);
	}
	
	@Override
	public <T> SerialProducerBuilder<T> then(FunctionBuilderImpl<? super O, ? extends T> builder)
	{
		return new SerialProducerBuilderImpl<>(taskConstructor.compose(builder::construct),
			() -> {
				FunctionTaskImpl<? super O, ? extends T> tail = builder.construct();
				return new SerialProducerTaskImpl<>(taskConstructor.apply(tail), tail);
			}
		);
	}

	@Override
	public NonInputTaskImpl construct(InputTaskImpl<? super O> nextTask)
	{
		return taskConstructor.apply(nextTask);
	}

	@Override
	public ProducerTaskImpl<O> construct()
	{
		return getFromCache();
	}
	
	static class SerialProducerTaskImpl<O> extends AbstractSerialNonInputTaskImpl implements ProducerTaskImpl<O>
	{
		public SerialProducerTaskImpl(NonInputTaskImpl head, OutputTaskImpl<? extends O> tail)
		{
			super(head);
		}
	}
}
