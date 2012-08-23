package simciv.builds;

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
public class Market extends PassiveWorkplace
{
	private static SpriteSheet sprites;
	private static BuildProperties properties;
	
	static
	{
		properties = new BuildProperties("Market");
		properties.setCost(50).setSize(2, 2, 1).setUnitsCapacity(6).setCategory(BuildCategory.MARKETING);
	}
	
	public Market(World w)
	{
		super(w);
		state = Build.STATE_NORMAL;
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
	public BuildProperties getProperties()
	{
		return properties;
	}

	@Override
	protected void onActivityStart()
	{
		sendDelivery();
	}

	@Override
	protected void onActivityStop()
	{
	}
	
	private void sendDelivery()
	{
		for(Citizen emp : employees.values())
		{
			if(!emp.isOut() && emp.getJob().getID() == Job.MARKET_DELIVERY)
				emp.exitBuilding();
		}
	}

}



