package phantom.framework;

import java.util.Arrays;
import java.util.Collection;

public interface JobHandler
{
	void handle(Job job);
	
	default void handle(Job[] jobs) { handle(Arrays.asList(jobs)); }
	
	default void handle(Collection<Job> jobs) { for(Job job : jobs) handle(job); }
}
