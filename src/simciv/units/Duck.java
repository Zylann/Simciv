package simciv.units;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import simciv.ContentManager;
import simciv.Direction2D;
import simciv.World;

public class Duck extends Unit
{
	private static Image sprite;
	
	protected int ticksBeforeNextMove;
	protected int ticksBeforeNextStop;
	
	public Duck(World w)
	{
		super(w);
		if(sprite == null)
			sprite = ContentManager.instance().getImage("unit.duck");
		ticksBeforeNextMove = (int) (5 + 10.f * Math.random());
	}

	@Override
	protected void renderUnit(Graphics gfx)
	{
		defaultRender(gfx, sprite);
	}

	@Override
	protected void tick()
	{
		if(ticksBeforeNextStop > 0)
		{
			ticksBeforeNextStop--;
			move(worldRef.map.getAvailableDirections(posX, posY));
		}
		else
		{
			setDirection(Direction2D.random());
			ticksBeforeNextMove--;
			if(ticksBeforeNextMove == 0)
			{
				ticksBeforeNextMove = (int) (5 + 10.f * Math.random());
				ticksBeforeNextStop = (int) (5 + 10.f * Math.random());
			}
		}
	}

	@Override
	public void onInit()
	{
	}

	@Override
	public void onDestruction()
	{
	}

}


