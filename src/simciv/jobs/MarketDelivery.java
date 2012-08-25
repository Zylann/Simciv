package simciv.jobs;

import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

import simciv.Game;
import simciv.ResourceSlot;
import simciv.builds.Build;
import simciv.builds.House;
import simciv.builds.Warehouse;
import simciv.builds.Workplace;
import simciv.content.Content;
import simciv.maptargets.WarehouseForMarketMapTarget;
import simciv.movements.RandomRoadMovement;
import simciv.units.Citizen;
import simciv.units.Unit;

/**
 * Deliver resources to citizen houses
 * @author Marc
 *
 */
public class MarketDelivery extends Job
{
	private static SpriteSheet unitSprites;
	
	private ResourceSlot carriedResource;
	
	public MarketDelivery(Citizen citizen, Workplace workplace)
	{
		super(citizen, workplace);
		carriedResource = new ResourceSlot();
		if(unitSprites == null)
			unitSprites = new SpriteSheet(Content.images.unitMarketDelivery, Game.tilesSize, Game.tilesSize);
	}

	@Override
	public void onBegin()
	{
		me.enterBuilding(workplaceRef);
	}
	
	public void addResourceCarriage(ResourceSlot r)
	{
		carriedResource.addAllFrom(r);
	}
	
	@Override
	public void tick()
	{
		if(!me.isOut())
			return;
		
		if(me.getState() == Unit.THINKING)
			return;

		/*
		 * Behavior :
		 * 1) Begins mission at workplace ;
		 * 2) If have resources, go to 5).
		 * 3) Go to a valid warehouse
		 * 4) Get food from the warehouse ;
		 * 5) Distribute resources at random while carrying it ;
		 * go to 2).
		 */
		
		if(carriedResource.isEmpty() && !me.isMovement())
		{
			me.findAndGoTo(new WarehouseForMarketMapTarget());
		}
		else
		{
			boolean wasEmpty = carriedResource.isEmpty();
			distributeResources();
			if(!wasEmpty && carriedResource.isEmpty())
			{
				me.findAndGoTo(new WarehouseForMarketMapTarget());
			}
			else
			{				
				if(me.isMovementBlocked())
					me.findAndGoTo(me.getMovementTarget());
				else if(me.isMovementFinished())
				{
					retrieveResourcesIfPossible();
					if(carriedResource.isEmpty())
						me.findAndGoTo(me.getMovementTarget());
					else
						me.setMovement(new RandomRoadMovement());
				}
			}
		}
	}
	
	/**
	 * Distributes resources to each house nearby
	 */
	private void distributeResources()
	{
		if(carriedResource.isEmpty())
			return;
		List<Build> buildings = me.getMap().getBuildsAround(me.getX(), me.getY());
		for(Build b : buildings)
		{
			if(b.isHouse())
				((House)b).onDistributedResource(carriedResource);
		}
	}

	private void retrieveResourcesIfPossible()
	{
		List<Build> buildings = me.getMap().getBuildsAround(me.getX(), me.getY());
		for(Build b : buildings)
		{
			if(Warehouse.class.isInstance(b))
			{
				Warehouse w = (Warehouse)b;
				w.retrieveResource(carriedResource);
			}
			if(carriedResource.isFull())
				return;
		}
	}

	@Override
	public void renderUnit(Graphics gfx)
	{
		if(carriedResource.isEmpty())
			me.defaultRender(gfx, unitSprites, 4); // render without bag
		else
			me.defaultRender(gfx, unitSprites); // render with a bag
	}

	@Override
	public byte getID()
	{
		return Job.MARKET_DELIVERY;
	}

	@Override
	public int getIncome()
	{
		return 14;
	}

}


