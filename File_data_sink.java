package vault;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

//****************************************
public class File_data_sink implements Data_sink
//****************************************
{
	File future;
	BufferedOutputStream out;
	//****************************************
	File init( File f )
	//****************************************
	{
		File returned = null;
		future = f;
		if ( future.exists() == true)
		{
			returned = Pg_crypt_core.rename_and_save(future);
		}
		out = null;
		return returned;
	}
	//****************************************
	@Override
	public OutputStream get_output_stream() 
	//****************************************
	{
		try {
			out = new BufferedOutputStream(new FileOutputStream(future));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return out;
	}
	//****************************************
	@Override
	public void close()
	//****************************************
	{
		try 
		{
			out.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	//****************************************
	@Override
	public byte[] get_hash() 
	//****************************************
	{
		return Pg_crypt_core.get_file_hash(future);
	}

}
