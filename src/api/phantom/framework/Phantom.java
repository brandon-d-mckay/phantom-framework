package phantom.framework;

import static phantom.concurrent.Runnables.throwRunnable;
import static phantom.concurrent.Runnables.throwRuntime;
import static phantom.util.Objects.assertNonNull;
import java.io.IOException;

public class Phantom
{
	private static JobHandler JOB_HANDLER = job ->
	{
		JobHandler[] durations = new JobHandler[4];
		
		durations[0] = new SmallJobHandler(30, 20);
		durations[1] = new ConcurrentPriorityJobHandler(1, 5, 25, 5000, 200, 500, 1000, 2500);
		durations[2] = new ConcurrentPriorityJobHandler(2, 1, 10, 10000, 3000, 5000, 10000, 15000);
		durations[3] = durations[2];
		
		setJobHandler(j -> durations[Meta.duration(j)].handle(j));
		dispatch(job);
	};
	
	private static JobHandler getJobHandler()
	{
		return JOB_HANDLER;
	}
	
	public static void setJobHandler(JobHandler jobHandler)
	{
		JOB_HANDLER = assertNonNull(jobHandler);
	}
	
	static void dispatch(Job job)
	{
		getJobHandler().handle(job);
	}
	
	static void dispatch(Job[] jobs)
	{
		JOB_HANDLER.handle(jobs);
	}
	
	public static void handleException(Exception e)
	{
		throwRunnable(e);
	}
	
	public static void main(String... args)
	{
		Task.serial(() -> {
			System.out.println("Task 1");
		}).then(() -> {
			String s = "Hello world!";
			System.out.println("Task 2 sends '" + s + "'");
			return s;
		}).then(s -> {
			int i = 7;
			System.out.println("Task 3 received '" + s + "' and sends " + i);
			return i;
		}).then(i -> {
			System.out.println("Task 4 received " + i);
		}).start();
		
		run();
	}
	
	public static void run(JobHandler jobHandler)
	{
		setJobHandler(jobHandler);
		run();
	}
	
	private static void run()
	{
		try { System.in.read(); }
		catch(IOException e) { throwRuntime(e); }
	}
	
	private Phantom() {}
}
