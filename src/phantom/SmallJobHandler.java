package phantom;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SmallJobHandler implements JobHandler
{
	private final Object sleepMonitor = new Object();
	private volatile boolean stop = false;
	private volatile boolean isCurrentlySleeping = false;
	
	public final ConcurrentLinkedQueue<Job> queue = new ConcurrentLinkedQueue<>();
	
	public SmallJobHandler(int numFailuresBeforeSleep, int sleepTime)
	{
		new Thread(() -> {
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
		}).start();
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
