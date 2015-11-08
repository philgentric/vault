package vault;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

//**********************************************************
public class Safe_text_file
//**********************************************************
{

	private static byte[] password = null; // making the password static means we need to ask for it only once
	private static File the_encrypted_file;

	//**********************************************************
	Safe_text_file(File f_)
	//**********************************************************
	{
		the_encrypted_file = f_;
	}
	
	class Extended_Operation_status extends Operation_status
	{
		String the_text = "";
	}
	//**********************************************************
	Extended_Operation_status init()
	//**********************************************************
	{
		/*
		 * decrypt in RAM the property file
		 */
		if ( password == null)
		{
			password = Pg_crypt_core.get_a_decryption_key_from_user();
		}

		In_memory_data_sink imds = new In_memory_data_sink();

		Operation_status os = Pg_decrypt_core.decrypt(
				the_encrypted_file,
				imds,
				password,
				null);
		System.out.println("Decryption of file = "+the_encrypted_file.getAbsolutePath()+" status = "+os.status);

		if ( os.status == false) return new Extended_Operation_status();

		Extended_Operation_status returned = new Extended_Operation_status();
		returned.the_text = new String(imds.get_the_bytes(), StandardCharsets.UTF_8); // Or any encoding.		
		
		returned.status = true;
		return returned;
	}
	
	
	//**********************************************************
	public Operation_status save(String new_text)
	//**********************************************************
	{
		Operation_status returned = new Operation_status();
		/*
		 * encrypt FROM RAM to file
		 */
		if ( password == null)
		{
			return returned;
		}
		if ( the_encrypted_file == null)
		{
			return new Operation_status();
		}
		
		byte bytes[] = null;
		Operation_status ds;
		try {
			
			bytes = new_text.getBytes("UTF-8");

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ds = Pg_encrypt_core.encrypt(
				new In_memory_data_source(bytes),
				the_encrypted_file,
				password,
				null);
		
		System.out.println("Encryption of file = "+ds.status);		
		
		return ds;

	}


}
