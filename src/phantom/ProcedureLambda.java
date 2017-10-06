package phantom;

public interface ProcedureLambda extends ProcedureBuilder
{
	void invoke();
	
	default ProcedureLambda pipeRight(ProcedureLambda after) { return () -> { invoke(); after.invoke(); }; }
	
	default <T> ProducerLambda<T> pipeRight(ProducerLambda<? extends T> after) { return () -> { invoke(); return after.invoke(); }; }
	
	default <T> ConsumerLambda<T> pipeLeft(ConsumerLambda<? super T> before) { return (T t) -> { before.invoke(t); invoke(); }; }
	
	default ProcedureLambda pipeLeft(ProcedureLambda before) { return () -> { before.invoke(); invoke(); }; }
	
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
