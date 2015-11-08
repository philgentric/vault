package vault;

import java.io.OutputStream;

//****************************************
public interface Data_sink
//****************************************
{
	public OutputStream get_output_stream();
	void close();
	public byte[] get_hash();
}
