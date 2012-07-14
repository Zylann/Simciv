package simciv.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import simciv.ContentManager;

/**
 * Defines how widgets are rendered (defines the graphical theme)
 * @author Marc
 *
 */
public class UIRenderer
{
	protected static UIRenderer instance;
	private static int FRAME_BASE = 16;
	
	private int globalScale;
	private Image toolButtonBackground;
	private Image menuItemBackground;
	private SpriteSheet frameBackground;
	private Font font;
	
	public static UIRenderer instance()
	{
		if(instance == null)
			instance = new UIRenderer();
		return instance;
	}
	
	private UIRenderer()
	{
		globalScale = 1;
		font = ContentManager.instance().getFont("ui.font");
		toolButtonBackground = ContentManager.instance().getImage("ui.toolButton");
		menuItemBackground = ContentManager.instance().getImage("ui.menuItem");
		
		Image frameBackgroundImg = ContentManager.instance().getImage("ui.frame");
		frameBackground = new SpriteSheet(frameBackgroundImg, FRAME_BASE, FRAME_BASE);		
	}
	
	public void setGlobalScale(int s)
	{
		globalScale = s;
	}
	
	public void beginRender(Graphics gfx)
	{
		gfx.pushTransform();
		gfx.setFont(font);
		gfx.scale(globalScale, globalScale);
	}
	
	public void endRender(Graphics gfx)
	{
		gfx.popTransform();
	}
	
	public void renderImageRepeatXY(Graphics gfx, Image img, int posX, int posY, int w, int h)
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
	 * Cells marked by a '*' are repeated to match the right size.
	 * Note : render a frame at too small sizes may cause visual artefacts.
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
	public void renderFrame(Graphics gfx, int posX, int posY, int w, int h)
	{		
		Image top = frameBackground.getSprite(1, 0);
		Image left = frameBackground.getSprite(0, 1);
		Image right = frameBackground.getSprite(2, 1);
		Image bottom = frameBackground.getSprite(1, 2);
		Image center = frameBackground.getSprite(1, 1);
		
		int b = FRAME_BASE;
		
		// Corners
		gfx.drawImage(frameBackground.getSprite(0, 0), posX, posY);
		gfx.drawImage(frameBackground.getSprite(2, 0), posX + w - b, posY);
		gfx.drawImage(frameBackground.getSprite(0, 2), posX, posY + h - b);
		gfx.drawImage(frameBackground.getSprite(2, 2), posX + w - b, posY + h - b);
		// Borders
		renderImageRepeatXY(gfx, top, posX + b, posY, w - 2 * b, b);
		renderImageRepeatXY(gfx, left, posX, posY + b, b, h - 2 * b);
		renderImageRepeatXY(gfx, right, posX + w - b, posY + b, b, h - 2 * b);
		renderImageRepeatXY(gfx, bottom, posX + b, posY + h - b, w - 2 * b, b);
		// Center
		renderImageRepeatXY(gfx, center, posX + b, posY + b, w - 2 * b, h - 2 * b);
	}
	
	public void renderPanel(Graphics gfx, Panel w)
	{
		renderFrame(gfx, w.getX(), w.getY(), w.getWidth(), w.getHeight());
	}
	
	private void renderButton(Graphics gfx, Button w, Image sprite, Image icon, String text, int pressOffset)
	{
		int x = w.getAbsoluteX();
		int y = w.getAbsoluteY();
		
		int srcOff = 0;
		if(w.isMouseOver() && w.isEnabled())
			srcOff = w.getHeight();
		
		if(w.isPressed())
			y += pressOffset;
		
		gfx.drawImage(sprite,
				x, y,
				x + w.getWidth(),
				y + w.getHeight(),
				0, srcOff,
				w.getWidth(),
				w.getHeight() + srcOff
		);
		
		if(icon != null)
			gfx.drawImage(icon, x, y);
		
		if(text != null)
		{
			if(w.isEnabled())
				gfx.setColor(Color.black);
			else
				gfx.setColor(Color.gray);
			gfx.drawString(text, x + 4, y + 2);
		}
	}

	public void renderMenuItem(Graphics gfx, MenuItem w)
	{
		renderButton(gfx, w, menuItemBackground, null, w.getText(), 1);
	}
	
	public void renderToolButton(Graphics gfx, ToolButton w)
	{
		renderButton(gfx, w, toolButtonBackground, w.icon, null, 2);
	}

	public int getGlobalScale()
	{
		return globalScale;
	}
}

