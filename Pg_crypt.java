package vault;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JFileChooser;
import org.apache.commons.compress.archivers.ArchiveException;
import not_used_yet.Tar;

//****************************************
public class Pg_crypt
//****************************************
{

	static String extension = ".vault";

	//****************************************
	private static Operation_status decrypt_and_deTar_after_if_needed(
			File target, 
			SwingWorker_with_progress_bar publisher, 
			byte[] key)
	//****************************************
	{
		Operation_status returned = new Operation_status();
		System.out.println("going to DECRYPT");
		/*
		 * DECRYPT
		 */
		File local_clear_text_file = Pg_crypt_core.get_clear_text_file(target);// 
		
		File_data_sink fds = new File_data_sink();
		returned.encrypted_file = fds.init(local_clear_text_file);
		
		Operation_status os = Pg_decrypt_core.decrypt(
				target,
				fds,
				key,
				publisher);
		if ( os.status == false)
		{
			System.out.println("decryption failed");
			return os;
		}
		System.out.println("decryption OK");
		// maybe the file we decrypted is a tar, let us then de-tar it !!!
		if ( local_clear_text_file.getName().endsWith(".tar") == true)
		{	
			try 
			{
				/*
				 * DETAR
				 */
				File new_folder = Tar.detar_dir(local_clear_text_file);
				returned.clear_text_file = new_folder;
			} 
			catch (ArchiveException | FileNotFoundException e) 
			{
				e.printStackTrace();
				returned.status = false;
				return returned;
			}
			finally 
			{
				Garbagor.sure_delete(local_clear_text_file); // ALWAYS sure-delete the tar file				
			}
		}
		returned.status = true;
		return returned;
	}
	
	//****************************************
	private static Operation_status decrypt(
			File target, 
			SwingWorker_with_progress_bar publisher, 
			byte[] key)
	//****************************************
	{
		Operation_status returned = new Operation_status();
		System.out.println("going to DECRYPT");
		/*
		 * DECRYPT
		 */
		File local_clear_text_file = Pg_crypt_core.get_clear_text_file(target);// 
		
		File_data_sink fds = new File_data_sink();
		returned.encrypted_file = fds.init(local_clear_text_file);
		
		Operation_status os = Pg_decrypt_core.decrypt(
				target,
				fds,
				key,
				publisher);
		if ( os.status == false)
		{
			System.out.println("decryption failed");
			return os;
		}
		System.out.println("decryption OK");
		returned.clear_text_file = local_clear_text_file;
		returned.status = true;
		return returned;
	}


	static final boolean support_tar = false;
	// if the_map is not null, it means we are requested 
	// to decrypt toward RAM , if it is a decrypt operation
	// to encrypt from RAM , if it is a encrypt operation
	//****************************************
	public static Operation_status select_cryptographic_operation_based_on_file_name(
			File target, 
			SwingWorker_with_progress_bar publisher)
	//****************************************
	{
		
		if ( target.getName().endsWith(extension) == true)
		{
			byte [] key = Pg_crypt_core.get_a_decryption_key_from_user();
			if ( support_tar == true)
			{
				return decrypt_and_deTar_after_if_needed(target, publisher, key);
			}
			else
			{
				return decrypt(target, publisher, key);				
			}
		}
		else
		{
			byte [] key = Pg_crypt_core.get_an_encryption_key_from_user();
			if ( support_tar == true)
			{
				return encrypt_and_Tar_before_if_needed(target, publisher, key);
			}
			else
			{
				if ( target.isDirectory())
				{
					Show_dialog_box.display("Encrypting folders is not supported, first use an archiver (tar, zip, etc) to create a file");
					return new Operation_status();
				}
				else
				{
					return encrypt(target, publisher, key);				
				}
			}
		}
	}

	//****************************************
	private static Operation_status encrypt_and_Tar_before_if_needed(
			File source_clear_text_file, 
			SwingWorker_with_progress_bar publisher,
			byte[] key) 
	//****************************************
	{
		Operation_status returned = new Operation_status();
		boolean there_is_a_tmp_tar_to_delete = false;
		File local_source_clear_text_file = null;
		if ( source_clear_text_file.isDirectory() == true )
		{
			/*
			 * TAR
			 */
			File tarred_dir = Tar.tar_dir(source_clear_text_file);
			if ( tarred_dir == null)
			{
				returned.status = false;
				return returned;
			}
			local_source_clear_text_file = tarred_dir;
			there_is_a_tmp_tar_to_delete = true;
		}
		else
		{
			local_source_clear_text_file = source_clear_text_file;
		}
		
		Operation_status ds = Pg_encrypt_core.encrypt(
				new File_data_source(local_source_clear_text_file),
				Pg_crypt_core.get_encrypted_File(local_source_clear_text_file),
				key,
				publisher);
		if ( ds.status == true)
		{
			if ( there_is_a_tmp_tar_to_delete == true)
			{
				Garbagor.sure_delete(local_source_clear_text_file);
			}
			ds.clear_text_file = source_clear_text_file;
		}
		return ds;
	}

	//****************************************
	private static Operation_status encrypt(
			File source_clear_text_file, 
			SwingWorker_with_progress_bar publisher,
			byte[] key) 
	//****************************************
	{
		Operation_status returned = new Operation_status();
		if ( source_clear_text_file.isDirectory() == true )
		{
			returned.status = false;
			return returned;
		}
		
		Operation_status ds = Pg_encrypt_core.encrypt(
				new File_data_source(source_clear_text_file),
				Pg_crypt_core.get_encrypted_File(source_clear_text_file),
				key,
				publisher);
		if ( ds.status == true)
		{
			ds.clear_text_file = source_clear_text_file;
		}
		return ds;
	}





	/*
	 * unit test code
	 */

	//****************************************
	public static void main(String[] args)
	//****************************************
	{
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);

		if (returnVal != JFileChooser.APPROVE_OPTION) return;
		File file = fc.getSelectedFile();

		Pg_crypt.select_cryptographic_operation_based_on_file_name(file,null);


	}


}
