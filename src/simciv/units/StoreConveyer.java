package simciv.units;

import java.util.List;

import backend.pathfinding.IMapTarget;
import simciv.Map;
import simciv.builds.Build;
import simciv.builds.IResourceHolder;
import simciv.builds.Warehouse;
import simciv.builds.Workplace;
import simciv.resources.ResourceSlot;

/**
 * A store conveyer stores resources into a warehouse.
 * He can be employed in different workplaces.
 * @author Marc
 *
 */
public class StoreConveyer extends Conveyer
{
	private static final long serialVersionUID = 1L;
	
	public StoreConveyer(Map m, Workplace w)
	{
		super(m, w);
	}

	@Override
	public String getDisplayableName()
	{
		return "StoreConveyer";
	}
	
	@Override
	protected IMapTarget getTransactionPlace()
	{
		return new FreeWarehouseTarget();
	}
	
	public void addResourceCarriage(ResourceSlot r)
	{
		carriedResource.addAllFrom(r);
	}

	@Override
	protected boolean doTransaction()
	{
		distributeResources();
		return carriedResource.isEmpty();
	}

	/**
	 * Distributes resources to neighboring builds if possible
	 */
	private void distributeResources()
	{
		if(carriedResource.isEmpty())
			return;
		List<Build> buildingsAround = getMap().getBuildsAround(getX(), getY());
		for(Build b : buildingsAround)
		{
			if(IResourceHolder.class.isInstance(b))
			{
				IResourceHolder rh = (IResourceHolder)b;
				if(rh.allowsStoring())
					rh.store(carriedResource, -1);
			}
		}
	}
	
	private class FreeWarehouseTarget implements IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y) {
			Build b = mapRef.getBuild(x, y);
			if(b != null && Warehouse.class.isInstance(b))
			{
				Warehouse w = (Warehouse)b;
				return w.allowsStoring() 
					&& w.getFreeSpaceForResource(carriedResource.getType()) > 0;
			}
			return false;
		}
	}

}



