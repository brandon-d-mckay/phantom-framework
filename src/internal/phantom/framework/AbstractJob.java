package phantom.framework;

import phantom.framework.Job;

abstract class AbstractJob implements Job
{
	private final byte metadata;
	
	public AbstractJob(byte metadata)
	{
		this.metadata = metadata;
	}

	@Override
	public byte getMetadata()
	{
		return metadata;
	}
}
