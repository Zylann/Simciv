package simciv.builds;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Game;
import simciv.Resource;
import simciv.ResourceSlot;
import simciv.Map;
import simciv.content.Content;

/**
 * A Warehouse is a building for storing resources.
 * @author Marc
 *
 */
public class Warehouse extends PassiveWorkplace
{
	private static final long serialVersionUID = 1L;
	
	private static BuildProperties properties;
	private static final int NB_SLOTS = 8;
	
	private ResourceSlot resourceSlots[];
	private boolean full;
	private boolean empty;

	static
	{
		properties = new BuildProperties("Warehouse");
		properties.setUnitsCapacity(4).setSize(3, 3, 1).setCost(50).setCategory(BuildCategory.INDUSTRY);
	}
	
	public Warehouse(Map m)
	{
		super(m);
		resourceSlots = new ResourceSlot[NB_SLOTS];
		full = false;
		empty = true;
		
		for(int i = 0; i < resourceSlots.length; i++)
			resourceSlots[i] = new ResourceSlot();		
	}
	
	@Override
	public void onInit()
	{
		super.onInit();
		mapRef.playerCity.registerWarehouse(this);
	}

	@Override
	protected void onDispose()
	{
		super.onDispose();
		mapRef.playerCity.unregisterWarehouse(this);
	}
		
	public boolean isFull()
	{
		return full;
	}
	
	public boolean isEmpty()
	{
		return empty;
	}

	@Override
	public BuildProperties getProperties()
	{
		return properties;
	}
	
	@Override
	public boolean isAcceptResources()
	{
		return !needEmployees() && !full;
	}

	@Override
	public void storeResource(ResourceSlot r)
	{
		// Iterate over slots
		full = true;
		for(ResourceSlot slot : resourceSlots)
		{
			if(!slot.isFull())
			{
				full = false;
				if(!r.isEmpty())
				{
					if(slot.addAllFrom(r))
						empty = false;
				}
			}
		}
	}
	
	/**
	 * Gets a resource from the warehouse until the given slot is full.
	 * @param r
	 * @return true if the given slot has been modified.
	 */
	public boolean retrieveResource(ResourceSlot r)
	{
		boolean retrieved = false;
		empty = true;
		
		for(ResourceSlot slot : resourceSlots)
		{
			if(!r.isFull())
			{
				if(r.addAllFrom(slot))
					retrieved = true;
			}
			if(!slot.isEmpty())
				empty = false;
		}
				
		if(retrieved)
			full = false;
		return retrieved;
	}
	
	/**
	 * Computes the free space for storing the given type of resource.
	 * It may differ depending on the resource type.
	 * @param type : resource type
	 * @return free space in the warehouse
	 */
	public int getFreeSpaceForResource(byte type)
	{
		int stackLimit = Resource.get(type).getStackLimit();
		
		if(empty)
			return stackLimit * resourceSlots.length;
		if(full)
			return 0;
		
		int space = 0;
		for(ResourceSlot s : resourceSlots)
		{
			if(s.isEmpty())
				space += stackLimit;
			else if(s.getType() == type)
				space += s.getFreeSpace();
		}
		
		return space;
	}
	
	/**
	 * Counts how many resources of the given type are contained in the warehouse
	 * @param type : resource type
	 * @return total in units
	 */
	public int getResourceTotal(byte type)
	{
		int total = 0;		
		for(ResourceSlot s : resourceSlots)
		{
			if(s.getType() == type)
				total += s.getAmount();
		}
		return total;
	}
	
	@Override
	public void renderBuild(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		// Floor
		if(isActive())
			gfx.drawImage(Content.sprites.buildWarehouse.getSprite(1, 0), 0, -Game.tilesSize);
		else
			gfx.drawImage(Content.sprites.buildWarehouse.getSprite(0, 0), 0, -Game.tilesSize);
			
		// Resources
		
		renderSlot(gfx, 0, Game.tilesSize, 		0);
		renderSlot(gfx, 1, Game.tilesSize * 2, 	0);
		
		renderSlot(gfx, 2, 0, 					Game.tilesSize);
		renderSlot(gfx, 3, Game.tilesSize, 		Game.tilesSize);
		renderSlot(gfx, 4, Game.tilesSize * 2, 	Game.tilesSize);

		renderSlot(gfx, 5, 0, 					Game.tilesSize * 2);
		renderSlot(gfx, 6, Game.tilesSize, 		Game.tilesSize * 2);
		renderSlot(gfx, 7, Game.tilesSize * 2, 	Game.tilesSize * 2);
		
		// Debug
//		gfx.setColor(Color.white);
//		gfx.drawString("" + getNbEmployees(), 0, 0);
	}
	
	private void renderSlot(Graphics gfx, int i, int gx, int gy)
	{
		resourceSlots[i].renderStorage(gfx, gx, gy);
	}
		
	public int getLoad()
	{
		float k = 0;
		for(ResourceSlot slot : resourceSlots)
			k += slot.getLoadRatio();
		return (int) (100.f * k / (float)NB_SLOTS);
	}
	
	/**
	 * Returns true if it contains resources
	 * @return
	 */
	public boolean containsResources()
	{
		for(ResourceSlot slot : resourceSlots)
		{
			if(!slot.isEmpty())
				return true;
		}
		return false;
	}
	
	/**
	 * Returns true if it contains resources availables for markets
	 * @return
	 */
	public boolean containsResourcesForMarkets()
	{
		return containsResources();
	}
	
	@Override
	public String getInfoLine()
	{
		return super.getInfoLine() + ", load : " + getLoad() + "%";
	}

	@Override
	protected void onActivityStart()
	{
	}

	@Override
	protected void onActivityStop()
	{
	}

	@Override
	public BuildReport getReport()
	{
		BuildReport report = super.getReport();
		
		if(isFull())
			report.add(BuildReport.PROBLEM_MINOR, "This warehouse is completely full.");
		else if(getNbOccupiedSlots() == getNbSlots())
			report.add(BuildReport.PROBLEM_MINOR, "All slots of this warehouse are occupied.");

		return report;
	}
	
	public int getNbSlots()
	{
		return resourceSlots.length;
	}
	
	public int getNbOccupiedSlots()
	{
		int n = 0;
		for(int i = 0; i < resourceSlots.length; i++)
		{
			if(!resourceSlots[i].isEmpty())
				n++;
		}
		return n;
	}

	public boolean canStore(byte resType)
	{
		if(isFull())
			return false;
		return getFreeSpaceForResource(resType) > 0;
	}

}


