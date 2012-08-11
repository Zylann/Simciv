package simciv.ui.base;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import simciv.content.Content;

/**
 * Simple text/image display widget
 * @author Marc
 *
 */
public class Label extends BasicWidget
{
	private Image image;
	private String text;
	
	public Label(Widget parent, int x, int y, Image image)
	{
		super(parent, x, y, 0, 0);
		setImage(image);
	}
	
	public Label(Widget parent, int x, int y, String text)
	{
		super(parent, x, y, 0, 0);
		setText(text);
	}
	
	public void setImage(Image image)
	{
		this.image = image;
		updateSize();
	}
	
	public void setText(String text)
	{
		this.text = text;
		updateSize();
	}
	
	public String getText()
	{
		return text;
	}
	
	public Image getImage()
	{
		return image;
	}
	
	/**
	 * Sets the size of the widget from its content (text + image)
	 */
	public void updateSize()
	{
		int w = width;
		int h = height;
		
		if(text != null)
		{
			int tw = Content.globalFont.getWidth(text);
			int th = Content.globalFont.getHeight(text);
			if(tw > w)
				w = tw;
			if(th > h)
				h = th;
		}
		
		if(image != null)
		{
			if(image.getWidth() > w)
				w = image.getWidth();
			if(image.getHeight() > h)
				h = image.getHeight();
		}
		
		setSize(w, h);		
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		UIRenderer.instance().renderLabel(gfx, this);
	}

}


