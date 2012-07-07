package simciv.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import simciv.ContentManager;

/**
 * Defines how widgets are rendered (defines the graphical theme)
 * @author Marc
 *
 */
public class UIRenderer
{
	protected static UIRenderer instance;
	
	private int globalScale;
	private Image toolButtonBackground;
	private Image menuItemBackground;
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
		if(toolButtonBackground == null)
			toolButtonBackground = ContentManager.instance().getImage("ui.toolButton");
		if(menuItemBackground == null)
			menuItemBackground = ContentManager.instance().getImage("ui.menuItem");
		if(font == null)
			font = ContentManager.instance().getFont("ui.font");
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
		
	public void renderFrame(Graphics gfx, Panel w)
	{
		gfx.setLineWidth(globalScale);
		gfx.setColor(Color.black);
		gfx.drawRect(w.getX(), w.getY(), w.getWidth(), w.getHeight());
	}
	
	private void renderButton(Graphics gfx, Button w, Image sprite, Image icon, String text, int pressOffset)
	{
		int x = w.getAbsoluteX();
		int y = w.getAbsoluteY();
		
		int srcOff = 0;
		if(w.isMouseOver())
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
			gfx.setColor(Color.black);
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

