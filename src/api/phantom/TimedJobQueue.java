package phantom;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TimedJobQueue implements JobQueue
{
	public final ConcurrentLinkedQueue<Job> queue = new ConcurrentLinkedQueue<>();
	public final int expectedResponseTime;
	public volatile long trackerTimestamp;

	public TimedJobQueue(int expectedResponseTime)
	{
		this.expectedResponseTime = expectedResponseTime;
		updateTrackerTimestamp();
	}
	
	public void updateTrackerTimestamp()
	{
		trackerTimestamp = System.currentTimeMillis();
		if(!queue.isEmpty()) { queue.add(this::updateTrackerTimestamp); }
	}
	
	public int getTimeOverdue()
	{
		return ((int) (System.currentTimeMillis() - trackerTimestamp)) - expectedResponseTime;
	}

	public void add(Job e)
	{
		queue.add(e);
	}

	@Override
	public Job take()
	{
		return queue.poll();
	}
}
