package phantom;

interface InputTaskImpl<I> extends Task
{
	Job createNewJob(I input);
	
	Job createNewSubParallelJob(I input, ParallelTaskContext context);
}
