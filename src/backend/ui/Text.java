package backend.ui;

import java.util.ArrayList;

import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

/**
 * Utility class displaying a long text, and providing simple line wrap
 * @author Marc
 *
 */
public class Text
{
	private String src;
	private ArrayList<String> lines;
	private int maxLineWidth;
	private boolean wrapped;
	private boolean wrapEnabled;
	private Font lastFontUsed;
	
	public Text()
	{
		lines = new ArrayList<String>();
	}
	
	public Text(String str)
	{
		this();
		setFromString(str);
	}
	
	public int getNbLines()
	{
		return lines.size();
	}
	
	public String getSourceText()
	{
		return src;
	}
	
	public boolean isWrapEnabled()
	{
		return wrapEnabled;
	}
	
	public void setWrapEnabled(boolean e)
	{
		wrapEnabled = e;
	}
	
	// TODO editing support

	public void clear()
	{
		src = null;
		lines = new ArrayList<String>();
	}
	
	public boolean isEmpty()
	{
		return lines.isEmpty();
	}
	
	public void setMaxLineWidth(int newMaxWidth)
	{
		if(maxLineWidth != newMaxWidth)
			wrapped = false;
		maxLineWidth = newMaxWidth;
	}
	
	public int getMaxLineWidth()
	{
		return maxLineWidth;
	}
	
	public void wrap(Font font)
	{
		// TODO end-of-line support
		
		if(lines.isEmpty()) {
			wrapped = true;
			return;
		}
		
		lines.clear();
		
		// Split words (see java.util.regex.Pattern)
		String[] words = src.split("\\s"); // any character but spaces
		String line = "";
		
		for(int i = 0; i < words.length; i++)
		{
			if(font.getWidth(line) + font.getWidth(words[i]) < maxLineWidth)
				line += words[i] + " ";
			else {
				lines.add(line);
				line = words[i] + " ";
			}
		}
		
		if(!line.isEmpty())
			lines.add(line);
		
		wrapped = true;
	}
	
	public void setFromString(String str)
	{
		clear();
		src = str;
		lines.add(src == null ? "" : src);
		wrapped = false;
	}
	
	public void render(Graphics gfx, int x, int y)
	{
		if(src == null || src.isEmpty())
			return;
						
		if(wrapEnabled)
		{
			Font font = gfx.getFont();

			if(!wrapped || font != lastFontUsed) {
				wrap(font);
				lastFontUsed = font;
			}
			
			int lh = font.getLineHeight();
			int l = 0;

			for(String line : lines) {
				gfx.drawString(line, x, y + l * lh);
				l++;
			}
		}
		else
			gfx.drawString(src, x, y);
	}

}

