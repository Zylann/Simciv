package backend.ui;

import java.util.ArrayList;

import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

/**
 * Utility class displaying a long text, and providing simple line wrap.
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
	
	/**
	 * Gets the number of computed lines.
	 * Will return zero if the text is empty OR if word wrap is not enabled.
	 * @return
	 */
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
		return src.isEmpty();
	}
	
	/**
	 * Sets the max line width for word wrapping.
	 * If the new and old width differ, word wrap will be
	 * recomputed on next rendering if enabled.
	 * @param newMaxWidth
	 */
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
	
	/**
	 * Computes lines using word wrapping.
	 * Each line may have a limited width (maxLineWidth).
	 * @param font : font that will be used for text rendering
	 */
	public void wrap(Font font)
	{
		wrap(font, this.maxLineWidth);
	}
	
	/**
	 * Sets the max line width and computes lines using word wrapping.
	 * Each line may have a limited width.
	 * @param font : font that will be used for text rendering
	 */
	public void wrap(Font font, int lineWidth)
	{
		setMaxLineWidth(lineWidth);

		lines.clear();
		
		if(src.isEmpty()) {
			wrapped = true;
			return;
		}
		
		src += ' ';
		
		String word = "";
		String line = "";
		
		for(int i = 0; i < src.length(); i++)
		{
			char currentChar = src.charAt(i);
			boolean newline = (currentChar == '\n');
			boolean endOfWord = (currentChar == ' ') | newline;

			if(!endOfWord)
			{
				word += currentChar;
			}
			else // End of word
			{
				boolean overflow = (font.getWidth(line) + font.getWidth(word) >= maxLineWidth);
				
				if(!word.isEmpty())
					word += " ";
				
				if(overflow && newline)
				{
					lines.add(line);
					lines.add(word);
					line = new String();
				}
				else if(overflow)
				{
					lines.add(line);
					line = new String(word);
				}
				else if(newline)
				{
					lines.add(line + word);
					line = new String();
				}
				else
				{
					line += word;
				}
				
				word = "";
			}
		}
		
		if(!line.isEmpty())
			lines.add(line);
		
		wrapped = true;
	}
	
	/**
	 * Sets the text from a string.
	 * Word wrap will be recomputed on next rendering if needed.
	 * @param str
	 */
	public void setFromString(String str)
	{
		clear();
		src = str;
		wrapped = false;
	}
	
	/**
	 * Renders the text on the screen at the given position.
	 * If word wrap is enabled and not computed yet,
	 * it is recomputed once using the current font.
	 * @param gfx
	 * @param x
	 * @param y
	 */
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

