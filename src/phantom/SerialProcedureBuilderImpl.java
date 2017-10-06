package phantom;

import java.util.function.Function;
import java.util.function.Supplier;
import phantom.SerialProducerBuilderImpl.SerialProducerTask;
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
				return new SerialProcedureTask(taskConstructor.apply(tail), tail);
			}
		);
	}
	
	@Override
	public <T> SerialProducerBuilder<T> then(ProducerBuilderImpl<? extends T> builder)
	{
		return new SerialProducerBuilderImpl<>(taskConstructor.compose(builder::construct),
			() -> {
				ProducerTaskImpl<? extends T> tail = builder.construct();
				return new SerialProducerTask<>(taskConstructor.apply(tail), tail);
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

	public static class SerialProcedureTask extends AbstractSerialNonInputTaskImpl implements ProcedureTaskImpl
	{
		public SerialProcedureTask(NonInputTaskImpl head, NonOutputTaskImpl tail)
		{
			super(head);
		}
	}
}
