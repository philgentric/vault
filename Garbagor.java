package vault;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFileChooser;

/*
 * utility to write random garbage on top of a file content
 * this is useful before deleting a file
 * to prevent un-deleting the file
 */
//****************************************
public class Garbagor 
//****************************************
{
	static Random r = null;
	//****************************************
	static boolean garbagor(File target) 
	//****************************************
	{
		if ( target.isDirectory() == true)
		{
			return split(target);
		}
		long size = target.length();
		//System.out.println("file size="+size);
		if ( r == null) r = new Random();
		BufferedOutputStream out = null;
		try 
		{
			out = new BufferedOutputStream(new FileOutputStream(target));
			

			int GROUK = 8;
			int buf_size = GROUK*Long.BYTES;
			//System.out.println("buf_size="+buf_size);
			ByteBuffer byte_buffer__UUID = ByteBuffer.allocate(buf_size);
			for (;;)
			{
				byte_buffer__UUID.clear();
				for ( int k = 0 ; k < GROUK; k++)
				{
					byte_buffer__UUID.putLong(r.nextLong());
				}
				byte[] bytes_UUID = byte_buffer__UUID.array();
				out.write(bytes_UUID);
				//System.out.println("==="+byte_buffer__UUID.toString());
				size -= (long)buf_size;
				if ( size < 0) break;
			}
			//System.out.println("done");
			out.flush();
			out.close();
		} 
		catch (IOException e) 
		{
			//System.out.println("exception !???");

			e.printStackTrace();
			return false;
		}
		
		//System.out.println("END");
		return true;
		
	}
	
	
	//****************************************
	private static boolean split(File target) 
	//****************************************
	{
		boolean returned = true;
		for ( File f : target.listFiles())
		{
			if ( f.isDirectory() == true)
			{
				if ( split(f) == false) returned = false;
			}
			else
			{
				if ( garbagor(f) == false) returned = false;
			}
		}
		return returned;
	}


	
	//****************************************
	static boolean sure_delete(File f) 
	//****************************************
	{
		boolean status = garbagor(f);
		if ( status == false)
		{
			Show_dialog_box.display("Possibly unsafe: the garbaging operation on file "+f.getAbsolutePath()+" failed");
		}
		status = f.delete();
		if ( status == false)
		{
			Show_dialog_box.display("Possibly unsafe: could not delete the file "+f.getAbsolutePath());
		}
		return status;
	}	
	
	//****************************************
	public static void main(String[] args)
	//****************************************
	{
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);

		if (returnVal != JFileChooser.APPROVE_OPTION) return;
		File file = fc.getSelectedFile();
	
		System.out.println("file to garbage = "+file.getAbsolutePath());
		
		Garbagor.garbagor(file);
	}
	
}
