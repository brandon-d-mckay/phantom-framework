package phantom;

interface NonInputTaskImpl extends Task
{
	Job createNewJob();
	
	Job createNewSubParallelJob(Context context);
}
