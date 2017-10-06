package phantom;

interface ParallelOutputTaskContext<O> extends ParallelTaskContext
{
	void completeSubtask(O output);
}
