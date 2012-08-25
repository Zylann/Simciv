package simciv.rendering;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Game;
import simciv.IRenderable;
import simciv.MapGrid;

/**
 * Renders a line of natural elements
 * @author Marc
 *
 */
public class RenderNatureElements implements IRenderable
{
	MapGrid mapRef;
	int startX;
	int y;
	int maxX;
	
	public RenderNatureElements(MapGrid map, int startX, int y, int maxX)
	{
		this.startX = startX;
		this.y = y;
		this.maxX = maxX;
		this.mapRef = map;
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		for(int x = startX; x <= maxX; x++)
		{
			if(mapRef.contains(x, y))
				mapRef.getCellExisting(x, y).renderNatureElement(Game.tilesSize * x, Game.tilesSize * y, gfx);
		}
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}

	@Override
	public int getDrawOrder()
	{
		return y;
	}
}
