package simciv.content;

import java.io.File;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This loader parses an XML file listing content files and loads them into maps.
 * @author Marc
 *
 */
public class ContentLoader
{
	private Map<String, Image> imageMap;
	private Map<String, Sound> soundMap;
	private Map<String, SpriteSheet> spriteSheetMap;
	private Map<String, String> stringMap;
	private Map<String, Animation> animationMap;
	private ContentSettings settings;
	
	// TODO add language attribute to strings (easier translation of the game)
	// TODO write a DTD of the expected XML structure
	
	public ContentLoader(ContentSettings settings)
	{
		imageMap = new HashMap<String, Image>();
		soundMap = new HashMap<String, Sound>();
		spriteSheetMap = new HashMap<String, SpriteSheet>();
		stringMap = new HashMap<String, String>();
		animationMap = new HashMap<String, Animation>();
		this.settings = settings;
	}
	
	public void clear()
	{
		imageMap.clear();
		soundMap.clear();
		spriteSheetMap.clear();
		stringMap.clear();
	}
	
	public Image getImage(String id)
	{
		return imageMap.get(id);
	}

	public SpriteSheet getSpriteSheet(String id)
	{
		return spriteSheetMap.get(id);
	}
	
	public Sound getSound(String id)
	{
		return soundMap.get(id);
	}
	
	public String getString(String id)
	{
		return stringMap.get(id);
	}
	
	public Animation getAnimation(String id)
	{
		return animationMap.get(id);
	}
	
	public int getTotalCount()
	{
		return imageMap.size() + spriteSheetMap.size() + soundMap.size() + stringMap.size();
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
    		System.out.println("Reading " + filename + "...");    		
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
		
		// Get the content node and its children
		NodeList tlist = doc.getElementsByTagName("content");
		if(tlist.getLength() == 0)
		{
			System.out.println("INFO: no XML content node found");
			return;
		}
		NodeList contentNodes = tlist.item(0).getChildNodes();
		
		for(int i = 0; i < contentNodes.getLength(); i++)
		{
			Node node = contentNodes.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element contentElement = (Element)node;
				String type = contentElement.getTagName();

				if(type.equals("image"))
				{
					loadImageFromXML(contentElement);
				}
				else if(type.equals("spritesheet"))
				{
					loadSpriteSheetFromXML(contentElement);
				}
				else if(type.equals("sound"))
				{
					loadSoundFromXML(contentElement);
				}
				else if(type.equals("string"))
				{
					loadStringFromXML(contentElement);
				}
				else if(type.equals("animationsheet"))
				{
					loadAnimationSheetFromXML(contentElement);
				}
			}
		}
		
		Log.info(getTotalCount() + " content files read");
	}

	private void loadStringFromXML(Element stringElement) throws SlickException
	{
		String id = stringElement.getAttribute("id");
		
		if(id.isEmpty())
			throw new SlickException("Missing id attribute in string content XML line");
		
		String str = stringElement.getTextContent();
		
		if(str == null)
			throw new SlickException("Missing content text in string XML content line");
		
		stringMap.put(id, str);
	}

	private void loadSoundFromXML(Element soundElement) throws SlickException
	{
		String id = soundElement.getAttribute("id");
		String src = soundElement.getAttribute("src");
		
		if(src.isEmpty() || id.isEmpty())
			throw new SlickException("Missing id or src attribute in sound content XML line");
		
		Sound s = new Sound(settings.contentDir + src);
		
		soundMap.put(id, s);
	}

	private void loadImageFromXML(Element imageElement) throws SlickException
	{
		String id = imageElement.getAttribute("id");
		String src = imageElement.getAttribute("src");
		
		if(src.isEmpty() || id.isEmpty())
			throw new SlickException("Missing id or src attribute in image content XML line");
		
		Image img = new Image(settings.contentDir + src);
		img.setFilter(settings.defaultImageFilter);
		
		imageMap.put(id, img);
	}

	private void loadSpriteSheetFromXML(Element spritesheetElement) throws SlickException
	{
		String id = spritesheetElement.getAttribute("id");
		String src = spritesheetElement.getAttribute("src");
		String twStr = spritesheetElement.getAttribute("tw");
		String thStr = spritesheetElement.getAttribute("th");		
		
		if(src.isEmpty() || id.isEmpty())
			throw new SlickException("Missing id or src attribute in spritesheet content XML line");
		
		if(twStr.isEmpty() || thStr.isEmpty())
			throw new SlickException("Missing tw or th attribute in spritesheet content XML line");
		
		int tw = Integer.parseInt(twStr);
		int th = Integer.parseInt(thStr);
		
		if(tw <= 0 || th <= 0)
			throw new SlickException("Invalid value of tw or th in spritesheet content XML line");
		
		Image img = new Image(settings.contentDir + src);
		img.setFilter(settings.defaultImageFilter);
		SpriteSheet sprites = new SpriteSheet(img, tw, th);
		
		spriteSheetMap.put(id, sprites);
	}
	
	private void loadAnimationSheetFromXML(Element animSheetElement) throws SlickException
	{
		String id = animSheetElement.getAttribute("id");
		
		if(id.isEmpty())
			throw new SlickException("Missing id attribute in string content XML line");
		
		String src = animSheetElement.getAttribute("src");
		String twStr = animSheetElement.getAttribute("tw");
		String thStr = animSheetElement.getAttribute("th");
		String frametimeStr = animSheetElement.getAttribute("frametime");
		
		if(src.isEmpty() || twStr.isEmpty() || thStr.isEmpty() || frametimeStr.isEmpty())
			throw new SlickException("Missing arguments (src, tw, th and frametime are required)");
		
		int tw = Integer.parseInt(twStr);
		int th = Integer.parseInt(thStr);
		int frametime = Integer.parseInt(frametimeStr);
		
		if(tw <= 0 || th <= 0 || frametime <= 0)
			throw new SlickException("Invalid value of tw or th or frametime in spritesheet content XML line");
		
		Image img = new Image(settings.contentDir + src);
		img.setFilter(settings.defaultImageFilter);
		SpriteSheet sprites = new SpriteSheet(img, tw, th);
		Animation anim = new Animation(sprites, frametime);
		
		animationMap.put(id, anim);
	}

}


