package vault;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class In_memory_data_source implements Data_source
{
	byte[] bytes;
	
	In_memory_data_source(byte[] bytes_)
	{
		bytes = bytes_;
	}
	@Override
	public InputStream get_input_stream() 
	{
		return 	new ByteArrayInputStream(bytes);			
	}

	@Override
	public long get_size() 
	{
		return bytes.length;
	}

	@Override
	public byte[] get_source_hash() 
	{
		return Pg_crypt_core.get_array_hash(bytes);
	}

}
