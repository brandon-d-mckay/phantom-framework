package phantom;

public interface ParallelProcedureBuilder extends ProcedureBuilder
{
	default ParallelProcedureBuilder and(ProcedureLambda lambda) { return and(lambda.unmask()); }
	default ParallelProcedureBuilder and(byte metadata, ProcedureLambda lambda) { return and(lambda.unmask(metadata)); }
	default ParallelProcedureBuilder and(ProcedureBuilder builder) { return and(builder.unmask()); }
	ParallelProcedureBuilder and(ProcedureBuilderImpl builder);
}
 