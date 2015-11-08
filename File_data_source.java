package vault;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

//****************************************
public class File_data_source implements Data_source
//****************************************
{
	File source_file;
	//****************************************
	File_data_source(File src_)
	//****************************************
	{
		source_file = src_;
	}
	//****************************************
	@Override
	public InputStream get_input_stream()
	//****************************************
	{
		InputStream in = null;
		try 
		{
			in = new BufferedInputStream(new FileInputStream(source_file));
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
		return in;
	}
	//****************************************
	@Override
	public long get_size() 
	//****************************************
	{
		return source_file.length();
	}
	//****************************************
	@Override
	public byte[] get_source_hash() 
	//****************************************
	{
		return 	Pg_crypt_core.get_file_hash(source_file);
	}

}
