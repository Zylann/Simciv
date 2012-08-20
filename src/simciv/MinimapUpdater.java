package simciv;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.SlickException;

import simciv.content.Content;

/**
 * Updates an image representing a map where one cell is a pixel
 * @author Marc
 *
 */
public class MinimapUpdater implements IMapListener
{
	private static Image mapMask;
	private static final int MASK_BASE = 16; // in pixels
	private static final int VIZ_UPDATE_TIME = 1000; // in ms
	
	private ImageBuffer pixels;
	private Image viz;
	private int nextVizUpdateTime; // in ms
	
	public MinimapUpdater(World w) throws SlickException
	{
		pixels = new ImageBuffer(w.map.getWidth(), w.map.getHeight());
		viz = pixels.getImage(Image.FILTER_NEAREST);
		
		mapMask = Content.images.uiMinimapMask;
	}
	
	public void update(int delta) throws SlickException
	{
		nextVizUpdateTime -= delta;
		if(nextVizUpdateTime <= 0)
		{
			viz = pixels.getImage(Image.FILTER_NEAREST);
			nextVizUpdateTime = VIZ_UPDATE_TIME;
		}
	}
	
	public void updateCompleteViz(Map m)
	{
		for(int y = 0; y < m.getHeight(); y++)
		{
			for(int x = 0; x < m.getWidth(); x++)
				updateCellExisting(m.getCellExisting(x, y), x, y);
		}
	}
	
	public void updateCellExisting(MapCell cell, int x, int y)
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
				
		Color clr = cell.getMinimapColor();
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

	@Override
	public void onCellChange(MapCell cell, int x, int y)
	{
		updateCellExisting(cell, x, y);
	}
	
}
