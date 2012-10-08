package simciv.units;

import java.util.List;

import org.newdawn.slick.util.Log;

import backend.pathfinding.IMapTarget;

import simciv.Map;
import simciv.builds.Build;
import simciv.builds.IResourceHolder;
import simciv.builds.Workplace;
import simciv.resources.Resource;

/**
 * A "get" conveyer finds resources and retrieves it back to his workplace.
 * He can be employed in different workplaces.
 * @author Marc
 *
 */
public class GetConveyer extends Conveyer
{
	private static final long serialVersionUID = 1L;
	
	private byte wantedType;
	private int wantedAmount;

	public GetConveyer(Map m, Workplace w)
	{
		super(m, w);
		wantedType = Resource.NONE;
	}
	
	public void setWantedResource(byte resType, int amountWanted)
	{
		this.wantedType = resType;
		this.wantedAmount = amountWanted;
	}

	@Override
	protected IMapTarget getTransactionPlace()
	{
		if(wantedType == Resource.NONE)
		{
			Log.error("Cannot send a GetConveyer without wanted resource type.");
			return null;
		}
		return new StorageTarget();
	}

	@Override
	protected boolean doTransaction()
	{
		List<Build> builds = mapRef.getBuildsAround(getX(), getY());
		
		for(Build b : builds)
		{
			if(IResourceHolder.class.isInstance(b))
			{
				IResourceHolder resHolder = (IResourceHolder)b;
				int a = wantedAmount - carriedResource.getAmount();
				if(resHolder.allowsRetrieving())
					resHolder.retrieve(carriedResource, wantedType, a);
			}
		}
		
		return carriedResource.getAmount() >= wantedAmount;
	}

	@Override
	public String getDisplayableName()
	{
		return "GetConveyer";
	}

	private class StorageTarget implements IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y)
		{
			Build b = mapRef.getBuild(x, y);
			if(b != null && IResourceHolder.class.isInstance(b))
			{
				IResourceHolder rh = (IResourceHolder)b;
				if(rh.allowsRetrieving())
					return rh.getResourceTotal(wantedType) > 0;
			}
			return false;
		}
	}

}
