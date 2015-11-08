package vault;


import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

//****************************************
public class Vault_secrets extends Vault_JFrame
//****************************************
{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JFrame show_all_secret_frame = null;
	JFrame display_clear_text_files_frame = null;
	JFrame show_files_frame = null;
	//****************************************
	public Vault_secrets() 
	//****************************************
	{
		super("vaultsecret.png",Color.yellow, "Vault_secrets stores your secrets in an encrypted file");


		JMenu m = create_stores_menu();
		mb.add(m);


		m = create_safe_cleanup_menu();
		mb.add(m);


	}




	//****************************************
	private JMenu create_stores_menu() 
	//****************************************
	{
		JMenu m;
		m = new JMenu("Stores");

		JMenuItem mi;

		mi = new JMenuItem("Open a store to make it current");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				open_a_store();	
			}


		});
		m.add(mi);


		mi = new JMenuItem("Show current store");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_all_secrets();	
			}


		});
		m.add(mi);

		mi = new JMenuItem("Create an new empty store");
		mi.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				create_empty_store();
			}


		});
		m.add(mi);

		/*
		mi = new JMenuItem("Add a secret to the current store");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				add_a_secret();	
			}


		});
		m.add(mi);

		mi = new JMenuItem("Show all secrets in the current store");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_all_secrets();	
			}

		});
		m.add(mi);
		 */
		return m;
	}

	Safe_KV_store the_store = null;
	//**********************************************************
	boolean init_store()
	//**********************************************************
	{
		if ( the_store != null) return true;

		the_store = new Safe_KV_store(get_encrypted_properties_file());
		if ( the_store.load_properties().status == false)
		{
			Show_dialog_box.display("Safe store init failed");
			return false;
		}
		return true;
	}

	//****************************************
	private void create_empty_store() 
	//****************************************
	{
		JFileChooser chooser = new JFileChooser("Vault's secrets encrypted container NEW file");
		chooser.setDialogTitle("Select the destination FOLDER where will be Vault_secrets new encrypted container");
		chooser.setFileHidingEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = chooser.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{

			File empty_clear_text_property_file = new File (chooser.getSelectedFile(), Safe_KV_store.clear_text_properties_file_name);
			//byte [] key = Pg_crypt_core.get_an_encryption_key_from_user();
			//if ( key != null)
			{
				the_store = new Safe_KV_store(Pg_crypt_core.get_encrypted_File(empty_clear_text_property_file));
				Operation_status os = the_store.store_properties();
				if ( os.status == true)
				{
					if (os.encrypted_file != null) backup_encrypted_files.add(os.encrypted_file);
					show_all_secrets();
				}
			}

		}
	}	


	//**********************************************************
	static private File get_encrypted_properties_file()
	//**********************************************************
	{
		JFileChooser chooser = new JFileChooser("Vault's secrets encrypted container file");
		chooser.setDialogTitle("Select the Vault's secrets encrypted container file");
		chooser.setFileHidingEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int returnVal = chooser.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			return chooser.getSelectedFile();
		}
		return null;
	}

	//****************************************
	void add_a_secret() 
	//****************************************
	{
		if ( the_store == null)
		{
			if ( init_store() == false) return;
		}

		JFrame add_secret_frame = new JFrame("Add a secret to the store");
		add_secret_frame.setLayout(new GridLayout(0,2));
		JLabel l1   = new JLabel("key (can be any thing)                                            ");
		add_secret_frame.add(l1);		
		JLabel l2   = new JLabel("value (can be any thing)                                          ");
		add_secret_frame.add(l2);		
		JTextField key   = new JTextField("");
		add_secret_frame.add(key);
		JTextField value = new JTextField("");
		add_secret_frame.add(value);
		JButton b = new JButton("Add (or press enter)");
		class EnterAction extends AbstractAction
		{
			private static final long serialVersionUID = 1L;
			private JButton ok;
			public EnterAction(JButton button)
			{
				this.ok = button;
			}
			public void actionPerformed(ActionEvent e)
			{
				ok.doClick();
			}
		};

		Action enter = new EnterAction(b);
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0);
		b.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW).put(stroke,"Enter");
		b.getActionMap().put("Enter",enter);

		b.setBackground(Color.green);
		add_secret_frame.add(b);
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) 
			{

				the_store.put(key.getText(), value.getText());
				Operation_status ds = the_store.store_properties();
				if ( ds.encrypted_file != null)
				{
					backup_encrypted_files.add(ds.encrypted_file);
				}
				add_secret_frame.dispose();
				show_all_secrets();	

			}
		});
		add_secret_frame.pack();
		add_secret_frame.setLocationRelativeTo(null);
		add_secret_frame.validate();
		add_secret_frame.setVisible(true);
	}


	//****************************************
	private void open_a_store()
	//****************************************
	{
		the_store = null;
		show_all_secrets();
	}
	//****************************************
	private void show_all_secrets() 
	//****************************************
	{
		if ( the_store == null)
		{
			if ( init_store() == false) return;
		}

		if ( show_all_secret_frame != null) show_all_secret_frame.dispose();
		show_all_secret_frame = new JFrame("All the secrets in the store");
		//show_all_secret_frame.setLayout(new BoxLayout(show_all_secret_frame, BoxLayout.Y_AXIS));	

		JPanel left = new JPanel();
		GridLayout gl1 = new GridLayout(0, 1);
		left.setLayout(gl1);

		JPanel center = new JPanel();
		GridLayout gl2 = new GridLayout(0, 1);
		center.setLayout(gl2);

		JPanel right = new JPanel();
		GridLayout gl3 = new GridLayout(0, 1);
		right.setLayout(gl3);


		final int height = 18;
		final int X_size = 120;


		JLabel keyl   = new JLabel("Secret name");// (can contain any character)");
		keyl.setPreferredSize(new Dimension(2*X_size, height));
		left.add(keyl);
		JLabel valuel = new JLabel("Secret content");// (can contain any character)");
		valuel.setPreferredSize(new Dimension(5*X_size, height));
		center.add(valuel);		
		JLabel deletel = new JLabel("Delete undo requires manual action on backup");// (can contain any character)");
		deletel.setPreferredSize(new Dimension(X_size, height));
		right.add(deletel);


		Set<String> all_keys = the_store.get_all_keys();
		List<String> all_keys_list = new ArrayList<String>();
		all_keys_list.addAll(all_keys);
		Comparator<String> comp = new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) 
			{
				return o1.compareToIgnoreCase(o2);
			}
		};
		Collections.sort(all_keys_list,comp );
		for ( String k : all_keys_list )
		{
			String v = the_store.get(k);
			JTextField key   = new JTextField(k);
			key.setPreferredSize(new Dimension(2*X_size, height));
			JTextField value = new JTextField(v);
			//value.setBackground(Color.yellow);
			value.setPreferredSize(new Dimension(5*X_size, height));
			JButton but = new JButton("Delete this item");
			but.setBackground(Color.red);
			but.setPreferredSize(new Dimension(X_size, height));
			but.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e)
				{
					the_store.remove(k);
					Operation_status ds = the_store.store_properties();
					if ( ds.encrypted_file != null)
					{
						backup_encrypted_files.add(ds.encrypted_file);
					}
					show_all_secrets();					
				}
			});


			left.add(key);		
			left.setMaximumSize(new Dimension(2*X_size, Integer.MAX_VALUE));
			center.add(value);
			center.setMaximumSize(new Dimension(5*X_size, Integer.MAX_VALUE));
			right.add(but);	
			right.setMaximumSize(new Dimension(X_size, Integer.MAX_VALUE));

		}


		JPanel master = new JPanel();
		//FlowLayout fl = new FlowLayout();
		//master.setLayout(fl);
		GridBagLayout gl0 = new GridBagLayout();
		GridBagConstraints c0 = new GridBagConstraints();
		master.setLayout(gl0);
		//c0.gridx = 0;
		//c0.gridy = 0;
		c0.gridwidth = 2;
		c0.gridheight = 1;
		c0.weightx = 1.0;
		c0.weighty = 0.0;
		c0.fill = GridBagConstraints.BOTH;
		master.add(left,c0);

		//c0.gridx = 2;
		//c0.gridy = 0;
		c0.gridwidth = 5;
		c0.gridheight = 1;
		c0.weightx = 1.0;
		c0.weighty = 0.0;
		c0.fill = GridBagConstraints.BOTH;
		master.add(center,c0);

		//c0.gridx = 7;
		//c0.gridy = 0;
		c0.gridwidth = 1;
		c0.gridheight = 1;
		c0.weightx = 1.0;
		c0.weighty = 0.0;
		c0.fill = GridBagConstraints.BOTH;
		master.add(right,c0);

		//master.setPreferredSize(new Dimension(Integer.MAX_VALUE, 800));
		master.setMinimumSize(new Dimension(1200,800));

		ScrollPane sp = new ScrollPane();
		//sp.add(master, ScrollPane.SCROLLBARS_AS_NEEDED);
		sp.add(master, ScrollPane.SCROLLBARS_AS_NEEDED);

		show_all_secret_frame.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		int MAGIC_FAC = 50;

		{
			JButton but = new JButton("Add a new item");
			but.setBackground(Color.green);
			but.setPreferredSize(new Dimension(X_size, height));
			but.setMinimumSize(new Dimension(X_size, height));
			but.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
			but.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e)
				{
					add_a_secret();
				}
			});
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = MAGIC_FAC;
			c.gridheight = 1;
			c.weightx = 1.0;
			c.weighty = 0.0;
			//c.fill = GridBagConstraints.BOTH;
			c.fill = GridBagConstraints.HORIZONTAL;
			show_all_secret_frame.add(but,c);		
		}

		{
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = MAGIC_FAC ;
			c.gridheight = MAGIC_FAC;
			c.weightx = 1.0;
			c.weighty = 1.0;
			c.fill = GridBagConstraints.BOTH;
			show_all_secret_frame.add(sp,c);
		}


		//center.add(Box.createHorizontalGlue());		
		//right.add(Box.createHorizontalGlue());	
		//master.setPreferredSize(new Dimension(800, 800));

		show_all_secret_frame.setSize(1200, 600);

		//show_all_secret_frame.pack();
		show_all_secret_frame.setLocationRelativeTo(null);
		show_all_secret_frame.validate();
		show_all_secret_frame.setVisible(true);
	}



	@Override
	protected void what_to_do_on_exit() 
	{
		cleanup(backup_encrypted_files,"backup safe (encrypted)");
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

				createAndShowGUI(new Vault_secrets());
			}
		});
	}


	@Override
	protected void process_dropped_file(File f) 
	{
		// TODO Auto-generated method stub

	}


}