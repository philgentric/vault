package vault;



import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
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
public class Pg_decrypt_core
{



	private static boolean ultra_debug = false;


	//****************************************
	public static PaddedBufferedBlockCipher get_decrypt_cipher(byte[] IV_local, byte [] local_key)
	//****************************************
	{
		// AES block cipher in CBC mode with padding
		PaddedBufferedBlockCipher decryptCipher =  new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
		//create the IV parameter
		ParametersWithIV parameterIV = new ParametersWithIV(new KeyParameter(local_key),IV_local);
		try
		{
			decryptCipher.init(false, parameterIV);
		}
		catch (Exception e)
		{
			Show_dialog_box.display(e.getMessage());
		}
		return decryptCipher;
	}



	//****************************************
	public static byte[] decrypt_low_level(
			InputStream in, 
			OutputStream out, 
			SwingWorker_with_progress_bar publisher, 
			byte[] local_key)
	//****************************************
					throws ShortBufferException, 
					IllegalBlockSizeException,
					BadPaddingException,
					DataLengthException,
					IllegalStateException,
					InvalidCipherTextException,
					IOException
	{
		// Bytes read from in will be decrypted
		// Read in the decrypted bytes from in InputStream and and
		//      write them in cleartext to out OutputStream

		// get the IV from the file
		// DO NOT FORGET TO reinit the cipher with the IV

		/*
		 * read the UUID
		 */
		byte[] uuid_bytes = new byte[Pg_crypt_core.UUID_size_in_bytes];
		in.read(uuid_bytes,0,Pg_crypt_core.UUID_size_in_bytes);
		ByteBuffer uuid_byte_buffer = ByteBuffer.wrap(uuid_bytes);
		Long hi = uuid_byte_buffer.getLong();
		Long lo = uuid_byte_buffer.getLong();
		UUID uuid = new UUID(hi, lo);
		System.out.println("UUID = "+uuid.toString());

		/*
		 * read the hash of the original file
		 */
		byte[] result = new byte[4];
		in.read(result,0,4);
		//ByteBuffer b = ByteBuffer.allocate(4);
		ByteBuffer.wrap(result);
		int foundhash_length = ByteBuffer.wrap(result).getInt();
		//System.out.println("reading hash length as:"+foundhash_length);
		MessageDigest sha;
		try 
		{
			sha = MessageDigest.getInstance("MD5");
		} 
		catch (NoSuchAlgorithmException e1) 
		{
			e1.printStackTrace();
			if ( publisher != null) publisher.publish_progress(0);
			return null;
		}
		if ( foundhash_length != sha.getDigestLength())
		{
			if ( publisher != null) publisher.publish_progress(0);
			return null;
		}
		byte[] found_hash =  new byte[foundhash_length];
		in.read(found_hash,0,found_hash.length);

		/*
		System.out.println("reading hash as:");
		for ( byte bb : found_hash)
		{
			System.out.print(bb);
		}
		System.out.print("\n");
		 */


		/*
		 * read the Initialization Vector
		 * 
		 */
		byte iv[] = new byte[Pg_crypt_core.block_size_in_bytes];
		in.read(iv,0,iv.length);
		//this.InitCiphers(iv,local_key);
		//in.read(IV,0,IV.length);
		//this.InitCiphers(IV,local_key);


		PaddedBufferedBlockCipher decryptCipher = get_decrypt_cipher(iv, local_key);

		byte[] buf = new byte[16];              //input buffer
		byte[] obuf = new byte[512];            //output buffer

		int noBytesRead = 0;        //number of bytes read from input
		int noBytesProcessed = 0;   //number of bytes processed
		int tot_bytes = 0;

		while ((noBytesRead = in.read(buf)) >= 0) 
		{
			if ( ultra_debug  == true) System.out.println(noBytesRead +" bytes read");
			noBytesProcessed = decryptCipher.processBytes(buf, 0, noBytesRead, obuf, 0);
			if ( ultra_debug == true) System.out.println(noBytesProcessed +" bytes processed");
			out.write(obuf, 0, noBytesProcessed);
			tot_bytes += noBytesProcessed;
			if ( publisher != null) publisher.publish_progress(tot_bytes);
		}
		if ( ultra_debug == true) System.out.println(noBytesRead +" bytes read");
		try
		{
			noBytesProcessed = decryptCipher.doFinal(obuf, 0);
		}
		catch(Exception e)
		{
			Show_dialog_box.display("FATAL: "+e);
			found_hash = null;
		}
		if ( ultra_debug == true) System.out.println(noBytesProcessed +" bytes processed");
		out.write(obuf, 0, noBytesProcessed);
		tot_bytes += noBytesProcessed;
		if ( publisher != null) publisher.publish_progress(tot_bytes);
		out.flush();

		in.close();
		out.close();

		return found_hash;
	}




	/* when we decrypt the source is always a file
	 * but the destination can be either a file
	 * or an in-memory location, which is transparent here
	 * because it is implemented by the Data_sink
	 */

	public static Operation_status decrypt(
			File input_file,
			Data_sink data_sink, 
			byte[] key, 
			SwingWorker_with_progress_bar publisher) 
	{
		Operation_status returned = new Operation_status();
		long target_size = input_file.length();
		if ( publisher != null) publisher.set_max(target_size);
		InputStream in = null;
		OutputStream out = data_sink.get_output_stream();

		try 
		{
			in = new BufferedInputStream(new FileInputStream(input_file));
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			returned.status = false;
			return returned;
		}




		byte[] original_hash = null;
		try
		{
			original_hash = decrypt_low_level(in, out, publisher, key);
			if ( original_hash == null)
			{
				System.out.println("low level decryption FAILED");
				returned.status = false;
				return returned;
			}
			System.out.println("low level decryption OK");
		} 
		catch (DataLengthException | ShortBufferException
				| IllegalBlockSizeException | BadPaddingException
				| IllegalStateException | InvalidCipherTextException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			returned.status = false;
			return returned;
		}

		byte[] hash_to_be_verified = data_sink.get_hash();

		if ( Arrays.equals(hash_to_be_verified, original_hash) == false)
		{
			System.out.println("different hash, the decrypted file is corrupted!");
			returned.status = false;
			return returned;
		}

		returned.clear_text_file = null;
		returned.status = true;
		return returned;
	}



}
