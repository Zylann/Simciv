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
	}
	
	public void update(int delta) throws SlickException
	{
		int nbCellsToUpdate = 5 * delta;
				
		for(int n = 0; n < nbCellsToUpdate; n++)
		{
			Color clr = mapRef.getCellExisting(cursorX, cursorY).getMinimapColor();
			pixels.setRGBA(cursorX, cursorY,
					(int)(255.f * clr.r),
					(int)(255.f * clr.g),
					(int)(255.f * clr.b), 255);
			
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

	public Image getViz()
	{
		return viz;
	}
}
