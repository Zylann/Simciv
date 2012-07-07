package simciv;

import java.io.File;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.newdawn.slick.AngelCodeFont;
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
 * Singleton class used to get access to resources (sounds, pictures, strings...).
 * It makes abstraction of physical filenames by creating IDs.
 * These IDs are specified in an XML file.
 * Then, if we change a sound, we don't have to modify each code line we have used it.
 * @author Marc
 *
 */
public class ContentManager
{
	// unique instance
	private static ContentManager instance = new ContentManager();
	
	// indexed data storage
	private Map<String, Sound> soundMap;
	private Map<String, Image> imageMap;
	private Map<String, AngelCodeFont> fontMap;
	private Map<String, String> textMap;
	
	int defaultImageFilter = Image.FILTER_NEAREST;
	String contentDirectory = "data/";
	
	private ContentManager()
	{
		soundMap = new HashMap<String, Sound>();
		imageMap = new HashMap<String, Image>();
		fontMap = new HashMap<String, AngelCodeFont>();
		textMap = new HashMap<String, String>();
	}
	
	/**
	 * Get the resource manager
	 * @return unique instance
	 */
	public final static ContentManager instance()
	{
		return instance;
	}
	
	/**
	 * Loads all resources from a file describing them
	 * @param filename
	 * @param deferred : if true, content will be added to a LoadingList. If not, content will be loaded directly.
	 * @throws SlickException
	 */
	public void loadRessources(String filename, boolean deferred) throws SlickException
	{
        // open resources file
        File file = new File(filename);
        InputStream is = null;
        try
        {
			is = new DataInputStream(new FileInputStream(file));
			// load resources
			loadResources(is, deferred);
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
	public void loadResources(InputStream is, boolean deferred) throws SlickException
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
		
		if(deferred)
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
				else if(type.equals("text"))
					addElementAsText(resourceElement);
			}
		}
	}

	/**
	 * load text from XML node
	 * @param resourceElement XML text resource node
	 * @throws SlickException
	 */
	private final void addElementAsText(Element resourceElement) throws SlickException
	{
		loadText(resourceElement.getAttribute("id"),
				resourceElement.getTextContent());
	}
	
	/**
	 * load and index a text
	 * @param id : unique text identifier
	 * @param value : the text
	 * @throws SlickException
	 */
	private String loadText(String id, String value) throws SlickException
	{	
		if(value == null)
			throw new SlickException("Text resource [" + id + "] has invalid value");
		
		textMap.put(id, value);
		
		return value;
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
			sound = new Sound(contentDirectory + path);
		}
		catch(SlickException e)
		{
			throw new SlickException("Could not load sound", e);
		}
		
		// index sound
		this.soundMap.put(id, sound);
		
		return sound;
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
			image = new Image(contentDirectory + path);
			image.setFilter(defaultImageFilter);
		}
		catch(SlickException e)
		{
			throw new SlickException("Could not load image", e);
		}
		
		this.imageMap.put(id, image);
		
		return image;
	}
	
	/**
	 * Loads a font based on BMFont from AngelCode
	 * @param id
	 * @param fntFilePath
	 * @param imagePath
	 * @return
	 * @throws SlickException
	 */
	public AngelCodeFont loadFont(String id, String fntFilePath, String imagePath) throws SlickException
	{
		Image fontImage = new Image(contentDirectory + imagePath);
		fontImage.setFilter(defaultImageFilter);
		AngelCodeFont font = new AngelCodeFont(contentDirectory + fntFilePath, fontImage);
		fontMap.put(id, font);
		return font;
	}
	
	public final AngelCodeFont getFont(String id)
	{
		return fontMap.get(id);
	}
	
	/**
	 * 
	 * @param id image unique identifier
	 * @return wanted image
	 */
	public final Image getImage(String id)
	{
		return imageMap.get(id);
	}
	
	/**
	 * get a sound
	 * @param id : sound unique identifier
	 * @return wanted sound
	 */
	public Sound getSound(String id)
	{
		return soundMap.get(id);
	}
	
	/**
	 * get at text from id
	 * @param id : unique identifier
	 * @return the text
	 */
	public String getText(String id)
	{
		return textMap.get(id);
	}
}


