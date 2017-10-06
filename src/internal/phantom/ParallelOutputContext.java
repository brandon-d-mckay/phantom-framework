package phantom;

interface ParallelOutputContext<O> extends ParallelContext
{
	void completeSubtask(O output);
}
