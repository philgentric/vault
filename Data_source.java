package vault;

import java.io.InputStream;

//****************************************
public interface Data_source
{
	public InputStream get_input_stream();
	long get_size(); // number of bytes in the source data
	byte[] get_source_hash();
}
