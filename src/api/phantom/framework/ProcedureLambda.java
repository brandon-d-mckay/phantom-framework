package phantom.framework;

import static phantom.util.Objects.assertNonNull;
import phantom.framework.LeafProcedureBuilderImpl;
import phantom.framework.ProcedureBuilderImpl;

public interface ProcedureLambda extends ProcedureBuilder
{
	void invoke();
	
	default ProcedureLambda pipeRight(ProcedureLambda after) { assertNonNull(after); return () -> { invoke(); after.invoke(); }; }
	
	default <T> ProducerLambda<T> pipeRight(ProducerLambda<? extends T> after) { assertNonNull(after); return () -> { invoke(); return after.invoke(); }; }
	
	default <T1, T2> FunctionLambda<? super T1, ? extends T2> wrapWith(FunctionLambda<? super T1, ? extends T2> wrapper) { return wrapper.wrap(this); }
	
	default <T> ConsumerLambda<T> pipeLeft(ConsumerLambda<? super T> before) { assertNonNull(before); return (T t) -> { before.invoke(t); invoke(); }; }
	
	default ProcedureLambda pipeLeft(ProcedureLambda before) { assertNonNull(before); return () -> { before.invoke(); invoke(); }; }
	
	@Override
	default ProcedureBuilderImpl unmask()
	{
		return unmask(Meta.Default);
	}
	
	default ProcedureBuilderImpl unmask(byte metadata)
	{
		return new LeafProcedureBuilderImpl(this, metadata);
	}
	
	@Override
	default ProcedureTask build()
	{
		return build(Meta.Default);
	}
	
	default ProcedureTask build(byte metadata)
	{
		return unmask(metadata).build();
	}

	default void start(byte metadata)
	{
		unmask(metadata).start();
	}
}
