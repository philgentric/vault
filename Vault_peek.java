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
public class Vault_peek extends Vault_JFrame
//****************************************
{


	//****************************************
	public Vault_peek() 
	//****************************************
	{
		super("vault_peek.png",Color.green, "Drag and drop a file in the landing zone below to decrypt in RAM and have a peek");

		JMenu m = create_peek_menu();		
		mb.add(m);


		m = create_safe_cleanup_menu();
		mb.add(m);


		getContentPane().add(desktopPane);
		setTransferHandler(handler);
		desktopPane.setTransferHandler(handler);

	}
	

	//****************************************
	private JMenu create_peek_menu() 
	//****************************************
	{
		JMenu m;
		m = new JMenu("Having a peek at a file");

		JMenuItem mi;

		mi = new JMenuItem("What is a peek?");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				show_text("What is a peek?", Long_texts.explain_peek);
			}
		});
		m.add(mi);

		mi = new JMenuItem("Peek a file");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(null);

				if (returnVal != JFileChooser.APPROVE_OPTION) return;
				File file = fc.getSelectedFile();
				peek(file);
			}
		});
		m.add(mi);


		return m;
	}

	//****************************************
	protected static void peek(File file) 
	//****************************************
	{
		if ( file.getName().endsWith(Pg_crypt.extension) == false)
		{
			Show_dialog_box.display("you can only peek at encrypted files");
		}

		JFrame peek_frame = new JFrame();

		peek_frame.setTitle(file.getAbsolutePath());

		Safe_text_file stf = new Safe_text_file(file);
		Extended_Operation_status eos = stf.init();
		if ( eos.status == false )
		{
			return;
		}

		System.out.println(eos.the_text);
		JTextArea jta = new JTextArea();
		jta.setText(eos.the_text);

		JScrollPane scrollPane = new JScrollPane(jta);
		scrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		peek_frame.add(scrollPane);


		JMenuItem mi = new JMenuItem("Save");
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));

		mi.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("SAVING");
				stf.save(jta.getText());								
			}
		});
		JMenu menu = new JMenu("Menu");
		menu.add(mi);
		JMenuBar mbar=new JMenuBar();
		mbar.add(menu);
		peek_frame.setJMenuBar(mbar);



		peek_frame.setSize(1200, 600);
		peek_frame.setLocationRelativeTo(null);
		peek_frame.validate();
		peek_frame.setVisible(true);				
	}






	//****************************************
	@Override
	protected void process_dropped_file(File f)
	//****************************************
	{
		peek(f);
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

				createAndShowGUI(new Vault_peek());
			}
		});
	}


}