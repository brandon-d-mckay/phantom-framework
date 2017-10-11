package phantom.framework;

import static phantom.util.VeryUnsafe.decrementAndGetInt;
import static phantom.util.VeryUnsafe.incrementAndGetInt;
import static phantom.util.VeryUnsafe.objectFieldOffset;
import java.util.Arrays;
import java.util.List;
import phantom.concurrent.ShutdownException;
import phantom.service.ServiceThread;

public class ConcurrentPriorityJobHandler implements JobHandler
{
	private final ConcurrentTimedJobQueue[] queues;
	private final WorkerThread[] workerThreads;
	
	private final Object sleepMonitor = new Object();
	private volatile int sleepCount = 0;
	private final long sleepCountFieldOffset = objectFieldOffset(ConcurrentPriorityJobHandler.class, "sleepCount");
	
	private final ShutdownException shutdownException = new ShutdownException();

	public ConcurrentPriorityJobHandler
	(
			int numWorkers,
			int jobBatchSize,
			int numFailuresBeforeSleep,
			long sleepTime,
			int criticalResponseTime,
			int highResponseTime,
			int lowResponseTime,
			int delayedResponseTime
	)
	{
		queues = new ConcurrentTimedJobQueue[] {
			new ConcurrentTimedJobQueue(criticalResponseTime),
			new ConcurrentTimedJobQueue(highResponseTime),
			new ConcurrentTimedJobQueue(lowResponseTime),
			new ConcurrentTimedJobQueue(delayedResponseTime)
		};
		
		workerThreads = new WorkerThread[numWorkers];
		
		for(int i = 0; i < numWorkers; i++)
		{
			(workerThreads[i] = new WorkerThread(jobBatchSize, numFailuresBeforeSleep, sleepTime)).start();
		}
	}
	
	private void sleepCheck(int i)
	{
		if(sleepCount >= i)
		{
			synchronized(sleepMonitor)
			{
				sleepMonitor.notify();
			}
		}
	}
	
	@Override
	public void handle(Job job)
	{
		sleepCheck(workerThreads.length);
		
		queues[Meta.priority(job) >>> 2].add(job);
	}
	
	@Override
	public void handle(Job[] jobs)
	{
		sleepCheck(1);
		
		Arrays.sort(jobs, Meta.PriorityComparator);
		List<Job> list = Arrays.asList(jobs);
		
		for(int start = 0, current = 0, byteValue = Meta.priority(jobs[0]); current <= jobs.length; current++)
		{
			if(current == jobs.length || Meta.priority(jobs[current]) != byteValue)
			{
				queues[byteValue >>> 2].addAll(list.subList(start, current));
				byteValue += 1 << 2;
				start = current;
			}
		}
	}
	
	private int getMostOverdueQueueIndex()
	{
		int mostOverdueQueueIndex = 0;
		int mostTimeOverdue = queues[0].getTimeOverdue();
		
		for(int currentQueueIndex = 1, currentTimeOverdue; currentQueueIndex < 4; currentQueueIndex++)
		{
			currentTimeOverdue = queues[currentQueueIndex].getTimeOverdue();
			
			if(currentTimeOverdue > mostTimeOverdue)
			{
				mostOverdueQueueIndex = currentQueueIndex;
				mostTimeOverdue = currentTimeOverdue;
			}
		}
		
		return mostOverdueQueueIndex;
	}
	
	private class WorkerThread extends ServiceThread
	{
		private final int jobBatchSize;
		private final int numFailuresBeforeSleep;
		private final long sleepTime;
		
		public WorkerThread(int jobBatchSize, int numFailuresBeforeSleep, long sleepTime)
		{
			this.jobBatchSize = jobBatchSize;
			this.numFailuresBeforeSleep = numFailuresBeforeSleep;
			this.sleepTime = sleepTime;
		}

		@Override
		protected void onLoop()
		{
			ConcurrentTimedJobQueue currentQueue = null;
			Job currentJob = null;
			int currentJobNumber = 0;
			int successiveFailures = 0;
			
			while(true)
			{
				try
				{
					while(true)
					{
						currentQueue = queues[getMostOverdueQueueIndex()];
						
						for(currentJobNumber = 0; currentJobNumber < jobBatchSize; currentJobNumber++)
						{
							currentJob = currentQueue.take();
							
							if(currentJob != null)
							{
								successiveFailures = 0;
								currentJob.run();
							}
							else
							{
								successiveFailures++;
								break;
							}
						}
						
						currentQueue.releaseTracker();
						
						if(successiveFailures == numFailuresBeforeSleep)
						{
							synchronized(sleepMonitor)
							{
								incrementAndGetInt(this, sleepCountFieldOffset);
								
								try
								{
									sleepMonitor.wait(sleepTime);
								}
								catch(InterruptedException e)
								{
									Thread.currentThread().interrupt();
								}
								
								decrementAndGetInt(this, sleepCountFieldOffset);
							}
							
							successiveFailures = 0;
						}
					}
				}
				catch(Exception e)
				{
					if(e == shutdownException) break;
					else Phantom.handleException(e);
				}
			}
		}

		@Override
		protected void onInterrupted()
		{
			
		}
	}
}
