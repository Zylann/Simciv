package simciv.builds;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import simciv.Game;
import simciv.Map;
import simciv.Resource;
import simciv.ResourceSlot;
import simciv.content.Content;
import simciv.units.Conveyer;
import simciv.units.Jobs;
import simciv.units.Unit;

public class Hunters extends Workplace
{
	private static final long serialVersionUID = 1L;
	private static BuildProperties properties;
	private static int TICKS_PER_FOOD_PRODUCTION = 8;
	
	static
	{
		properties = new BuildProperties("Hunters");
		properties.setCategory(BuildCategory.FOOD)
			.setCost(100)
			.setSize(2, 2, 1)
			.setUnitsCapacity(4);
	}
	
	private boolean producing;
	private int ticksBeforeFoodExport;

	public Hunters(Map m)
	{
		super(m);
	}

	@Override
	public int getProductionProgress()
	{
		if(producing)
		{
			return (int) (100.f * (1.f - 
					(float)ticksBeforeFoodExport 
					/ (float)TICKS_PER_FOOD_PRODUCTION));
		}
		return 0;
	}
	
	private void sendHunters()
	{
		addAndSpawnUnitsAround(Jobs.HUNTER, 1);
	}

	@Override
	protected void onActivityStart()
	{
		sendHunters();
	}
	
	@Override
	protected void onActivityStop()
	{
		removeAllUnits();
		producing = false;
		ticksBeforeFoodExport = 0;
	}

	@Override
	protected void tickActivity()
	{
		if(producing)
		{
			ticksBeforeFoodExport--;
			if(ticksBeforeFoodExport <= 0)
			{
				exportFood();
				sendHunters();
			}
		}
	}
	
	private boolean exportFood()
	{
		for(Integer uID : units)
		{
			Unit u = mapRef.getUnit(uID);
			if(Conveyer.class.isInstance(u)) {
				Log.debug(this + " can't export food, the conveyer is already out");
				return false;
			}
		}
		
		Conveyer conveyers[] = new Conveyer[1];
		conveyers[0] = new Conveyer(mapRef, this);
		conveyers[0].addResourceCarriage(new ResourceSlot(Resource.MEAT, 50));
		
		addAndSpawnUnitsAround(conveyers);
		
		ticksBeforeFoodExport = 0;
		producing = false;

		return true;
	}

	@Override
	public BuildProperties getProperties()
	{
		return properties;
	}

	@Override
	protected void renderBuild(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		SpriteSheet sprites = Content.sprites.buildHunters;
		if(isActive())
		{
			if(producing)
				gfx.drawImage(sprites.getSprite(2, 0), 0, -Game.tilesSize);
			else
				gfx.drawImage(sprites.getSprite(1, 0), 0, -Game.tilesSize);
		}
		else
			gfx.drawImage(sprites.getSprite(0, 0), 0, -Game.tilesSize);
	}

	public void onHunterBackWithPrey()
	{
		producing = true;
		ticksBeforeFoodExport = TICKS_PER_FOOD_PRODUCTION;
	}

}


