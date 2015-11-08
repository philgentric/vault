package vault;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.swing.JFileChooser;

//**********************************************************
public class Properties_manager 
//**********************************************************
{
	public boolean debug_flag = false;
	//static String properties_file_path = "D:/tmp/";	
	//protected static String properties_file_name = "vault_properties.txt";
	private static File the_property_file;
	protected Properties the_Properties = new Properties();

	//**********************************************************
	public Properties_manager()
	//**********************************************************
	{
		the_property_file = null;
	}
	//**********************************************************
	public void store_properties()
	//**********************************************************
	{
		if ( debug_flag  == true ) System.out.println("store_properties()");
		FileOutputStream fos;
		try
		{
			File f = get_properties_file();

			if ( f.canWrite() == false )
			{
				//TODO: make this a dialog
				System.out.println("ALERT: cannot write properties in:"+f.getAbsolutePath());
				return;
			}

			fos = new FileOutputStream(f);
			the_Properties.store(fos,"no comment");
			fos.close();
			//System.out.println("properties stored in:"+f.getAbsolutePath());
		}
		catch (Exception e)
		{
			System.out.println("Exception: " + e);
		}
		//the_properties.list(System.out);
	}
	//**********************************************************
	static private File get_properties_file()
	//**********************************************************
	{
		if ( the_property_file != null) return the_property_file;
		

		JFileChooser chooser = new JFileChooser("Vault's secrets encrypted container file");
		chooser.setDialogTitle("Select the Vault's secrets encrypted container file");
		chooser.setFileHidingEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int returnVal = chooser.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			the_property_file = chooser.getSelectedFile();
		}
		
		
		
		return the_property_file;
	}
	//**********************************************************
	public void load_properties()
	//**********************************************************
	{
		if ( debug_flag == true ) System.out.println("load_properties()");
		FileInputStream fis;
		try
		{
			File f = get_properties_file();
			if ( f.exists()== true)
			{
				if ( f.canRead() == false )
				{
					System.out.println("cannot read properties from:"+f.getAbsolutePath());
					return;
				}
				fis = new FileInputStream(f);
				the_Properties.load(fis);
				System.out.println("properties loaded from:"+f.getAbsolutePath());
				fis.close();
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception: " + e);
		}
	}
	//**********************************************************
	public String get(String key)
	//**********************************************************
	{
		String returned = (String) the_Properties.get(key);
		//System.out.println(key + " = "+returned);
		return returned;
	}
	//**********************************************************
	public void clear()
	//**********************************************************
	{
		the_Properties.clear();		
	}
	//**********************************************************
	public void put(String key, String value) 
	//**********************************************************
	{
		System.out.println(key + "putting ="+value);
		the_Properties.put(key,value);	
	}

}
