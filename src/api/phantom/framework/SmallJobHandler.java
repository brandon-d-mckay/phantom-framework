package phantom.framework;

import java.util.concurrent.ConcurrentLinkedQueue;
import phantom.service.ServiceThread;

public class SmallJobHandler implements JobHandler
{
	private final Object sleepMonitor = new Object();
	private volatile boolean stop = false;
	private volatile boolean isCurrentlySleeping = false;
	
	private final ConcurrentLinkedQueue<Job> queue = new ConcurrentLinkedQueue<>();
	
	public SmallJobHandler(int numFailuresBeforeSleep, int sleepTime)
	{
		new WorkerThread(numFailuresBeforeSleep, sleepTime).start();
	}
	
	private class WorkerThread extends ServiceThread
	{
		private final int numFailuresBeforeSleep;
		private final long sleepTime;

		public WorkerThread(int numFailuresBeforeSleep, long sleepTime)
		{
			this.numFailuresBeforeSleep = numFailuresBeforeSleep;
			this.sleepTime = sleepTime;
		}

		@Override
		protected void onLoop()
		{
			int successiveFailures = 0;
			
			while(!stop)
			{
				Job job = queue.poll();
				
				if(job != null)
				{
					successiveFailures = 0;
					job.run();
				}
				else
				{
					if(++successiveFailures == numFailuresBeforeSleep)
					{
						synchronized(sleepMonitor)
						{
							try
							{
								while(queue.isEmpty())
								{
									sleepMonitor.wait(sleepTime);
								}
							}
							catch(InterruptedException e)
							{
								Thread.currentThread().interrupt();
							}
						}
					}
				}
			}
		}
		
		@Override
		protected void onInterrupted()
		{}
	}
	
	@Override
	public void handle(Job job)
	{
		queue.add(job);
		
		if(isCurrentlySleeping)
		{
			synchronized(sleepMonitor)
			{
				if(isCurrentlySleeping)
				{
					sleepMonitor.notify();
				}
			}
		}
	}
}
