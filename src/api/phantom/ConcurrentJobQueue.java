package phantom;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentJobQueue implements JobQueue
{
	private final ConcurrentLinkedQueue<Job> queue = new ConcurrentLinkedQueue<>();
	
	public void add(Job job)
	{
		queue.add(job);
	}

	public Job take()
	{
		return queue.poll();
	}
}
 