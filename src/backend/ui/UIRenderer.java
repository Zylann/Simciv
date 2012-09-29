package backend.ui;

import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;

/**
 * Defines how widgets are rendered (defines the graphical theme)
 * @author Marc
 *
 */
public class UIRenderer
{
	private static ITheme theme;
	private static int globalScale = 1;
	
	public static void setTheme(ITheme t)
	{
		theme = t;
	}
	
	public static Font getFont()
	{
		return theme.getFont();
	}
	
	public static ITheme getTheme()
	{
		if(theme == null)
		{
			Log.warn("No UI theme defined, DefaultTheme will be used");
			theme = new DefaultTheme();
		}
		return theme;
	}
		
	public static void setGlobalScale(int s)
	{
		globalScale = s;
	}
	
	public static int getGlobalScale()
	{
		return globalScale;
	}

	public static void beginRender(Graphics gfx)
	{
		gfx.pushTransform();
		
		ITheme t = getTheme();
		Font font = t.getFont();
		if(font != null)
			gfx.setFont(font);
		
		gfx.scale(globalScale, globalScale);
	}
	
	public static void endRender(Graphics gfx)
	{
		gfx.popTransform();
	}
	
	// Utility methods for easier UI rendering
	
	public static void renderImagePart(
			Graphics gfx, Image img,
			int posX, int posY,
			int srcX, int srcY,
			int srcW, int srcH)
	{
		gfx.drawImage(img,
				posX, posY,
				posX + srcW, posY + srcH,
				srcX, srcY,
				srcX + srcW, srcY + srcH);
	}
	
	public static void renderImageRepeatXY(
			Graphics gfx, Image img,
			int posX, int posY, int w, int h)
	{
		int lastW = w % img.getWidth();
		int lastH = h % img.getHeight();
		int x, y;
		
		gfx.pushTransform();
		gfx.translate(posX, posY);
		
		for(y = 0; y <= h - img.getHeight(); y += img.getHeight())
		{
			for(x = 0; x <= w - img.getWidth(); x += img.getWidth())
				gfx.drawImage(img, x, y);
			gfx.drawImage(img,
					x, y, 
					x + lastW, y + img.getHeight(), 
					0, 0, 
					lastW, img.getHeight());
		}
		for(x = 0; x <= w - img.getWidth(); x += img.getWidth())
		{
			gfx.drawImage(img,
					x, y, 
					x + img.getWidth(), y + lastH,
					0, 0,
					img.getWidth(), lastH);
		}
		gfx.drawImage(img,
				x, y, 
				x + lastW, y + lastH,
				0, 0,
				lastW, lastH);
		
		gfx.popTransform();
	}
	
	/**
	 * Renders a frame based on a spritesheet of 9 cells :
	 * Cells marked by a '*' are repeated to match the right size (Last cells are cropped).
	 * Note : render a frame at too small sizes may cause visual oddities.
	 * o---o---o---o
	 * |   | * |   |
	 * o---o---o---o
	 * | * | * | * |
	 * o---o---o---o
	 * |   | * |   |
	 * o---o---o---o
	 * @param gfx
	 * @param posX
	 * @param posY
	 * @param w
	 * @param h
	 */
	public static void renderFrame(Graphics gfx, SpriteSheet sheet,
			int posX, int posY, int w, int h, int b)
	{
		Image top = sheet.getSprite(1, 0);
		Image left = sheet.getSprite(0, 1);
		Image right = sheet.getSprite(2, 1);
		Image bottom = sheet.getSprite(1, 2);
		Image center = sheet.getSprite(1, 1);
		
		// Corners
		gfx.drawImage(sheet.getSprite(0, 0), posX, posY);
		gfx.drawImage(sheet.getSprite(2, 0), posX + w - b, posY);
		gfx.drawImage(sheet.getSprite(0, 2), posX, posY + h - b);
		gfx.drawImage(sheet.getSprite(2, 2), posX + w - b, posY + h - b);
		// Borders
		renderImageRepeatXY(gfx, top, 		posX + b,		posY, 			w - 2 * b, 	b);
		renderImageRepeatXY(gfx, left, 		posX, 			posY + b, 		b, 			h - 2 * b);
		renderImageRepeatXY(gfx, right, 	posX + w - b, 	posY + b, 		b, 			h - 2 * b);
		renderImageRepeatXY(gfx, bottom, 	posX + b,		posY + h - b, 	w - 2 * b, 	b);
		// Center
		renderImageRepeatXY(gfx, center, posX + b, posY + b, w - 2 * b, h - 2 * b);
	}
	
	public static void renderBar(Graphics gfx, SpriteSheet sheet,
			int posX, int posY, int w, int h, int b, int s)
	{
		// Corners
		gfx.drawImage(sheet.getSprite(0, s), posX, posY);
		gfx.drawImage(sheet.getSprite(2, s), posX + w - b, posY);
		// Middle
		renderImageRepeatXY(gfx, sheet.getSprite(1, s), posX + b, posY, w - 2 * b, b);
	}

}




