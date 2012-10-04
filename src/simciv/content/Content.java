package simciv.content;

import java.lang.reflect.Field;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;

/**
 * Content subclasses may only have public attributes of the same type.
 * @author Marc
 *
 */
// TODO texture packs support
public class Content
{
	// Direct access
	public static Sprites sprites;
	public static Sounds sounds;
	
	// Static content (loading is hardcoded)
	public static AngelCodeFont globalFont;
	
	// Content settings
	public static ContentSettings settings = new ContentSettings();
	
	private static ContentLoader loader;
	
	/**
	 * Loads content from an XML file and put it in the mapping only.
	 * Note that if deferred loading is enabled, content will not be available until
	 * we don't call LoadingList methods.
	 * @param filename
	 * @throws SlickException
	 */
	public static void loadFromContentFile(String filename) throws SlickException
	{
		if(loader == null)
			loader = new ContentLoader(settings);
		loader.loadRessources(filename);
	}
	
	public static void loadMinimalContent() throws SlickException
	{
		Log.info("Loading minimal content...");
		Image fontImage = new Image(settings.contentDir + "font/arial8px_0.png");
		fontImage.setFilter(Content.settings.defaultImageFilter);
		globalFont = new AngelCodeFont(Content.settings.contentDir + "font/arial8px.fnt", fontImage);
	}
	
	public static int getTotalCount()
	{
		if(loader == null)
			return 0;
		
		return loader.getTotalCount();
	}

	/**
	 * Makes content available on direct access.
	 * All must be loaded before.
	 * @throws SlickException
	 */
	public static void indexAll() throws SlickException
	{
		indexSprites();
		indexSounds();
	}
	
	private static void indexSprites() throws SlickException
	{
		Sprites newSprites = new Sprites();
		Field[] fields = newSprites.getClass().getDeclaredFields();
		
		for(Field field : fields)
		{
			String ID = field.getName();
			
			if(field.getType() == Image.class)
			{
				Image img = loader.getImage(ID);
				if(img == null)
					throw new SlickException("Image not found (ID = " + ID + ")");
				try
				{
					field.set(newSprites, img);
				} catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				} catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
			else if(field.getType() == SpriteSheet.class)
			{
				SpriteSheet spr = loader.getSpriteSheet(ID);
				if(spr == null)
					throw new SlickException("SpriteSheet not found (ID = " + ID + ")");
				try
				{
					field.set(newSprites, spr);
				} catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				} catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
			else if(field.getType() == Animation.class)
			{
				Animation anim = loader.getAnimation(ID);
				if(anim == null)
					throw new SlickException("Animation not found (ID = " + ID + ")");
				try
				{
					field.set(newSprites, anim);
				} catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				} catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		sprites = newSprites;
	}
	
	private static void indexSounds() throws SlickException
	{
		Sounds newSounds = new Sounds();
		Field[] fields = newSounds.getClass().getDeclaredFields();
		
		for(Field field : fields)
		{
			String sndID = field.getName();
			Sound snd = loader.getSound(sndID);
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



