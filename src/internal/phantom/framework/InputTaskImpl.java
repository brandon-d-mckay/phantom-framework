package phantom.framework;

import phantom.framework.Job;
import phantom.framework.Task;

interface InputTaskImpl<I> extends Task
{
	Job createNewJob(I input);
	
	Job createNewSubParallelJob(I input, Context context);
}
