package vault;



import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.UUID;

import javax.swing.JOptionPane;
import javax.swing.JTextField;


//****************************************
public class Pg_crypt_core
//****************************************
{
	private final static boolean ultra_debug = false;

	final static int block_size_in_bytes = 16;
	final static int internal_hash_computation_buffer_size_in_bytes = 1024;
	final static int UUID_size_in_bytes = 16;
	final static int hash_length_size_in_bytes = 4;
	private static String extension = ".vault";


	//****************************************
	static byte[] get_an_encryption_key_from_user()
	//****************************************
	{
		JTextField password1 = new JTextField();//JPasswordField();
		JTextField password2 = new JTextField();//JPasswordField();
		Object[] message = {"Password:", password1,"Confirm Password:", password2};

		int option = JOptionPane.showConfirmDialog(null, message, "A password is required", JOptionPane.OK_CANCEL_OPTION);
		if (option != JOptionPane.OK_OPTION)
		{
			return null;
		}
		if ( password1.getText().equals(password2.getText()) == false)
		{
			Show_dialog_box.display("Passwords dont match! Aborting!");
			return null;
		}
		String local_password = password1.getText();
		if ( password1.getText().length()> 32)
		{
			local_password = password1.getText().substring(0, 32);
		}
		byte [] key = Pg_crypt_core.password_to_encryption_key(local_password);
		return key;
	}

	//****************************************
	static byte[] get_a_decryption_key_from_user()
	//****************************************
	{
		JTextField password1 = new JTextField();//JPasswordField();
		Object[] message = {"Password:", password1};

		int option = JOptionPane.showConfirmDialog(null, message, "A password is required", JOptionPane.OK_CANCEL_OPTION);
		if (option != JOptionPane.OK_OPTION)
		{
			return null;
		}
		String local_password = password1.getText();
		if ( password1.getText().length()> 32)
		{
			local_password = password1.getText().substring(0, 32);
		}
		byte [] key = Pg_crypt_core.password_to_encryption_key(local_password);
		return key;
	}


	//****************************************
	static private byte[] password_to_encryption_key(String password)
	//****************************************
	{
		byte[] local_key;

		if ( password.length() < 8)
		{
			//TODO this check is appropriate if the password is for ENCRYPTION
			Show_dialog_box.display("A password of at leats 8 chars is REQUIRED, minimum recommended complexity is to include 1 uppercase, and one number");
			return null;
		}
		//System.out.println("initial password ->"+password+"<-");
		byte[] password_bytes = password.getBytes();
		local_key = new byte[32];
		/*
		 * copy the password up to 32 char to get a 256 bit key
		 */
		int j = 0;
		for ( int i = 0 ; i < 32; i ++)
		{
			local_key[i] = password_bytes[j];
			j++;
			if ( j >= password_bytes.length ) j = 0;
		}

		return local_key;
	}

	//**********************************************************
	public static byte[] get_file_hash(File srcfile)
	//**********************************************************
	{
		byte[] hash = null;
		try
		{
			FileInputStream fis = new FileInputStream(srcfile);
			byte[] b = new byte[Pg_crypt_core.internal_hash_computation_buffer_size_in_bytes];

			MessageDigest sha = MessageDigest.getInstance("MD5");
			//sha.reset();
			for(;;)
			{
				int available = fis.available();
				if ( available < Pg_crypt_core.internal_hash_computation_buffer_size_in_bytes)
				{
					fis.read(b, 0, available);
					//System.out.println("byte " + new String(b));
					sha.update(b, 0, available);
					break;
				}
				else
				{
					fis.read(b, 0, Pg_crypt_core.internal_hash_computation_buffer_size_in_bytes);
					//System.out.println("byte " + new String(b));
					sha.update(b, 0, Pg_crypt_core.internal_hash_computation_buffer_size_in_bytes);
				}
			}
			fis.close();
			hash = sha.digest();
			//System.out.println("the MD5 hash of " + srcfile + " is " + new String(hash) + " " + sha.getDigestLength());

		}
		catch(Exception ioe)
		{
			System.out.println(ioe);
			return null;
		}
		return hash;
	}

	/* 
	 * compute the hash of an in-memory data
	 * so as to write it into the encrypted file ( encryption operation)
	 * so has to VERIFY that the value we read in the encrypted file matches (after decryption) 
	 */

	//****************************************
	static byte[] get_array_hash(byte[] bytes) 
	//****************************************
	{
		byte[] hash = null;
		try
		{
			ByteArrayInputStream fis = new ByteArrayInputStream(bytes);
			byte[] b = new byte[Pg_crypt_core.internal_hash_computation_buffer_size_in_bytes];

			MessageDigest sha = MessageDigest.getInstance("MD5");
			//sha.reset();
			for(;;)
			{
				int available = fis.available();
				if ( available < Pg_crypt_core.internal_hash_computation_buffer_size_in_bytes)
				{
					fis.read(b, 0, available);
					//System.out.println("byte " + new String(b));
					sha.update(b, 0, available);
					break;
				}
				else
				{
					fis.read(b, 0, Pg_crypt_core.internal_hash_computation_buffer_size_in_bytes);
					//System.out.println("byte " + new String(b));
					sha.update(b, 0, Pg_crypt_core.internal_hash_computation_buffer_size_in_bytes);
				}
			}
			fis.close();
			hash = sha.digest();
			//System.out.println("the MD5 hash of the array is " + new String(hash) + " " + sha.getDigestLength());

		}
		catch(Exception ioe)
		{
			System.out.println(ioe);
			return null;
		}
		return hash;	
	}


	//****************************************
	static File rename_and_save(File future) 
	//****************************************
	{
		/* TODO = protect the original extension (??)
		 * if ( future.getName().endsWith(extension) == true)
		{
			// remove the extension
			String no_vault_ext = future.getName().substring(0, future.getName().indexOf(extension));
			String new_name = no_vault_ext+
		}*/
		File savor = new File(future.getParent(),future.getName()+"_previous_"+UUID.randomUUID()+extension);
		future.renameTo(savor);

		System.out.println("Target file exist, renaming it to:"+savor.getAbsolutePath());

		return savor;
	}

	/*
	 * clear text file in the SAME directory
	 */
	public static File get_clear_text_file(File target) 
	{
		return new File(
				target.getParent(), 
				target.getName().substring(0,target.getName().indexOf(Pg_crypt_core.extension)));
	}

	/*
	 * just the name = extension removed
	 */
	public static String get_clear_text_file_name(File target) 
	{
		return target.getName().substring(0,target.getName().indexOf(Pg_crypt_core.extension));
	}

	/*
	 * encrypted file in the SAME directory
	 */
	public static File get_encrypted_File(File target) 
	{
		return new File(target.getAbsolutePath()+Pg_crypt_core.extension);
	}
}
