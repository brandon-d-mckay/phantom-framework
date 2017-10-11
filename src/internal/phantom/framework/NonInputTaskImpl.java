package phantom.framework;

import phantom.framework.Job;
import phantom.framework.Task;

interface NonInputTaskImpl extends Task
{
	Job createNewJob();
	
	Job createNewSubParallelJob(Context context);
}
