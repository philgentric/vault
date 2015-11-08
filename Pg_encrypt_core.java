package vault;



import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;


//****************************************
public class Pg_encrypt_core
//****************************************
{
	private final static boolean ultra_debug = false;
	
	
	//****************************************
	public static PaddedBufferedBlockCipher get_encrypt_cipher(byte[] IV_local, byte [] local_key)
	//****************************************
	{
		// AES block cipher in CBC mode with padding
		PaddedBufferedBlockCipher encryptCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
		ParametersWithIV parameterIV = new ParametersWithIV(new KeyParameter(local_key),IV_local);
		try
		{
			encryptCipher.init(true, parameterIV);
		}
		catch (Exception e)
		{
			Show_dialog_box.display(e.getMessage());
		}
		return encryptCipher;
	}
	
	

	//****************************************
	public static UUID encrypt_low_level(
			InputStream in, 
			OutputStream out, 
			byte[] hash, 
			SwingWorker_with_progress_bar publisher, 
			byte[] key)
	//****************************************
			throws ShortBufferException, 
			IllegalBlockSizeException,
			BadPaddingException,
			DataLengthException,
			IllegalStateException,
			InvalidCipherTextException,
			IOException
	{
		/*
		 * write a UUID (can be useful to store a password in a DB)
		 */
	    UUID uuid = UUID.randomUUID();
	    System.out.println("UUID = "+uuid.toString());
	    long hi = uuid.getMostSignificantBits();
	    long lo = uuid.getLeastSignificantBits();
		ByteBuffer byte_buffer__UUID = ByteBuffer.allocate(Pg_crypt_core.UUID_size_in_bytes);
		byte_buffer__UUID.putLong(hi);
		byte_buffer__UUID.putLong(lo);
		byte[] bytes_UUID = byte_buffer__UUID.array();
		out.write(bytes_UUID, 0, Pg_crypt_core.UUID_size_in_bytes); // UUID

		/*
		 * write the hash of the original file for checking at decryption time
		 */
		ByteBuffer byte_buffer_hash_length = ByteBuffer.allocate(Pg_crypt_core.hash_length_size_in_bytes);
		byte_buffer_hash_length.putInt(hash.length);
		byte[] bytes_hash_length = byte_buffer_hash_length.array();
		out.write(bytes_hash_length,0,Pg_crypt_core.hash_length_size_in_bytes);   // hash length
		out.write(hash, 0, hash.length); // hash data
		
		/*
		 * create, fill random, write the initialization vector
		 */
		byte[] iv = new byte[Pg_crypt_core.block_size_in_bytes];
		Random r = new Random();
		r.nextBytes(iv);
		out.write(iv, 0, iv.length);

		/*
		 * get an encryptor, encrypt and write
		 */
		PaddedBufferedBlockCipher encryptCipher = get_encrypt_cipher(iv, key);
		byte[] buf = new byte[16];              //input buffer
		byte[] obuf = new byte[512];            //output buffer
		int noBytesRead = 0;        //number of bytes read from input
		int noBytesProcessed = 0;   //number of bytes processed
		int tot_bytes = 0;
		while ((noBytesRead = in.read(buf)) >= 0) 
		{
			if ( ultra_debug == true) System.out.println(noBytesRead +" bytes read");

			noBytesProcessed = encryptCipher.processBytes(buf, 0, noBytesRead, obuf, 0);
			if ( ultra_debug == true) System.out.println(noBytesProcessed +" bytes processed");
			out.write(obuf, 0, noBytesProcessed);
			tot_bytes += noBytesProcessed;
			if ( publisher != null) publisher.publish_progress(tot_bytes);
		}

		if ( ultra_debug == true) System.out.println(noBytesRead +" bytes read (if -1, means end of data)");
		noBytesProcessed = encryptCipher.doFinal(obuf, 0);

		if ( ultra_debug == true) System.out.println(noBytesProcessed +" bytes processed (includes padding)");
		out.write(obuf, 0, noBytesProcessed);
		tot_bytes += noBytesProcessed;
		if ( publisher != null) publisher.publish_progress(tot_bytes);
		out.flush();

		in.close();
		out.close();
		
		return uuid;
	}



	//****************************************
	public static Operation_status encrypt(
			Data_source encryption_source,
			File target_output_file,
			byte [] key, 
			SwingWorker_with_progress_bar publisher)
	//****************************************
	{
		Operation_status returned = new Operation_status();
		long target_size = encryption_source.get_size();
		if ( publisher != null) publisher.set_max(target_size);

		
		InputStream in = encryption_source.get_input_stream();
		if ( in == null)
		{
			Show_dialog_box.display("FATAL: null InputStream for source");
			returned.status = false;
			return returned;
		}
		byte[] hash = encryption_source.get_source_hash();
		if ( hash == null)
		{
			Show_dialog_box.display("FATAL: null hash for source");
			returned.status = false;
			return returned;
		}

		
		OutputStream out = null;
		if ( target_output_file.exists() == true)
		{
			returned.encrypted_file = Pg_crypt_core.rename_and_save(target_output_file);
		}
		try 
		{
			out = new BufferedOutputStream(new FileOutputStream(target_output_file));
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			returned.status = false;
			return returned;
		}
		
		try 
		{
			encrypt_low_level(in, out, hash, publisher, key);
		} 
		catch (DataLengthException | ShortBufferException
				| IllegalBlockSizeException | BadPaddingException
				| IllegalStateException | InvalidCipherTextException
				| IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			returned.status = false;
			return returned;
		}
		returned.status = true;
		return returned;
	}

}
