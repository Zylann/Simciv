package simciv.units;

import java.util.List;

import org.newdawn.slick.Graphics;

import simciv.Map;
import simciv.ResourceSlot;
import simciv.builds.Build;
import simciv.builds.House;
import simciv.builds.Warehouse;
import simciv.builds.Workplace;
import simciv.content.Content;
import simciv.maptargets.WarehouseForMarketMapTarget;
import simciv.movements.RandomRoadMovement;

/**
 * Deliver resources to citizen houses
 * @author Marc
 *
 */
public class MarketDelivery extends Citizen
{
	private static final long serialVersionUID = 1L;
	
	private ResourceSlot carriedResource;
	
	public MarketDelivery(Map m, Workplace workplace)
	{
		super(m, workplace);
		//setMovement(new RandomRoadMovement());
		carriedResource = new ResourceSlot();		
	}
	
	public void addResourceCarriage(ResourceSlot r)
	{
		carriedResource.addAllFrom(r);
	}
	
	@Override
	public void tick()
	{
		if(getState() == Unit.THINKING)
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
		
		if(carriedResource.isEmpty() && !isMovement())
		{
			findAndGoTo(new WarehouseForMarketMapTarget());
		}
		else
		{
			boolean wasEmpty = carriedResource.isEmpty();
			distributeResources();
			if(!wasEmpty && carriedResource.isEmpty())
			{
				findAndGoTo(new WarehouseForMarketMapTarget());
			}
			else
			{				
				if(isMovementBlocked())
					findAndGoTo(getMovementTarget());
				else if(isMovementFinished())
				{
					retrieveResourcesIfPossible();
					if(carriedResource.isEmpty())
						findAndGoTo(getMovementTarget());
					else
						setMovement(new RandomRoadMovement());
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
		List<Build> buildings = mapRef.getBuildsAround(getX(), getY());
		for(Build b : buildings)
		{
			if(b.isHouse())
				((House)b).onDistributedResource(carriedResource);
		}
	}

	private void retrieveResourcesIfPossible()
	{
		List<Build> buildings = mapRef.getBuildsAround(getX(), getY());
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
			defaultRender(gfx, Content.sprites.unitMarketDelivery, 4); // render without bag
		else
			defaultRender(gfx, Content.sprites.unitMarketDelivery); // render with a bag
	}

	@Override
	public byte getJobID()
	{
		return Job.MARKET_DELIVERY;
	}

}


