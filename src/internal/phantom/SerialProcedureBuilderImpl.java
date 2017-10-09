package phantom;

import java.util.function.Function;
import java.util.function.Supplier;
import phantom.SerialProducerBuilderImpl.SerialProducerTaskImpl;
import util.AbstractCachedBuilder;

class SerialProcedureBuilderImpl extends AbstractCachedBuilder<ProcedureTaskImpl> implements SerialProcedureBuilder, ProcedureBuilderImpl
{
	private final Function<NonInputTaskImpl, NonInputTaskImpl> taskConstructor;
	
	public SerialProcedureBuilderImpl(ProcedureBuilderImpl builder)
	{
		this(builder::construct, builder::construct);
	}
	
	SerialProcedureBuilderImpl(Function<NonInputTaskImpl, NonInputTaskImpl> taskConstructor, Supplier<ProcedureTaskImpl> leafTaskBuilder)
	{
		super(leafTaskBuilder);
		this.taskConstructor = taskConstructor;
	}
	 
	@Override
	public SerialProcedureBuilder then(ProcedureBuilderImpl builder)
	{
		return new SerialProcedureBuilderImpl(taskConstructor.compose(builder::construct),
			() -> {
				ProcedureTaskImpl tail = builder.construct();
				return new SerialProcedureTaskImpl(taskConstructor.apply(tail), tail);
			}
		);
	}
	
	@Override
	public <T> SerialProducerBuilder<T> then(ProducerBuilderImpl<? extends T> builder)
	{
		return new SerialProducerBuilderImpl<>(taskConstructor.compose(builder::construct),
			() -> {
				ProducerTaskImpl<? extends T> tail = builder.construct();
				return new SerialProducerTaskImpl<>(taskConstructor.apply(tail), tail);
			}
		);
	}

	@Override
	public NonInputTaskImpl construct(NonInputTaskImpl nextTask)
	{
		return taskConstructor.apply(nextTask);
	}
	
	@Override
	public ProcedureTaskImpl construct()
	{
		return getFromCache();
	}

	static class SerialProcedureTaskImpl extends AbstractSerialNonInputTaskImpl implements ProcedureTaskImpl
	{
		public SerialProcedureTaskImpl(NonInputTaskImpl head, NonOutputTaskImpl tail)
		{
			super(head);
		}
	}
}
