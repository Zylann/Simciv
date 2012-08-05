package simciv.buildings;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Game;
import simciv.ResourceSlot;
import simciv.World;
import simciv.content.Content;
import simciv.jobs.InternalJob;
import simciv.jobs.Job;
import simciv.units.Citizen;

/**
 * A Warehouse is a building for storing resources.
 * @author Marc
 *
 */
public class Warehouse extends Workplace
{
	private static BuildingProperties properties;
	private static int NB_SLOTS = 8;
	
	private ResourceSlot resourceSlots[] = new ResourceSlot[NB_SLOTS];
	private boolean full;

	static
	{
		properties = new BuildingProperties("Warehouse");
		properties.setUnitsCapacity(4).setSize(3, 3, 1).setCost(50);
	}
	
	public Warehouse(World w)
	{
		super(w);
		state = Building.NORMAL;
		full = false;
		
		for(int i = 0; i < resourceSlots.length; i++)
			resourceSlots[i] = new ResourceSlot();
	}
	
	@Override
	public int getProductionProgress()
	{
		// Warehouses just store resources. They do not produce anything.
		return 0;
	}

	@Override
	public BuildingProperties getProperties()
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
		for(ResourceSlot slot : resourceSlots)
		{
			slot.addFrom(r);
			if(r.isEmpty())
				return;
		}
		full = true;
	}
	
	public boolean retrieveResource(ResourceSlot r)
	{
		boolean retrieved = false;
		for(ResourceSlot slot : resourceSlots)
		{
			if(r.addFrom(slot))
				retrieved = true;
			if(r.isFull())
				break;
		}
		if(retrieved)
			full = false;
		return retrieved;
	}

	@Override
	protected int getTickTime()
	{
		return 500; // 1/2 second
	}

	@Override
	protected void tick()
	{
		if(state == Building.NORMAL)
		{
			if(!needEmployees())
				state = Building.ACTIVE;
		}
		else if(state == Building.ACTIVE)
		{
			if(needEmployees())
				state = Building.NORMAL;
		}
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		int gx = posX * Game.tilesSize;
		int gy = posY * Game.tilesSize;
		
		// Floor
		
		if(state == Building.ACTIVE)
			gfx.drawImage(Content.images.buildActiveWarehouse, gx, gy - Game.tilesSize);
		else
			gfx.drawImage(Content.images.buildInactiveWarehouse, gx, gy - Game.tilesSize);
			
		// Resources
		
		renderSlot(gfx, 0, gx + Game.tilesSize, 	gy);
		renderSlot(gfx, 1, gx + Game.tilesSize * 2, gy);
		
		renderSlot(gfx, 2, gx, 						gy + Game.tilesSize);
		renderSlot(gfx, 3, gx + Game.tilesSize, 	gy + Game.tilesSize);
		renderSlot(gfx, 4, gx + Game.tilesSize * 2, gy + Game.tilesSize);

		renderSlot(gfx, 5, gx, 						gy + Game.tilesSize * 2);
		renderSlot(gfx, 6, gx + Game.tilesSize, 	gy + Game.tilesSize * 2);
		renderSlot(gfx, 7, gx + Game.tilesSize * 2, gy + Game.tilesSize * 2);
	}
	
	private void renderSlot(Graphics gfx, int i, int gx, int gy)
	{
		resourceSlots[i].renderStorage(gfx, gx, gy);
	}
	
	@Override
	public Job giveNextJob(Citizen citizen)
	{
		if(needEmployees())
		{
			Job job = new InternalJob(citizen, this, Job.WAREHOUSE_INTERNAL);
			addEmployee(citizen);
			return job;
		}
		return null;
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
	public String getInfoString()
	{
		return "[" + getProperties().name + "] employees : "
			+ getNbEmployees() + "/" + getMaxEmployees()
			+ ", load : " + getLoad() + "%";
	}
	
}


