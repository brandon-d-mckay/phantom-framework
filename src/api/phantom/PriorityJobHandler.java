package phantom;

import java.util.concurrent.atomic.AtomicInteger;

public class PriorityJobHandler implements JobHandler
{
	private final TimedJobQueue[] queues;
	private final WorkerThread[] workerThreads;
	
	private final Object sleepMonitor = new Object();
	private final AtomicInteger sleepCount = new AtomicInteger();

	public PriorityJobHandler(int numWorkers, int jobBatchSize, int numFailuresBeforeSleep, int criticalResponseTime, int highResponseTime, int lowResponseTime, int delayedResponseTime)
	{
		queues = new TimedJobQueue[] {
			new TimedJobQueue(criticalResponseTime),
			new TimedJobQueue(highResponseTime),
			new TimedJobQueue(lowResponseTime),
			new TimedJobQueue(delayedResponseTime)
		};
		
		workerThreads = new WorkerThread[numWorkers];
		
		for(int i = 0; i < numWorkers; i++)
		{
			workerThreads[i] = new WorkerThread(jobBatchSize, numFailuresBeforeSleep);
			new Thread(workerThreads[i]).start();
		}
	}
	
	private int getLatestQueue()
	{
		int latestQueue = 0;
		int latestTimeOverdue = queues[latestQueue].getTimeOverdue();
		
		for(int queue = 1; queue < 4; queue++)
		{
			int timeOverdue = queues[queue].getTimeOverdue();
			
			if(timeOverdue > latestTimeOverdue)
			{
				latestQueue = queue;
				latestTimeOverdue = timeOverdue;
			}
		}
		
		return latestQueue;
	}
	
	@Override
	public void handle(Job job)
	{
		int priority = job.getMetadata() & Meta.PriorityOnly;
		
		if(priority == Meta.CriticalPriority) queues[0].add(job);
		else if(priority == Meta.HighPriority) queues[1].add(job);
		else if(priority == Meta.LowPriority) queues[2].add(job);
		else queues[3].add(job);
		
		if(sleepCount.get() > 0)
		{
			synchronized(sleepMonitor)
			{
				sleepMonitor.notify();
			}
		}
	}
	
	public void shutdown()
	{
		for(WorkerThread workerThread : workerThreads)
		{
			workerThread.stop();
		}
	}
	
	private class WorkerThread implements Runnable
	{
		private volatile boolean stop = false;
		private final int jobBatchSize;
		private final int numFailuresBeforeSleep;
		
		public WorkerThread(int jobBatchSize, int numFailuresBeforeSleep)
		{
			this.jobBatchSize = jobBatchSize;
			this.numFailuresBeforeSleep = numFailuresBeforeSleep;
		}

		@Override
		public void run()
		{
			int queue = getLatestQueue();
			Job job = null;
			int jobNumber = 0;
			int successiveFailures = 0;
			
			for(; !stop; queue = getLatestQueue())
			{
				for(; jobNumber < jobBatchSize; jobNumber++)
				{
					job = queues[queue].take();
					
					if(job == null)
					{
						successiveFailures++;
						break;
					}
					else
					{
						successiveFailures = 0;
						job.run();
					}
				}
				
				if(successiveFailures > numFailuresBeforeSleep)
				{
					synchronized(sleepMonitor)
					{
						sleepCount.incrementAndGet();
						
						try
						{
							sleepMonitor.wait();
						}
						catch(InterruptedException e)
						{
							Thread.currentThread().interrupt();
						}
						
						sleepCount.decrementAndGet();
					}
					
					queues[queue].updateTrackerTimestamp();
					successiveFailures = 0;
				}
			}
		}
		
		public void stop()
		{
			stop = true;
		}
	}
}
