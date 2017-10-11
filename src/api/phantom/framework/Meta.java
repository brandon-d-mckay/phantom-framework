package phantom.framework;

import java.util.Comparator;

public class Meta<T>
{
	public static final byte VeryLongDuration    = 0b0000011;
	public static final byte LongDuration        = 0b0000010;
	public static final byte ModerateDuration    = 0b0000001;
	public static final byte ShortDuration       = 0b0000000;
	
	public static final byte CriticalPriority    = 0b0001100;
	public static final byte HighPriority        = 0b0001000;
	public static final byte LowPriority         = 0b0000100;
	public static final byte DeferredPriority    = 0b0000000;
	
	public static final byte Default = ShortDuration | LowPriority;
	public static final byte ExecuteOnSupplyingThread = Byte.MIN_VALUE;

	public static final byte DurationOnly        = 0b0000011;
	public static final byte PriorityOnly        = 0b0001100;
	
	public static final Comparator<Job> DurationComparator = (job1, job2) -> Integer.compare(job1.getMetadata() & Meta.DurationOnly, job2.getMetadata() & Meta.DurationOnly);
	public static final Comparator<Job> PriorityComparator = (job1, job2) -> Integer.compare(job1.getMetadata() & Meta.PriorityOnly, job2.getMetadata() & Meta.PriorityOnly);
	
	public static int duration(Job job)
	{
		return job.getMetadata() & DurationOnly;
	}
	
	public static int priority(Job job)
	{
		return job.getMetadata() & PriorityOnly;
	}
	
	private Meta() {}
}