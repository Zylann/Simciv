package simciv.ui.base;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import simciv.content.Content;

import backend.ui.Text;

/**
 * Simple text/image display widget
 * @author Marc
 *
 */
public class Label extends BasicWidget
{
	private Image image;
	private Text text;
	private Color textColor;
	
	public Label(Widget parent, int x, int y, Image image)
	{
		super(parent, x, y, 0, 0);
		setImage(image);
		textColor = Color.black;
	}
	
	public Label(Widget parent, int x, int y, String text)
	{
		super(parent, x, y, 0, 0);
		setText(text);
	}
	
	public Label(Widget parent, int x, int y, int w, int h, String text)
	{
		super(parent, x, y, w, h);
		setText(text);
		setTextWrap(true);
	}
	
	public Text getText()
	{
		return text;
	}
	
	public void setTextColor(Color clr)
	{
		textColor = clr;
	}
	
	public Color getTextColor()
	{
		return textColor;
	}
	
	public void setImage(Image image)
	{
		this.image = image;
		updateSize();
	}
	
	public void setText(String str)
	{
		if(text == null)
			text = new Text(str);
		else
			text.setFromString(str);
	}
	
	public void setTextWrap(boolean enable)
	{
		if(text != null)
		{
			text.setWrapEnabled(enable);
			text.setMaxLineWidth(getWidth());
		}
	}
		
	@Override
	public void setSize(int x, int y)
	{
		super.setSize(x, y);
		if(text != null)
			text.setMaxLineWidth(getWidth());
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
		
		if(text != null && !text.isWrapEnabled())
		{
			String str = text.getSourceText();
			int tw = Content.globalFont.getWidth(str);
			int th = Content.globalFont.getHeight(str);
			if (tw > w)
				w = tw;
			if (th > h)
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


