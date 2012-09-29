package backend.ui;

import org.newdawn.slick.Color;
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
	private Text text;
	private Color textColor;
	
	public Label(Widget parent, String text)
	{
		super(parent, 0, 0, 0, 0);
		setText(text);
	}
	
	public Label(Widget parent, Image image)
	{
		super(parent, 0, 0, 0, 0);
		setImage(image);
	}

	public Label(Widget parent, int x, int y, Image image)
	{
		super(parent, x, y, 0, 0);
		setImage(image);
	}

	public Label(Widget parent, int x, int y, String text)
	{
		super(parent, x, y, 0, 0);
		textColor = Color.black;
		setText(text);
	}
	
	public Label(Widget parent, int x, int y, int w, int h, String text)
	{
		super(parent, x, y, w, h);
		setText(text, true);
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
			setText(str, false);
		else
			setText(str, text.isWrapEnabled());
	}
	
	public void setText(String str, boolean wrap)
	{
		if(text == null)
			text = new Text(str);
		else
			text.setFromString(str);
		setTextWrap(wrap);
		updateSize();
	}
	
	public void setTextWrap(boolean enable)
	{
		if(text != null)
		{
			text.setMaxLineWidth(getWidth());
			text.setWrapEnabled(enable);
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
		int w = 0;
		int h = 0;
		
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
		
		if(w == 0)
			w = getWidth();
		if(h == 0)
			h = getHeight();
		
		setSize(w, h);		
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		UIRenderer.getTheme().renderLabel(gfx, this);
	}

}


