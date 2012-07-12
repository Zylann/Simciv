package simciv.units;

import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Direction2D;
import simciv.Entity;
import simciv.Game;
import simciv.Vector2i;
import simciv.World;

/**
 * An unit can move, and is seen as a "living" thing
 * @author Marc
 *
 */
public abstract class Unit extends Entity
{	
	//public static final byte NORMAL = 1; // what is it?
	public static int count = 0; // Counts all the units
	
	boolean isAlive;
		
	public Unit(World w)
	{
		super(w);
		
		direction = Direction2D.EAST;
		count++;
		isAlive = true;
	}
	
	/**
	 * Moves the unit according to available directions
	 * @param dirs : available directions
	 */
	protected void move(List<Byte> dirs)
	{		
		if(!dirs.isEmpty())
		{
			if(dirs.size() == 1) // only one direction
			{
				direction = dirs.get(0);
			}
			else if(dirs.size() == 2) // two directions
			{
				// remove U-turn
				if(direction != Direction2D.NONE)
					dirs.remove((Byte)Direction2D.opposite[direction]);
				// use the remaining direction
				direction = dirs.get(0);
			}
			else
			{
				// remove U-turn
				if(direction != Direction2D.NONE)
					dirs.remove((Byte)Direction2D.opposite[direction]);
				// Choose a direction at random
				chooseNewDirection(dirs);
			}
		}
		else
			direction = Direction2D.NONE;
		
		// Apply movement
		if(direction != Direction2D.NONE)
		{
			posX += Direction2D.vectors[direction].x;
			posY += Direction2D.vectors[direction].y;
		}
	}
	
	protected void chooseNewDirection(List<Byte> dirs)
	{
		if(!dirs.isEmpty())
		{
			// Choosing a direction at random
			direction = dirs.get((byte) (dirs.size() * Math.random()));
		}
	}
	
	public boolean isAlive()
	{
		return isAlive;
	}
	
	public void kill()
	{
		isAlive = false;
	}
	
	/**
	 * Is the unit outside?
	 * @return
	 */
	public boolean isOut()
	{
		return true;
	}
	
	@Override
	public boolean isVisible()
	{
		return isOut();
	}
		
	protected final void beginRenderForFancyMovements(Graphics gfx)
	{
		if(getDirection() != Direction2D.NONE)
		{
			float k = -Game.tilesSize * getK();
			Vector2i dir = Direction2D.vectors[getDirection()];
			gfx.translate(k * dir.x, k * dir.y);
		}
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		gfx.pushTransform();
		
		if(Game.renderFancyUnitMovements)
			beginRenderForFancyMovements(gfx);
		
		renderUnit(gfx);
		
		gfx.popTransform();
	}

	protected abstract void renderUnit(Graphics gfx);

	@Override
	protected int getTickTime()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	protected final void defaultRender(Graphics gfx, Image sprite)
	{		
		gfx.translate(
				posX * Game.tilesSize,
				posY * Game.tilesSize - Game.tilesSize / 3);

		if(sprite == null)
		{
			// For debug
//			gfx.setLineWidth(1);
//			gfx.setColor(Color.red);
//			gfx.drawRect(0, 0, Game.tilesSize, Game.tilesSize);
			return;
		}
		
		gfx.drawImage(sprite,
				0, 0,
				Game.tilesSize, Game.tilesSize,
				0, direction * Game.tilesSize,
				Game.tilesSize, (direction + 1) * Game.tilesSize);
	}
	
}

