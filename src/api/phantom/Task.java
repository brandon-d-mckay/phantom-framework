package phantom;

public interface Task
{
	static ProcedureLambda lambda(ProcedureLambda lambda) { return lambda; }
	static <I> ConsumerLambda<I> lambda(ConsumerLambda<I> lambda) { return lambda; }
	static <O> ProducerLambda<O> lambda(ProducerLambda<O> lambda) { return lambda; }
	static <I, O> FunctionLambda<I, O> lambda(FunctionLambda<I, O> lambda) { return lambda; }
	
	static ProcedureBuilder leaf(ProcedureLambda lambda) { return lambda.unmask(); }
	static ProcedureBuilder leaf(byte metadata, ProcedureLambda lambda) { return lambda.unmask(metadata); }
	static <I> ConsumerBuilder<I> leaf(ConsumerLambda<I> lambda) { return lambda.unmask(); }
	static <I> ConsumerBuilder<I> leaf(byte metadata, ConsumerLambda<I> lambda) { return lambda.unmask(metadata); }
	static <O> ProducerBuilder<O> leaf(ProducerLambda<O> lambda) { return lambda.unmask(); }
	static <O> ProducerBuilder<O> leaf(byte metadata, ProducerLambda<O> lambda) { return lambda.unmask(metadata); }
	static <I, O> FunctionBuilder<I, O> leaf(FunctionLambda<I, O> lambda) { return lambda.unmask(); }
	static <I, O> FunctionBuilder<I, O> leaf(byte metadata, FunctionLambda<I, O> lambda) { return lambda.unmask(metadata); }
	
	static SerialProcedureBuilder serial(ProcedureLambda lambda) { return serial(lambda.unmask()); }
	static SerialProcedureBuilder serial(byte metadata, ProcedureLambda lambda) { return serial(lambda.unmask(metadata)); }
	static SerialProcedureBuilder serial(ProcedureBuilder builder) { return serial(builder.unmask()); }
	static SerialProcedureBuilder serial(ProcedureBuilderImpl builder) { return new SerialProcedureBuilderImpl(builder); }
	static <I> SerialConsumerBuilder<I> serial(ConsumerLambda<I> lambda) { return serial(lambda.unmask()); }
	static <I> SerialConsumerBuilder<I> serial(byte metadata, ConsumerLambda<I> lambda) { return serial(lambda.unmask(metadata)); }
	static <I> SerialConsumerBuilder<I> serial(ConsumerBuilder<I> builder) { return serial(builder.unmask()); }
	static <I> SerialConsumerBuilder<I> serial(ConsumerBuilderImpl<I> builder) { return new SerialConsumerBuilderImpl<>(builder); }
	static <O> SerialProducerBuilder<O> serial(ProducerLambda<O> lambda) { return serial(lambda.unmask()); }
	static <O> SerialProducerBuilder<O> serial(byte metadata, ProducerLambda<O> lambda) { return serial(lambda.unmask(metadata)); }
	static <O> SerialProducerBuilder<O> serial(ProducerBuilder<O> builder) { return serial(builder.unmask()); }
	static <O> SerialProducerBuilder<O> serial(ProducerBuilderImpl<O> builder) { return new SerialProducerBuilderImpl<>(builder); }
	static <I, O> SerialFunctionBuilder<I, O> serial(FunctionLambda<I, O> lambda) { return serial(lambda.unmask()); }
	static <I, O> SerialFunctionBuilder<I, O> serial(byte metadata, FunctionLambda<I, O> lambda) { return serial(lambda.unmask(metadata)); }
	static <I, O> SerialFunctionBuilder<I, O> serial(FunctionBuilder<I, O> builder) { return serial(builder.unmask()); }
	static <I, O> SerialFunctionBuilder<I, O> serial(FunctionBuilderImpl<I, O> builder) { return new SerialFunctionBuilderImpl<>(builder); }
	
	static ParallelProcedureBuilder parallel(ProcedureLambda lambda) { return parallel(lambda.unmask()); }
	static ParallelProcedureBuilder parallel(byte metadata, ProcedureLambda lambda) { return parallel(lambda.unmask(metadata)); }
	static ParallelProcedureBuilder parallel(ProcedureBuilder builder) { return parallel(builder.unmask()); }
	static ParallelProcedureBuilder parallel(ProcedureBuilderImpl builder) { return new ParallelProcedureBuilderImpl(builder); }
	static <I> ParallelConsumerBuilder<I> parallel(ConsumerLambda<I> lambda) { return parallel(lambda.unmask()); }
	static <I> ParallelConsumerBuilder<I> parallel(byte metadata, ConsumerLambda<I> lambda) { return parallel(lambda.unmask(metadata)); }
	static <I> ParallelConsumerBuilder<I> parallel(ConsumerBuilder<I> builder) { return parallel(builder.unmask()); }
	static <I> ParallelConsumerBuilder<I> parallel(ConsumerBuilderImpl<I> builder) { return new ParallelConsumerBuilderImpl<>(builder); }
	static <O> ParallelProducerBuilder<O> parallel(ProducerLambda<O> lambda) { return parallel(lambda.unmask()); }
	static <O> ParallelProducerBuilder<O> parallel(byte metadata, ProducerLambda<O> lambda) { return parallel(lambda.unmask(metadata)); }
	static <O> ParallelProducerBuilder<O> parallel(ProducerBuilder<O> builder) { return parallel(builder.unmask()); }
	static <O> ParallelProducerBuilder<O> parallel(ProducerBuilderImpl<O> builder) { return new ParallelProducerBuilderImpl<>(builder); }
	static <I, O> ParallelFunctionBuilder<I, O> parallel(FunctionLambda<I, O> lambda) { return parallel(lambda.unmask()); }
	static <I, O> ParallelFunctionBuilder<I, O> parallel(byte metadata, FunctionLambda<I, O> lambda) { return parallel(lambda.unmask(metadata)); }
	static <I, O> ParallelFunctionBuilder<I, O> parallel(FunctionBuilder<I, O> builder) { return parallel(builder.unmask()); }
	static <I, O> ParallelFunctionBuilder<I, O> parallel(FunctionBuilderImpl<I, O> builder) { return new ParallelFunctionBuilderImpl<>(builder); }
	
	static ProcedureTask make(ProcedureLambda lambda) { return lambda.build(); }
	static ProcedureTask make(byte metadata, ProcedureLambda lambda) { return lambda.unmask(metadata).build(); }
	static <I> ConsumerTask<I> make(ConsumerLambda<I> lambda) { return lambda.build(); }
	static <I> ConsumerTask<I> make(byte metadata, ConsumerLambda<I> lambda) { return lambda.unmask(metadata).build(); }
	static <O> ProducerTask<O> make(ProducerLambda<O> lambda) { return lambda.build(); }
	static <O> ProducerTask<O> make(byte metadata, ProducerLambda<O> lambda) { return lambda.unmask(metadata).build(); }
	static <I, O> FunctionTask<I, O> make(FunctionLambda<I, O> lambda) { return lambda.build(); }
	static <I, O> FunctionTask<I, O> make(byte metadata, FunctionLambda<I, O> lambda) { return lambda.unmask(metadata).build(); }
	
	static void start(ProcedureLambda lambda) { lambda.start(); }
	static void start(byte metadata, ProcedureLambda lambda) { lambda.unmask(metadata).start(); }
	static void start(ProcedureLambda lambda, ProcedureLambda... lambdas) { lambda.start(); for(ProcedureLambda l : lambdas) l.start(); }
	static void start(byte metadata, ProcedureLambda lambda, ProcedureLambda... lambdas) { lambda.start(metadata); for(ProcedureLambda l : lambdas) l.start(metadata); }
	static <I> void start(I input, ConsumerLambda<I> lambda) { lambda.start(input); }
	static <I> void start(byte metadata, I input, ConsumerLambda<I> lambda) { lambda.unmask(metadata).start(input); }
	@SafeVarargs static <I> void start(I input, ConsumerLambda<I> lambda, ConsumerLambda<I>... lambdas) { lambda.start(input); for(ConsumerLambda<I> l : lambdas) l.start(input); }
	@SafeVarargs static <I> void start(byte metadata, I input, ConsumerLambda<I> lambda, ConsumerLambda<I>... lambdas) { lambda.start(metadata, input); for(ConsumerLambda<I> l : lambdas) l.start(metadata, input); }
}
