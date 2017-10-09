package phantom;

import java.util.Collection;

public interface JobQueue
{
	void add(Job job);
	
	default void addAll(Job[] jobs) { for(Job job : jobs) { add(job); } }
	
	default void addAll(Collection<Job> jobs) { for(Job job : jobs) { add(job); } }
	
	Job take();
}
