package phantom;

import static util.Objects.assertNonNull;

public class Phantom
{
	static
	{
		JobHandler[] durations = new JobHandler[4];
		
		durations[0] = new SmallJobHandler(30, 20);
		durations[1] = new ConcurrentPriorityJobHandler(1, 5, 25, 5000, 200, 500, 1000, 2500);
		durations[2] = new ConcurrentPriorityJobHandler(2, 1, 10, 10000, 3000, 5000, 10000, 15000);
		durations[3] = durations[2];
		
		JOB_HANDLER = job -> durations[Meta.duration(job)].handle(job);
	}
	
	private static volatile JobHandler JOB_HANDLER;
	
	static void dispatch(Job job)
	{
		JOB_HANDLER.handle(job);
	}
	
	static void dispatch(Job[] jobs)
	{
		JOB_HANDLER.handle(jobs);
	}
	
	public static void setJobHandler(JobHandler jobHandler)
	{
		JOB_HANDLER = assertNonNull(jobHandler);
	}
}
