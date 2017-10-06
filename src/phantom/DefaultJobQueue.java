package phantom;

import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultJobQueue implements JobQueue
{
	private final ConcurrentLinkedQueue<Job> queue = new ConcurrentLinkedQueue<>();
	
	@Override
	public void add(Job job)
	{
		queue.add(job);
	}

	@Override
	public Job take()
	{
		return queue.poll();
	}
}
