package simciv.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import simciv.content.Content;

/**
 * Defines how widgets are rendered (defines the graphical theme)
 * @author Marc
 *
 */
public class UIRenderer
{
	// Unique instance
	protected static UIRenderer instance;
	// Constants
	private static final int FRAME_BASE = 16;
	private static final int WINDOW_SHADOW_SIZE = 2;
	
	private int globalScale;
	private SpriteSheet frameBackground;
	private SpriteSheet windowTitleBarBackground;
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
		loadContent();
	}
	
	private void loadContent()
	{
		font = Content.globalFont;
		
		Image frameBackgroundImg = Content.images.uiFrame;
		frameBackground = new SpriteSheet(frameBackgroundImg, FRAME_BASE, FRAME_BASE);
		
		Image windowTitleBarBackgroundImg = Content.images.uiWindowTitleBar;
		windowTitleBarBackground =
			new SpriteSheet(windowTitleBarBackgroundImg,
					windowTitleBarBackgroundImg.getHeight(),
					windowTitleBarBackgroundImg.getHeight());
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
	public void renderFrame(Graphics gfx, SpriteSheet sheet, int posX, int posY, int w, int h, int b)
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
		
	public void renderPanel(Graphics gfx, Panel w)
	{
		renderFrame(gfx, frameBackground,
				w.getAbsoluteX(), w.getAbsoluteY(),
				w.getWidth() + WINDOW_SHADOW_SIZE, 
				w.getHeight() + WINDOW_SHADOW_SIZE, 
				FRAME_BASE);
	}
	
	public void renderWindow(Graphics gfx, Window w)
	{
		renderFrame(gfx, frameBackground,
				w.getAbsoluteX(), w.getAbsoluteY(),
				w.getWidth() + WINDOW_SHADOW_SIZE, 
				w.getHeight() + WINDOW_SHADOW_SIZE, 
				FRAME_BASE);
	}
	
	public void renderWindowTitleBar(Graphics gfx, WindowTitleBar w)
	{
		int x = w.getAbsoluteX();
		int y = w.getAbsoluteY();
		int b = WindowTitleBar.height;
		
		// Left
		gfx.drawImage(windowTitleBarBackground.getSprite(0, 0), x, y);
		// Middle
		renderImageRepeatXY(gfx, windowTitleBarBackground.getSprite(1, 0), x + b, y, w.getWidth() - 3 * b, b);
		// Right
		gfx.drawImage(windowTitleBarBackground.getSprite(2, 0), x + w.getWidth() - 2 * b, y);
		
		// Title
		if(w.getText() != null)
		{
			gfx.setColor(Color.white);
			gfx.drawString(w.getText(), x + b, y + 2);
		}
	}

	public void renderWindowCloseButton(Graphics gfx, WindowCloseButton w)
	{
		renderButton(gfx, w, Content.images.uiWindowCloseButton, null, null, 0);
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
		renderButton(gfx, w, Content.images.uiMenuItem, null, w.getText(), 1);
	}
	
	public void renderToolButton(Graphics gfx, ToolButton w)
	{
		renderButton(gfx, w, Content.images.uiToolButton, w.icon, null, 2);
	}

	public void renderPushButton(Graphics gfx, PushButton w)
	{
		renderButton(gfx, w, Content.images.uiPushButton, null, w.getText(), 1);
	}

	public int getGlobalScale()
	{
		return globalScale;
	}


}

