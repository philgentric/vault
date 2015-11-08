package vault;


public interface Progress_reported_task
{
	public void do_the_job_and_publish_progress(SwingWorker_with_progress_bar publisher);
	public boolean get_final_status();
}
