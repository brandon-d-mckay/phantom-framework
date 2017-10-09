package phantom;

import java.util.function.IntFunction;
import util.AbstractCachedBuilder;

class ParallelProcedureBuilderImpl extends AbstractCachedBuilder<ProcedureTaskImpl> implements ParallelProcedureBuilder, ProcedureBuilderImpl
{
	private final IntFunction<ProcedureTaskImpl[]> subtasksCollector;
	
	public ParallelProcedureBuilderImpl(ProcedureBuilderImpl builder)
	{
		this(
			subtaskIndex -> {
				ProcedureTaskImpl[] subtasks = new ProcedureTaskImpl[1 + subtaskIndex];
				subtasks[subtaskIndex] = builder.construct();
				return subtasks;
			}
		);
	}
	
	private ParallelProcedureBuilderImpl(IntFunction<ProcedureTaskImpl[]> subtasksCollector)
	{
		super(() -> new ParallelProcedureTaskImpl(subtasksCollector.apply(0), null));
		this.subtasksCollector = subtasksCollector;
	}
	
	@Override
	public ParallelProcedureBuilderImpl and(ProcedureBuilderImpl builder)
	{
		return new ParallelProcedureBuilderImpl(
			subtaskIndex -> {
				ProcedureTaskImpl[] subtasks = subtasksCollector.apply(1 + subtaskIndex);
				subtasks[subtaskIndex] = builder.construct();
				return subtasks;
			}
		);
	}
 
	@Override
	public NonInputTaskImpl construct(NonInputTaskImpl nextTask)
	{
		return new ParallelProcedureTaskImpl(subtasksCollector.apply(0), nextTask);
	}
	
	@Override
	public ProcedureTaskImpl construct()
	{
		return getFromCache();
	}
	
	private static class ParallelProcedureTaskImpl extends AbstractParallelNonOutputTaskImpl implements ProcedureTaskImpl
	{
		private final ProcedureTaskImpl[] subtasks;
		
		public ParallelProcedureTaskImpl(ProcedureTaskImpl[] subtasks, NonInputTaskImpl nextTask)
		{
			super(subtasks.length, nextTask);
			this.subtasks = subtasks;
		}
		
		@Override
		public Job createNewJob()
		{
			return new ParallelProcedureJob(new ParallelNonOutputContext());
		}
		
		@Override
		public Job createNewSubParallelJob(Context context)
		{
			return new ParallelProcedureJob(new ParallelNonOutputChildContext(context));
		}
		
		private class ParallelProcedureJob extends AbstractJob
		{
			private final ParallelNonOutputContext context;
			
			public ParallelProcedureJob(ParallelNonOutputContext context)
			{
				super(Meta.ExecuteOnSupplyingThread);
				this.context = context;
			}
			
			@Override
			public void run()
			{
				Job[] jobs = new Job[numSubtasks];
				
				for(int i = 0; i < numSubtasks; i++)
				{
					jobs[i] = subtasks[i].createNewSubParallelJob(context);
				}
				
				Phantom.dispatch(jobs);
			}
		}
	}
}
