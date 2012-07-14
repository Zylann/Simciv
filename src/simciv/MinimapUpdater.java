package simciv;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.SlickException;

/**
 * Updates an image representing a map where one cell is a pixel
 * @author Marc
 *
 */
public class MinimapUpdater
{
	private static Image mapMask;
	private static final int MASK_BASE = 16;
	
	private Map mapRef;
	private ImageBuffer pixels;
	private Image viz;
	private int cursorX;
	private int cursorY;
	
	public MinimapUpdater(Map mapRef) throws SlickException
	{
		this.mapRef = mapRef;
		pixels = new ImageBuffer(mapRef.getWidth(), mapRef.getHeight());
		viz = pixels.getImage(Image.FILTER_NEAREST);
		
		if(mapMask == null)
			mapMask = ContentManager.instance().getImage("ui.minimap.mask");
	}
	
	public void update(int delta) throws SlickException
	{
		int nbCellsToUpdate = 5 * delta;
				
		for(int n = 0; n < nbCellsToUpdate; n++)
		{
			updateCellExisting(cursorX, cursorY);
			
			cursorX++;
			if(cursorX >= mapRef.getWidth())
			{
				cursorX = 0;
				cursorY++;
				if(cursorY >= mapRef.getHeight())
				{
					cursorY = 0;
					viz = pixels.getImage(Image.FILTER_NEAREST);
				}
			}
		}
	}
	
	public void updateCellExisting(int x, int y)
	{		
		int b = MASK_BASE;
		int maskX, maskY;
		
		if(x >= viz.getWidth() - b)
			maskX = b * 2 + x % b;
		else if(x >= b)
			maskX = b + x % b;
		else
			maskX = x;
		
		if(y >= viz.getWidth() - b)
			maskY = b * 2 + y % b;
		else if(y >= b)
			maskY = b + y % b;
		else
			maskY = y;
				
		Color clr = mapRef.getCellExisting(x, y).getMinimapColor();
		clr = clr.multiply(mapMask.getColor(maskX, maskY));
		
		pixels.setRGBA(x, y,
				(int)(255.f * clr.r),
				(int)(255.f * clr.g),
				(int)(255.f * clr.b),
				(int)(255.f * clr.a));
	}

	public Image getViz()
	{
		return viz;
	}
}
