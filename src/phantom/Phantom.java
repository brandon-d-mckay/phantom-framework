package phantom;

import static util.Objects.assertNonNull;

public class Phantom
{
	static
	{
		JobHandler shortQueue = new SmallJobHandler(30, 20);
		JobHandler moderateQueue = new PriorityJobHandler(1, 5, 25, 200, 500, 1000, 2500);
		JobHandler longQueue = new PriorityJobHandler(2, 1, 10, 3000, 5000, 10000, 15000);
		
		JOB_HANDLER = job -> {
			int duration = job.getMetadata() & Meta.DurationOnly;
			
			if(duration == Meta.ShortDuration) shortQueue.handle(job);
			else if(duration == Meta.ModerateDuration) moderateQueue.handle(job);
			else longQueue.handle(job);
		};
	}
	
	private static volatile JobHandler JOB_HANDLER;
	
	static void dispatch(Job job)
	{
		JOB_HANDLER.handle(job);
	}
	
	public static void setJobHandler(JobHandler jobHandler)
	{
		JOB_HANDLER = assertNonNull(jobHandler);
	}
}
