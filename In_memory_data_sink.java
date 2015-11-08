package vault;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

//****************************************
public class In_memory_data_sink implements Data_sink
//****************************************
{
	OutputStream out;
	//HashMap<String,byte[]> the_map;
	//String the_name;
	private byte[] the_bytes = null;
	//****************************************
	//In_memory_data_sink(HashMap<String,byte[]> the_map_, String the_name_)
	//****************************************
	//{
	//	the_map = the_map_;
	//	the_name = the_name_;
	//}
	public In_memory_data_sink() 
	{
	}
	
	byte[] get_the_bytes(){return the_bytes;}
	
	//****************************************
	@Override
	public OutputStream get_output_stream() 
	//****************************************
	{
		out = new ByteArrayOutputStream();
		return out;
	}

	//****************************************
	@Override
	public void close() 
	//****************************************
	{
		//the_map.put(the_name,((ByteArrayOutputStream)out).toByteArray() );
		the_bytes = ((ByteArrayOutputStream)out).toByteArray();
	}
	//****************************************
	@Override
	public byte[] get_hash()
	//****************************************
	{
		close();
		//byte[] bytes = the_map.get(the_name);

		//if ( bytes == null)
		//{
		//	System.out.println("state machine error in In+memory_data_sink");
		//}
		//return Pg_crypt_core.get_array_hash(bytes);
		return Pg_crypt_core.get_array_hash(the_bytes);
	}

}
