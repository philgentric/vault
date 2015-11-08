package vault;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

//**********************************************************
public class Safe_KV_store
//**********************************************************
{
	private Properties the_Properties = new Properties();
	private byte[] password = null; // making the password static means we need to ask for it only once
	public final static String clear_text_properties_file_name = "vault_properties.txt";
	private File the_encrypted_property_file;

	//**********************************************************
	public Safe_KV_store(File f)
	//**********************************************************
	{
		the_encrypted_property_file = f;
		//Operation_status os = load_properties();
		//return os.status;
	}


	//**********************************************************
	public Operation_status load_properties()
	//**********************************************************
	{
		Operation_status returned = new Operation_status();
		
		//File f2 = get_encrypted_properties_file();

		if ( the_encrypted_property_file == null)
		{
			returned.status = false;
			return returned;
		}
		
		/*
		 * decrypt in RAM the property file
		 */
		if ( password == null)
		{
			password = Pg_crypt_core.get_a_decryption_key_from_user();
		}

		In_memory_data_sink imds = new In_memory_data_sink();

		Operation_status os = Pg_decrypt_core.decrypt(
				the_encrypted_property_file,
				imds,
				password,
				null);
		System.out.println("Decryption of file = "+the_encrypted_property_file.getAbsolutePath()+" status = "+os.status);

		if ( os.status == false) return os;

		ByteArrayInputStream bais = new ByteArrayInputStream(imds.get_the_bytes());
		try 
		{
			the_Properties.load(bais);
		} 
		catch (IOException e) 
		{
			returned.status = false;
			e.printStackTrace();
		}
		returned.status = true;
		return returned;
	}
	
	//**********************************************************
	public Operation_status store_properties()
	//**********************************************************
	{
		return store_properties(the_encrypted_property_file);
	}
	
	//**********************************************************
	public Operation_status store_properties(File f)
	//**********************************************************
	{
		
		if ( f == null)
		{
			return new Operation_status();
		}
		
		//System.out.println("in memory store_properties()"+the_Properties.toString());

		/*
		 * encrypt FROM RAM the property file
		 */
		if ( password == null)
		{
			password = Pg_crypt_core.get_an_encryption_key_from_user();
		}

		ByteArrayOutputStream byte_array_output_stream = new ByteArrayOutputStream();

		try 
		{
			the_Properties.store(byte_array_output_stream,"no comment");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return new Operation_status();
		}


		
		Operation_status ds = Pg_encrypt_core.encrypt(
				new In_memory_data_source(byte_array_output_stream.toByteArray()),
				f,
				password,
				null);

		System.out.println("Encryption of DISK property file = "+ds.status);		
		
		return ds;

	}
	//**********************************************************
	public Set<String> get_all_keys()
	//**********************************************************
	{
		return the_Properties.stringPropertyNames();
	}
	//**********************************************************
	public String get(String key)
	//**********************************************************
	{
		String returned = (String) the_Properties.get(key);
		//System.out.println(key + " = "+returned);
		return returned;
	}
	//**********************************************************
	public void clear()
	//**********************************************************
	{
		the_Properties.clear();		
	}
	//**********************************************************
	public void put(String key, String value) 
	//**********************************************************
	{
		//System.out.println(key + " = "+value);
		while ( the_Properties.get(key) != null)
		{
			key += "-";
		}
		String replaced = (String)the_Properties.put(key,value);	
		if (replaced!= null) System.out.println("this key replaced a existing one = "+replaced);
	}
	//**********************************************************
	public void remove(String k)
	//**********************************************************
	{
		System.out.println("removing key "+k);
		String removed = (String) the_Properties.remove(k);
		
		if ( removed != null)
		{
			System.out.println("removed value "+removed);
		}
		else
		{
			System.out.println("remove failed");
		}
	}
}
