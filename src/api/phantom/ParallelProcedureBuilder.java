package phantom;

import static util.Objects.assertNonNull;

public interface ParallelProcedureBuilder extends ProcedureBuilder
{
	default ParallelProcedureBuilder and(ProcedureLambda lambda) { return and(assertNonNull(lambda).unmask()); }
	default ParallelProcedureBuilder and(byte metadata, ProcedureLambda lambda) { return and(assertNonNull(lambda).unmask(metadata)); }
	default ParallelProcedureBuilder and(ProcedureBuilder builder) { return and(assertNonNull(builder).unmask()); }
	ParallelProcedureBuilder and(ProcedureBuilderImpl builder);
}
