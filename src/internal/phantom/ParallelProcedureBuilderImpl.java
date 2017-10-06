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
		super(() -> new ParallelProcedureTask(subtasksCollector.apply(0), null));
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
		return new ParallelProcedureTask(subtasksCollector.apply(0), nextTask);
	}
	
	@Override
	public ProcedureTaskImpl construct()
	{
		return getFromCache();
	}
	
	public static class ParallelProcedureTask extends AbstractParallelNonOutputTaskImpl implements ProcedureTaskImpl
	{
		private final ProcedureTaskImpl[] subtasks;
		
		public ParallelProcedureTask(ProcedureTaskImpl[] subtasks, NonInputTaskImpl nextTask)
		{
			super(subtasks.length, nextTask);
			this.subtasks = subtasks;
		}
		
		@Override
		public Job createNewJob()
		{
			return new ParallelProcedureJob(new Context());
		}
		
		@Override
		public Job createNewSubParallelJob(ParallelContext context)
		{
			return new ParallelProcedureJob(new ChildContext(context));
		}
		
		protected class ParallelProcedureJob extends AbstractJob
		{
			private final Context context;
			
			public ParallelProcedureJob(Context context)
			{
				super(Meta.ExecuteOnSupplyingThread);
				this.context = context;
			}
			
			@Override
			public void run()
			{
				for(NonInputTaskImpl subtask : subtasks)
				{
					Phantom.dispatch(subtask.createNewSubParallelJob(context));
				}
			}
		}
	}
}
