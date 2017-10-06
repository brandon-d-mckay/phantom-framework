package phantom;

public interface JobQueue
{
	void add(Job job);
	
	Job take();
}
