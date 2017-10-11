package phantom.framework;

import java.util.function.Function;
import java.util.function.Supplier;
import phantom.concurrent.ConcurrentCachedSupplier;
import phantom.framework.SerialConsumerBuilder;
import phantom.framework.SerialFunctionBuilder;
import phantom.framework.SerialConsumerBuilderImpl.SerialConsumerTaskImpl;

class SerialFunctionBuilderImpl<I, O> extends ConcurrentCachedSupplier<FunctionTaskImpl<I, O>> implements SerialFunctionBuilder<I, O>, FunctionBuilderImpl<I, O>
{
	private final Function<InputTaskImpl<? super O>, InputTaskImpl<I>> taskConstructor;
	
	public SerialFunctionBuilderImpl(FunctionBuilderImpl<I, O> builder)
	{
		this(builder::construct, builder::construct);
	}
	
	SerialFunctionBuilderImpl(Function<InputTaskImpl<? super O>, InputTaskImpl<I>> taskConstructor, Supplier<FunctionTaskImpl<I, O>> leafTaskBuilder)
	{
		super(leafTaskBuilder);
		this.taskConstructor = taskConstructor;
	}
	
	@Override
	public SerialConsumerBuilder<I> then(ConsumerBuilderImpl<? super O> builder)
	{
		return new SerialConsumerBuilderImpl<>(taskConstructor.compose(builder::construct),
			() -> {
				ConsumerTaskImpl<? super O> tail = builder.construct();
				return new SerialConsumerTaskImpl<>(taskConstructor.apply(tail), tail);
			}
		);
	}
	 
	@Override
	public <T> SerialFunctionBuilder<I, T> then(FunctionBuilderImpl<? super O, ? extends T> builder)
	{
		return new SerialFunctionBuilderImpl<>(taskConstructor.compose(builder::construct),
			() -> {
				FunctionTaskImpl<? super O, ? extends T> tail = builder.construct();
				return new SerialFunctionTaskImpl<>(taskConstructor.apply(tail), tail);
			}
		);
	}

	@Override
	public InputTaskImpl<I> construct(InputTaskImpl<? super O> nextTask)
	{
		return taskConstructor.apply(nextTask);
	}
	
	@Override
	public FunctionTaskImpl<I, O> construct()
	{
		return getFromCache();
	}

	static class SerialFunctionTaskImpl<I, O> extends AbstractSerialInputTaskImpl<I> implements FunctionTaskImpl<I, O>
	{
		public SerialFunctionTaskImpl(InputTaskImpl<? super I> head, OutputTaskImpl<? extends O> tail)
		{
			super(head);
		}
	}
}
