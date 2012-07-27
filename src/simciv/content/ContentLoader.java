package simciv.content;

import java.io.File;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.loading.LoadingList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This loaders parses an XML file listing content files to be loaded.
 * @author Marc
 *
 */
public class ContentLoader
{
	// Indexed data storage
	private Map<String, Image> imageMapRef;
	private Map<String, Sound> soundMapRef;
	private ContentSettings settingsRef;
	
	public ContentLoader(
			Map<String, Image> imageMap,
			Map<String, Sound> soundMap,
			ContentSettings settings)
	{
		this.imageMapRef = imageMap;
		this.soundMapRef = soundMap;
		this.settingsRef = settings;
	}
	
	/**
	 * Loads all resources from a file describing them
	 * @param filename
	 * @param deferred : if true, content will be added to a LoadingList. If not, content will be loaded directly.
	 * @throws SlickException
	 */
	public void loadRessources(String filename) throws SlickException
	{
        // open resources file
        File file = new File(filename);
        InputStream is = null;
        try
        {
			is = new DataInputStream(new FileInputStream(file));
			// load resources
			loadResources(is);
			is.close();
		}
        catch(FileNotFoundException e)
		{
			throw new SlickException("Cannot load resources", e);
		}
        catch(IOException e)
        {
			throw new SlickException("Cannot load resources", e);
		}
	}
	
	/**
	 * Loads all resources from a stream describing them
	 * @param is : XML resource file
	 * @param deferred : if true, content will be added to a LoadingList. If not, content will be loaded directly.
	 * @throws SlickException
	 */
	public void loadResources(InputStream is) throws SlickException
	{
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		
		// create document builder
		try
		{
			docBuilder = docBuilderFactory.newDocumentBuilder();
		}
		catch(ParserConfigurationException e)
		{
			throw new SlickException("Could not load resources", e);
		}
		
		Document doc = null;
		
		// parsing document
		try
		{
			doc = docBuilder.parse(is);
		}
		catch(SAXException e)
		{
			throw new SlickException("Could not load resources", e);
		}
		catch(IOException e)
		{
			throw new SlickException("Could not load resources", e);
		}
		
		// normalize text representation
		doc.getDocumentElement().normalize();
		
		// getting all resource elements
		NodeList listResources = doc.getElementsByTagName("resource");
		int nbResources = listResources.getLength();
		
		if(settingsRef.deferredLoading)
		{
			// clear loading list and enable deferred loading
			LoadingList.setDeferredLoading(true);
		}
			
		// fetching nodes
		for(int resourceIndex = 0; resourceIndex < nbResources; resourceIndex++)
		{
			Node resourceNode = listResources.item(resourceIndex);
			
			// if the node is an element
			if(resourceNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element resourceElement = (Element)resourceNode;				
				String type = resourceElement.getAttribute("type");
				
				// load resource from its type
				if(type.equals("image"))
					addElementAsImage(resourceElement);
				else if(type.equals("sound"))
					addElementAsSound(resourceElement);
			}
		}		
	}
	
	/**
	 * load sound from XML node
	 * @param resourceElement XML sound resource node
	 * @throws SlickException
	 */
	private final void addElementAsSound(Element resourceElement) throws SlickException
	{
		loadSound(resourceElement.getAttribute("id"),
				resourceElement.getTextContent());
	}
	
	/**
	 * load and index a sound
	 * @param id : unique identifier
	 * @param path : sound source path
	 * @return the sound
	 * @throws SlickException 
	 */
	private Sound loadSound(String id, String path) throws SlickException
	{
		// check path
		if(path == null || path.length() == 0)
			throw new SlickException("Sound resource [" + id + "] has invalid path");
		
		Sound sound = null;
		
		// load sound
		try
		{
			sound = new Sound(settingsRef.contentDir + path);
		}
		catch(SlickException e)
		{
			throw new SlickException("Could not load sound", e);
		}
		
		// index sound
		return soundMapRef.put(id, sound);
	}

	/**
	 * Load image from XML node
	 * @param resourceElement XML image resource node
	 * @throws SlickException
	 */
	private final void addElementAsImage(Element resourceElement) throws SlickException
	{
		loadImage(resourceElement.getAttribute("id"),
				resourceElement.getTextContent());
	}
	
	/**
	 * Load and index an image
	 * @param id image unique identifier
	 * @param path image source path
	 * @return loaded image
	 * @throws SlickException
	 */
	private Image loadImage(String id, String path) throws SlickException
	{
		// checking path
		if(path == null || path.length() == 0)
			throw new SlickException("Image resource [" + id + "] has invalid path");
		
		Image image = null;
		
		// loading image
		try
		{
			image = new Image(settingsRef.contentDir + path);
		}
		catch(SlickException e)
		{
			throw new SlickException("Could not load image", e);
		}
		
		return imageMapRef.put(id, image);		
	}
	
}


