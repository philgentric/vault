package vault;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;


/*
 * TODO
 * 
 * check the texts for typos
 * 
 * fix dir in dir ?... or just document the caveat
 * 
 * if the hash was encrypted, it would provide tamper checking
 * 
 * maven et launcher
 * 
 * cleanup the old files i.e.e the ones with "bis" in the name
 * 
 * store passwords in the store, with UUID as key => then auto generate passwords
 * well what is the use of having a different password for every file?
 * does it improve the security? 
 */

//****************************************
public abstract class Vault_JFrame extends JFrame
//****************************************
{
	private Color the_color;
	protected JMenuBar mb;
	private static final long serialVersionUID = 1L;

	protected static List<File> backup_encrypted_files = new ArrayList<File>();

	//****************************************
	Vault_JFrame(String icon_name, Color c, String string)
	//****************************************
	{
		super(string);
		the_color = c;
		setLocationRelativeTo(null); // center of screen

		mb = new JMenuBar();
		setJMenuBar(mb);

		{
			JMenu m = create_help_menu();		
			mb.add(m);
		}


		URL url = ClassLoader.getSystemResource("vault/resources/"+icon_name);
		//System.out.println("URL="+url);
		if ( url != null)
		{
			Toolkit kit = Toolkit.getDefaultToolkit();
			Image img = kit.createImage(url);			
			if ( img != null) setIconImage(img);
		}

		getContentPane().add(desktopPane);
		setTransferHandler(handler);
		desktopPane.setTransferHandler(handler);

	}




	//****************************************
	protected void show_text(String title, String text) 
	//****************************************
	{
		JFrame help_frame = new JFrame(title);
		JTextArea ta= new JTextArea();
		ta.setText(text);
		help_frame.add(ta);
		help_frame.pack();
		help_frame.setLocationRelativeTo(null);
		help_frame.validate();
		help_frame.setVisible(true);
	}	


	//****************************************
	protected boolean double_confirm_delete_file(File f) 
	//****************************************
	{
		int dialogButton = JOptionPane.showConfirmDialog (null, "Please confirm you want to sure-delete file "+f.getAbsolutePath()+" (WARNING: cannot be undone)","Are you sure?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
		if( dialogButton == JOptionPane.YES_OPTION)
		{
			if ( f.isDirectory() == true)
			{
				int dialogButton2 = JOptionPane.showConfirmDialog (null, 
						"DANGER: The target is a Folder ! Please confirm you want to sure-delete *recursively* ALL FILES IN THIS FOLDER (cannot be undone)\nIf you dont know what \"recursively\" means DONT click OK!","Are you REALLY REALLY sure?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
				if( dialogButton2 == JOptionPane.YES_OPTION)
				{
					Show_dialog_box.display("going to sure-delete:"+f.getAbsolutePath());
					return Garbagor.sure_delete(f);
				}
			}
			else
			{
				Show_dialog_box.display("going to sure-delete:"+f.getAbsolutePath());
				return Garbagor.sure_delete(f);				
			}
		}
		return false;
	}


	//****************************************
	private JMenu create_help_menu() 
	//****************************************
	{
		JMenu m = new JMenu("Help");
		JMenuItem mi = new JMenuItem("Help");
		mi.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("Vault_files help",Long_texts.vault_files_help);
			}


		});
		m.add(mi);

		mi = new JMenuItem("About");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("About Vault",Long_texts.about_vault);
			}
		});		
		m.add(mi);

		mi = new JMenuItem("Known issues");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("Known issues",Long_texts.known_issues);
			}
		});		
		m.add(mi);     

		mi = new JMenuItem("Why Vault is safe");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("Why Vault is quite safe",Long_texts.why_vault_is_safe);
			}
		});		
		m.add(mi);        


		mi = new JMenuItem("About passwords");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("About Passwords",Long_texts.about_passwords);
			}
		});		
		m.add(mi);        

		mi = new JMenuItem("I forgot my password, can you help?");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("I forgot my password, can you help?",Long_texts.dont_forget_password);
			}
		});		
		m.add(mi);        


		mi = new JMenuItem("What are safe backup files?");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("Safe backup files",Long_texts.explain_safe_files);
			}
		});
		m.add(mi);

		mi = new JMenuItem("What are unsafe files?");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("Unsafe clear text files",Long_texts.explain_unsafe_files);
			}
		});
		m.add(mi);

		mi = new JMenuItem("How can I sure-delete a file?");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("Sure-deleting files",Long_texts.explain_sure_deleting);
			}
		});
		m.add(mi);
		
		mi = new JMenuItem("What is sure-deleting?");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("What is sure-deleting?", Long_texts.explain_sure_deleting);
			}
		});
		m.add(mi);

		
		mi = new JMenuItem("Help");
		mi.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("Vault_secrets help",Long_texts.vault_secrets_help);
			}


		});
		m.add(mi);

		mi = new JMenuItem("About");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("About Vault",Long_texts.about_vault);
			}
		});		
		m.add(mi);

		mi = new JMenuItem("Known issues");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("Known issues",Long_texts.known_issues);
			}
		});		
		m.add(mi);     

		mi = new JMenuItem("Why Vault is safe");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("Why Vault is quite safe",Long_texts.why_vault_is_safe);
			}
		});		
		m.add(mi);        


		mi = new JMenuItem("About passwords");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("About Passwords",Long_texts.about_passwords);
			}
		});		
		m.add(mi);        

		mi = new JMenuItem("I forgot my password, can you help?");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("I forgot my password, can you help?",Long_texts.dont_forget_password);
			}
		});		
		m.add(mi);        

		mi = new JMenuItem("What are temporary safe files?");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("Temporary safe files",Long_texts.explain_safe_files);
			}
		});
		m.add(mi);

		mi = new JMenuItem("What are temporary unsafe files?");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("Temporary unsafe files",Long_texts.explain_unsafe_files);
			}
		});
		m.add(mi);

		mi = new JMenuItem("How can I sure-delete a file?");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("Sure-deleting files",Long_texts.explain_sure_deleting);
			}
		});
		m.add(mi);
		return m;
	}

	//****************************************
	protected JMenu create_safe_cleanup_menu() 
	//****************************************
	{
		JMenu m;
		m = new JMenu("Cleanup safe");

		JMenuItem mi;

		mi = new JMenuItem("What is safe?");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("What is safe?", Long_texts.what_is_safe);
			}
		});
		m.add(mi);

		mi = new JMenuItem("Show backup encrypted files");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_files(backup_encrypted_files);
			}
		});
		m.add(mi);

		mi = new JMenuItem("Erase backup encrypted files");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				cleanup(backup_encrypted_files,"backup safe files");
			}
		});
		m.add(mi);
		return m;
	}



	//****************************************
	public void cleanup(List<File> target, String text) 
	//****************************************
	{
		if ( target.isEmpty() == true) return;
		int s = target.size();
		int option = JOptionPane.showConfirmDialog(null, s+" "+text+" files will be deleted", "Please confirm, file deletion?", JOptionPane.OK_CANCEL_OPTION);
		if (option != JOptionPane.OK_OPTION)
		{
			return;
		}
		List<File> not_deleted = new ArrayList<File>();
		for( File f : target )
		{
			boolean b = Garbagor.sure_delete(f);
			if ( b == false) not_deleted.add(f);
		}
		target.clear();
		target.addAll(not_deleted);
		if ( not_deleted.isEmpty() == false)
		{
			int s2 = not_deleted.size();
			JOptionPane.showConfirmDialog(null, s2+" "+text+" files COULD NOT be deleted", "This is weird!?", JOptionPane.OK_CANCEL_OPTION);
		}

		show_files(target);
	}



	JFrame show_files_frame = null;

	//****************************************
	void show_files(List<File> ll) 
	//****************************************
	{
		if ( show_files_frame != null) show_files_frame.dispose();
		show_files_frame = new JFrame("Files: ");
		JTextArea ta= new JTextArea();
		String s = "";
		if ( ll.isEmpty() == true)
		{
			s = "No files for this session\n";
		}
		for( File f : ll )
		{
			s += f.getAbsolutePath()+"\n";

		}
		ta.setText(s);
		show_files_frame.add(ta);
		show_files_frame.pack();
		show_files_frame.setLocationRelativeTo(null);
		show_files_frame.validate();
		show_files_frame.setVisible(true);		
	}




	protected JDesktopPane desktopPane = new JDesktopPane()	
	{
		//****************************************
		public void paint(Graphics g)
		//****************************************
		{
			Graphics2D g2 = (Graphics2D) g;

			g2.setBackground(the_color);
			g2.clearRect(0, 0, getWidth(), getHeight());
		}

	};


	//****************************************
	static void createAndShowGUI(Vault_JFrame the_instance) 
	//****************************************
	{
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e) 
		{
		}

		the_instance.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		the_instance.addWindowListener(new java.awt.event.WindowAdapter() 
		{
			public void windowClosed(java.awt.event.WindowEvent evt)
			{
				System.out.println("window closing !!!!");
				the_instance.what_to_do_on_exit();
			}
		});

		the_instance.setSize(800, 200);
		the_instance.setLocationRelativeTo(null); // center of screen
		the_instance.setVisible(true);
	}


	protected abstract void what_to_do_on_exit();
	protected abstract void process_dropped_file(File f);



	/*
	 * magic of drag and drop files...
	 */

	protected TransferHandler handler = new TransferHandler() 
	{
		private static final long serialVersionUID = 1L;

		//****************************************
		public boolean canImport(TransferHandler.TransferSupport support) 
		//****************************************
		{
			if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) 
			{
				return false;
			}

			return true;
		}

		//****************************************
		public boolean importData(TransferHandler.TransferSupport support) 
		//****************************************
		{

			if (!canImport(support)) 
			{
				return false;
			}

			Transferable t = support.getTransferable();

			try 
			{
				List<File> l = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);

				for (File f : l) 
				{
					process_dropped_file(f);
				}
			} 
			catch (UnsupportedFlavorException | IOException e) 
			{
				return false;
			}

			return true;
		}
	};



}
