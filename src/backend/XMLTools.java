package backend;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.newdawn.slick.util.Log;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XMLTools
{
	private XMLTools() {}

	/**
	 * Writes the given object into an XML file
	 * @param object : the object to save
	 * @param fileName : path to the XML file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static boolean encodeToFile(Object object, String fileName)
			throws FileNotFoundException, IOException
	{
		XStream xstream = new XStream(new DomDriver());
		try
		{
			FileOutputStream fout = new FileOutputStream(fileName);
			xstream.toXML(object, fout);
		} catch (FileNotFoundException e)
		{
			Log.error("Couldn't write to " + fileName);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Reads an object from an XML file
	 * @param fileName : path to the XML file
	 * @return the read object
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Object decodeFromFile(String fileName)
			throws FileNotFoundException, IOException
	{
		Object object = null;
		XStream xstream = new XStream(new DomDriver());
		try
		{
			FileInputStream fin = new FileInputStream(fileName);
			object = xstream.fromXML(fin);
		} catch (FileNotFoundException e)
		{
			Log.error("Couldn't read from " + fileName);
			e.printStackTrace();
		}
		return object;
	}

}
