package simciv.buildings;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Game;
import simciv.World;
import simciv.content.Content;
import simciv.jobs.InternalJob;
import simciv.jobs.Job;
import simciv.jobs.MarketDelivery;
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
		state = Building.NORMAL;
		if(sprites == null)
		{
			sprites = new SpriteSheet(Content.images.buildMarket,
					getWidth() * Game.tilesSize,
					(getHeight() + getZHeight()) * Game.tilesSize);
		}
	}

	@Override
	public void renderBuilding(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		renderDefault(gfx, sprites);
	}

	@Override
	public int getProductionProgress()
	{
		return 0; // Markets are not productive buildings
	}

	@Override
	public Job giveNextJob(Citizen citizen)
	{
		if(needEmployees())
		{
			Job job;
			if(employees.size() < 3) // 3 internals,
				job = new InternalJob(citizen, this, Job.MARKET_INTERNAL);
			else // and 3 delivery men
				job = new MarketDelivery(citizen, this);
			
			addEmployee(citizen);
			return job;
		}
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
				sendDelivery();
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

	private void sendDelivery()
	{
		for(Citizen emp : employees.values())
		{
			if(!emp.isOut() && emp.getJob().getID() == Job.MARKET_DELIVERY)
			{				
				emp.exitBuilding();
			}
		}
	}

}



