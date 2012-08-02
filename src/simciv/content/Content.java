package simciv.content;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;


/**
 * Content subclasses may only have public attributes of the same type.
 * @author Marc
 *
 */
// TODO texture packs support
public class Content
{
	// Direct access
	public static Images images;
	public static Sounds sounds;
	// Static content (loading is hardcoded)
	public static AngelCodeFont globalFont;
	
	// Files mapping : path => file
	public static Map<String, Image> imageMap = new HashMap<String, Image>();
	public static Map<String, Sound> soundMap = new HashMap<String, Sound>();
	
	// Content settings
	public static ContentSettings settings = new ContentSettings();
	
	/**
	 * Loads content from an XML file and put it in the mapping only.
	 * Note that if deferred loading is enabled, content will not be available until
	 * we don't call LoadingList methods.
	 * @param filename
	 * @throws SlickException
	 */
	public static void loadFromContentFile(String filename) throws SlickException
	{
		ContentLoader loader = new ContentLoader(imageMap, soundMap, settings);
		loader.loadRessources(filename);
	}
	
	public static void loadMinimalContent() throws SlickException
	{
		Image fontImage = new Image(settings.contentDir + "arial8px_0.png");
		fontImage.setFilter(Content.settings.defaultImageFilter);
		globalFont = new AngelCodeFont(Content.settings.contentDir + "arial8px.fnt", fontImage);
	}
	
	public static int getTotalCount()
	{
		return imageMap.size() + soundMap.size();
	}

	/**
	 * Makes content available on direct access.
	 * All must be loaded before.
	 * @throws SlickException
	 */
	public static void indexAll() throws SlickException
	{
		indexImages();
		indexSounds();
	}
	
	private static void indexImages() throws SlickException
	{
		Images newImages = new Images();
		Field[] fields = newImages.getClass().getDeclaredFields();
		
		for(Field field : fields)
		{
			String imgID = field.getName();
			Image img = imageMap.get(imgID);
			if(img == null)
				throw new SlickException("Image not found (ID = " + imgID + ")");
			img.setFilter(settings.defaultImageFilter);
			try
			{
				field.set(newImages, img);
			} catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			} catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
		
		images = newImages;
	}
	
	private static void indexSounds() throws SlickException
	{
		Sounds newSounds = new Sounds();
		Field[] fields = newSounds.getClass().getDeclaredFields();
		
		for(Field field : fields)
		{
			String sndID = field.getName();
			Sound snd = soundMap.get(sndID);
			if(snd == null)
				throw new SlickException("Sound not found (ID = " + sndID + ")");
			try
			{
				field.set(newSounds, snd);
			} catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			} catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
		
		sounds = newSounds;
	}
	
}



