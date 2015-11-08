package vault;


import javax.swing.*;
import javax.swing.event.*;

import vault.Safe_text_file.Extended_Operation_status;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.util.*;
import java.util.List;

//****************************************
public class Vault_files extends Vault_JFrame
//****************************************
{


	JFrame show_all_secret_frame = null;
	JFrame display_clear_text_files_frame = null;
	JFrame show_files_frame = null;
	protected static List<File> clear_text_files = new ArrayList<File>();
	//****************************************
	public Vault_files() 
	//****************************************
	{
		super("vault_files.png",Color.blue, "Drag and drop a file or a folder in the landing zone below to trigger encrypt or decrypt");

		JMenu m = create_sure_delete_menu();
		mb.add(m);

		m = create_safe_cleanup_menu();
		mb.add(m);

		m = create_unsafe_cleanup_menu();
		mb.add(m);

	}


	//****************************************
	private JMenu create_sure_delete_menu() 
	//****************************************
	{
		JMenu m;
		m = new JMenu("Sure deleting");

		JMenuItem mi;


		mi = new JMenuItem("Sure delete a file");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(null);

				if (returnVal != JFileChooser.APPROVE_OPTION) return;
				File file = fc.getSelectedFile();
				double_confirm_delete_file(file);
			}
		});
		m.add(mi);

		mi = new JMenuItem("Garbage-inside a file");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) return;
				File file = fc.getSelectedFile();			
				double_confirm_garbage_file(file);
			}
		});
		m.add(mi);

		return m;
	}


	//****************************************
	private JMenu create_unsafe_cleanup_menu() 
	//****************************************
	{
		JMenu m;
		m = new JMenu("Cleanup unsafe");

		JMenuItem mi;
		mi = new JMenuItem("What is unsafe?");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("What is unsafe?", Long_texts.what_is_unsafe);
			}
		});
		m.add(mi);

		mi = new JMenuItem("Show discovered (clear text = unsafe) files");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				display_clear_text_files(clear_text_files);
			}
		});
		m.add(mi);

		return m;
	}


	//****************************************
	private void display_clear_text_files(List<File> list) 
	//****************************************
	{
		if ( display_clear_text_files_frame != null) display_clear_text_files_frame.dispose();
		display_clear_text_files_frame = new JFrame("Clear text (potentially unsafe) files");
		if ( list.isEmpty() == true)
		{
			Show_dialog_box.display("No (no more) clear text files discovered during this session");
			return;
		}
		for( File f : list )
		{
			JPanel p = new JPanel();
			TextField tf = new TextField(f.getAbsolutePath());
			p.add(tf);
			{
				JButton but = new JButton("Sure-delete this file");
				but.setBackground(Color.red);
				but.setPreferredSize(new Dimension(300, 20));
				but.addActionListener(new ActionListener() {				
					@Override
					public void actionPerformed(ActionEvent e)
					{
						if ( double_confirm_delete_file(f) == true)
						{
							List<File> ll = new ArrayList<File>();
							ll.addAll(list);
							ll.remove(f);
							display_clear_text_files(ll);
						}
					}
				});			
				p.add(but);
			}
			display_clear_text_files_frame.add(p);

		}
		display_clear_text_files_frame.pack();
		display_clear_text_files_frame.setLocationRelativeTo(null);
		display_clear_text_files_frame.validate();
		display_clear_text_files_frame.setVisible(true);		
	}


	//****************************************
	private void double_confirm_garbage_file(File f) 
	//****************************************
	{
		int dialogButton = JOptionPane.showConfirmDialog (null, "Please confirm you want to garbage-inside file "+f.getAbsolutePath()+" (WARNING: cannot be undone)","Are you sure?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
		if( dialogButton == JOptionPane.YES_OPTION)
		{
			if ( f.isDirectory() == true)
			{
				int dialogButton2 = JOptionPane.showConfirmDialog (null, 
						"DANGER: The target is a Folder ! Please confirm you want to garbage *recursively* ALL FILES IN THIS FOLDER (cannot be undone)\nIf you dont know what \"recursively\" means DONT click OK!","Are you REALLY REALLY sure?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
				if( dialogButton2 == JOptionPane.YES_OPTION)
				{
					Garbagor.garbagor(f);
				}

			}
			else
			{
				Garbagor.garbagor(f);				
			}
		}
	}




	//****************************************
	private static void createAndShowGUI(String[] args) 
	//****************************************
	{
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e) 
		{
		}

		Vault_files the_instance = new Vault_files();
		the_instance.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		the_instance.addWindowListener(new java.awt.event.WindowAdapter() 
		{
			public void windowClosed(java.awt.event.WindowEvent evt)
			{
				System.out.println("window closing !!!!");
				the_instance.cleanup(backup_encrypted_files,"backup safe (encrypted)");
			}


		});

		the_instance.setSize(800, 200);
		the_instance.setLocationRelativeTo(null); // center of screen
		the_instance.setVisible(true);
	}





	//****************************************
	@Override
	protected void process_dropped_file(File f)
	//****************************************
	{
		//****************************************
		class Pt implements Progress_reported_task
		//****************************************
		{
			private File f;
			private boolean final_status = false;


			//****************************************
			public Pt(File f)
			//****************************************
			{
				this.f = f;
			}


			//****************************************
			@Override
			public void do_the_job_and_publish_progress(SwingWorker_with_progress_bar publisher)
			//****************************************
			{
				Operation_status ds = Pg_crypt.select_cryptographic_operation_based_on_file_name(
						f,
						publisher);

				final_status = ds.status;
				if ( ds.encrypted_file != null)
				{
					backup_encrypted_files.add(ds.encrypted_file);							
				}
				if ( ds.clear_text_file != null)
				{
					clear_text_files.add(ds.clear_text_file);							
				}

				System.out.println("do_the_job_and_publish_progress, final status is = "+final_status);
			}


			//****************************************
			@Override
			public boolean get_final_status() 
			//****************************************
			{
				return final_status;
			}

		};
		Pt pt = new Pt(f);
		Progress_bar_frame.show_progress("Performing cryptographic operations, please wait ...", pt);
	}	

	@Override
	protected void what_to_do_on_exit() 
	{
		cleanup(backup_encrypted_files,"backup safe (encrypted)");
		cleanup(clear_text_files,"discovered unsafe (clear text)");
	}


	//****************************************
	public static void main(final String[] args) 
	//****************************************
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() 
			{
				//Turn off metal's use of bold fonts
				//UIManager.put("swing.boldMetal", Boolean.FALSE);

				createAndShowGUI(new Vault_files());
			}
		});
	}



}