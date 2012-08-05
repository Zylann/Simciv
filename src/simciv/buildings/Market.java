package simciv.buildings;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Game;
import simciv.World;
import simciv.content.Content;
import simciv.jobs.Job;
import simciv.units.Citizen;

/**
 * A market allows distributing resources to the populaion
 * @author Marc
 *
 */
public class Market extends Workplace
{
	private static SpriteSheet sprites;
	private static BuildingProperties properties;
	
	static
	{
		properties = new BuildingProperties("Market");
		properties.setCost(50).setSize(2, 2, 1).setUnitsCapacity(6);
	}
	
	public Market(World w)
	{
		super(w);
		if(sprites == null)
		{
			sprites = new SpriteSheet(Content.images.buildMarket,
					getWidth() * Game.tilesSize,
					(getHeight() + getZHeight()) * Game.tilesSize);
		}
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		int gx = posX * Game.tilesSize;
		int gy = posY * Game.tilesSize;

		if(state == Building.ACTIVE)
			gfx.drawImage(sprites.getSprite(1, 0), gx, gy - Game.tilesSize);
		else
			gfx.drawImage(sprites.getSprite(0, 0), gx, gy - Game.tilesSize);
	}

	@Override
	public int getProductionProgress()
	{
		return 0; // Markets are not productive buildings
	}

	@Override
	public Job giveNextJob(Citizen citizen)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BuildingProperties getProperties()
	{
		return properties;
	}

	@Override
	protected int getTickTime()
	{
		return 1000;
	}

	@Override
	protected void tick()
	{
		if(state == Building.NORMAL)
		{
			if(!needEmployees())
			{
				state = Building.ACTIVE;
			}
		}
		else if(state == Building.ACTIVE)
		{
			if(needEmployees())
			{
				state = Building.NORMAL;
			}
		}
	}

}



