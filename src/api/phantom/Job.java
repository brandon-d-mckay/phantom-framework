package phantom;

public interface Job extends Runnable
{
	default byte getMetadata() { return Meta.Default; }
}
