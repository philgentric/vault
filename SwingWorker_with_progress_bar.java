package vault;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;


//****************************************
public class SwingWorker_with_progress_bar extends SwingWorker<Void, Integer> 
//****************************************
{

	JProgressBar the_progress_bar;
	Progress_reported_task the_progress_reported_task;
	long max;
	JLabel label;
	Progress_bar_frame pb;
	//****************************************
	public SwingWorker_with_progress_bar(
			JProgressBar jpb,
			int max, 
			JLabel label, 
			Progress_reported_task task_,
			Progress_bar_frame pb_) 
	//****************************************
	{
		this.the_progress_bar = jpb;
		this.max = max;
		this.label = label;
		this.the_progress_reported_task = task_;
		pb = pb_;
	}

	//****************************************
	public void publish_progress(int i)
	//****************************************
	{
		publish(i);
	}
	
	
	//****************************************
	@Override
	protected void process(List<Integer> chunks) 
	//****************************************
	{
		int i = chunks.get(chunks.size()-1);
		the_progress_bar.setValue(i); // The last value in this array is all we care about.
		System.out.println(i);
		label.setText(i + " / " + max + " bytes");
	}

	//****************************************
	@Override
	protected Void doInBackground() throws Exception 
	//****************************************
	{
		the_progress_reported_task.do_the_job_and_publish_progress(this);
		return null;
	}

	//****************************************
	@Override
	protected void done() 
	//****************************************
	{
		try 
		{
			get(); // wait until the SwingWorker has finished
			
			if ( the_progress_reported_task.get_final_status() == true)
			{
				JOptionPane.showMessageDialog(the_progress_bar.getParent(), "The cryptographic operation completed", "Success", JOptionPane.INFORMATION_MESSAGE);
			}
			else
			{
				JOptionPane.showMessageDialog(the_progress_bar.getParent(), "Something went wrong: BEWARE if a file was produced, it is INVALID", "Failure", JOptionPane.INFORMATION_MESSAGE);				
			}
		} 
		catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
		pb.hide();
	}

	//****************************************
	public void set_max(long target_size)
	//****************************************
	{
		max = target_size;
		the_progress_bar.setMaximum((int)max);
	}
} 