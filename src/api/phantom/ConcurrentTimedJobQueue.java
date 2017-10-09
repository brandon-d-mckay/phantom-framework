package phantom;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import util.VeryUnsafe;

public class ConcurrentTimedJobQueue implements JobQueue
{
	private final ConcurrentLinkedQueue<Job> queue = new ConcurrentLinkedQueue<>();
	private final int expectedResponseTime;
	 
	private final Job trackerUpdateJob = this::updateTracker;
	@SuppressWarnings("unused") private volatile Job trackerUpdateJobHolder = trackerUpdateJob;
	private static final long trackerUpdateJobHolderFieldOffset = VeryUnsafe.getFieldOffset(ConcurrentTimedJobQueue.class, "trackerUpdateJobHolder");
	private volatile long trackerTimestamp = System.nanoTime();

	public ConcurrentTimedJobQueue(int expectedResponseTime)
	{
		this.expectedResponseTime = expectedResponseTime;
	}
	
	private void updateTracker()
	{
		if(queue.isEmpty()) trackerUpdateJobHolder = trackerUpdateJob;
		else queue.add(trackerUpdateJob);
		
		updateTrackerTimestamp();
	}
	
	public void updateTrackerTimestamp()
	{
		trackerTimestamp = System.nanoTime();
	}
	
	public void releaseTracker()
	{
		if(VeryUnsafe.compareAndSetObject(this, trackerUpdateJobHolderFieldOffset, trackerUpdateJob, null)) updateTracker();
	}
	
	public int getTimeOverdue()
	{
		return ((int) (System.nanoTime() - trackerTimestamp)) - expectedResponseTime;
	}
	
	@Override
	public void add(Job job)
	{
		queue.add(job);
	}
	
	@Override
	public void addAll(Job[] jobs)
	{
		addAll(Arrays.asList(jobs));
	}

	@Override
	public void addAll(Collection<Job> jobs)
	{
		queue.addAll(jobs);
	}
	
	@Override
	public Job take()
	{
		return queue.poll();
	}

	public List<Job> takeAll()
	{
		return queue.stream().collect(Collectors.toList());
	}
}
