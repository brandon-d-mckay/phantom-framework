package phantom;

import java.util.function.Function;
import java.util.function.Supplier;
import phantom.SerialFunctionBuilderImpl.SerialFunctionTask;
import util.AbstractCachedBuilder;

class SerialConsumerBuilderImpl<I> extends AbstractCachedBuilder<ConsumerTaskImpl<I>> implements SerialConsumerBuilder<I>, ConsumerBuilderImpl<I>
{
	private final Function<NonInputTaskImpl, InputTaskImpl<I>> taskConstructor;
	
	public SerialConsumerBuilderImpl(ConsumerBuilderImpl<I> builder)
	{
		this(builder::construct, builder::construct);
	}
	
	SerialConsumerBuilderImpl(Function<NonInputTaskImpl, InputTaskImpl<I>> taskConstructor, Supplier<ConsumerTaskImpl<I>> leafTaskBuilder)
	{
		super(leafTaskBuilder);
		this.taskConstructor = taskConstructor;
	}
	
	@Override
	public SerialConsumerBuilder<I> then(ProcedureBuilderImpl builder)
	{
		return new SerialConsumerBuilderImpl<>(taskConstructor.compose(builder::construct),
			() -> {
				ProcedureTaskImpl tail = builder.construct();
				return new SerialConsumerTaskImpl<>(taskConstructor.apply(tail), tail);
			}
		);
	}
	
	@Override
	public <T> SerialFunctionBuilder<I, T> then(ProducerBuilderImpl<? extends T> builder)
	{
		return new SerialFunctionBuilderImpl<>(taskConstructor.compose(builder::construct),
			() -> {
				ProducerTaskImpl<? extends T> tail = builder.construct();
				return new SerialFunctionTask<>(taskConstructor.apply(tail), tail);
			}
		);
	}

	@Override
	public InputTaskImpl<I> construct(NonInputTaskImpl nextTask)
	{
		return taskConstructor.apply(nextTask);
	}
	
	@Override
	public ConsumerTaskImpl<I> construct()
	{
		return getFromCache();
	}

	public static class SerialConsumerTaskImpl<I> extends AbstractSerialInputTaskImpl<I> implements ConsumerTaskImpl<I>
	{
		public SerialConsumerTaskImpl(InputTaskImpl<? super I> head, NonOutputTaskImpl tail)
		{
			super(head);
		}
	}
}
