package vault;


import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;


//****************************************
public class Progress_bar_frame
//****************************************
{
	JFrame frame;
	
	//****************************************
	public void go(String msg, int max, Progress_reported_task pt)
	//****************************************
	{
		frame = new JFrame("Cryptographic operation in progress");
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JLabel label = new JLabel("Work in progress, please wait...");
		JProgressBar jpb = new JProgressBar();
		jpb.setIndeterminate(false);
		jpb.setMaximum(max);
		//jpb.setMinimumSize(new Dimension(600, 50));
		panel.add(label, BorderLayout.NORTH);
		panel.add(jpb, BorderLayout.SOUTH);
		frame.add(panel);
		frame.pack();
		frame.setSize(600,100);
		frame.setLocation(300, 300);
		//frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SwingWorker_with_progress_bar swwpb = new SwingWorker_with_progress_bar(jpb, max, label, pt, this);
		swwpb.execute();
		

	}

  

	//****************************************
	public static void show_progress(String msg, Progress_reported_task pt) 
	//****************************************
	{
		SwingUtilities.invokeLater(new Runnable() 
		{
			@Override
			public void run() 
			{
				Progress_bar_frame pb = new Progress_bar_frame();
				pb.go(msg,100, pt);
			}
		});
	}


	//****************************************
	public static void main(String args[]) 
	//****************************************
	{
		//****************************************
		class Prt implements Progress_reported_task
		//****************************************
		{
			boolean final_status = false;

			//****************************************
			@Override
			public void do_the_job_and_publish_progress(SwingWorker_with_progress_bar publisher)
			//****************************************
			{
				for(int i = 0 ;i < 100; i++)
				{
					try 
					{
						Thread.sleep(100);
					} 
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					publisher.publish_progress(i);
				}		
				final_status = true;
			}


			//****************************************
			@Override
			public boolean get_final_status() 
			//****************************************
			{
				return final_status;
			}
			
		};
		
		
		Prt pt = new Prt();
		show_progress("toto ...", pt);
	}



	//****************************************
	public void hide()
	//****************************************
	{
		frame.setVisible(false);
	}

}