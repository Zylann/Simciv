package simciv.buildings;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import simciv.ContentManager;
import simciv.Game;
import simciv.Job;
import simciv.ResourceSlot;
import simciv.World;
import simciv.jobs.InternalJob;
import simciv.units.Citizen;

/**
 * A Warehouse is a building for storing resources.
 * @author Marc
 *
 */
public class Warehouse extends Workplace
{
	private static BuildingProperties properties;
	private static Image backSprite[] = new Image[2]; // inactive, active
	private static int NB_SLOTS = 8;
	
	private ResourceSlot resourceSlots[] = new ResourceSlot[NB_SLOTS];

	static
	{
		properties = new BuildingProperties("Warehouse");
		properties.setUnitsCapacity(4).setSize(3, 3, 1).setCost(100);
	}
	
	public Warehouse(World w)
	{
		super(w);
		if(backSprite[0] == null)
			backSprite[0] = ContentManager.instance().getImage("city.warehouse");
		if(backSprite[1] == null)
			backSprite[1] = ContentManager.instance().getImage("city.activeWarehouse");
		state = Building.NORMAL;
		
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
	
	public void storeResource(ResourceSlot r)
	{
		// Iterate over slots
		for(ResourceSlot slot : resourceSlots)
		{
			slot.addFrom(r);
			if(r.isEmpty())
				break;
		}
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
		int i = state == Building.ACTIVE ? 1 : 0;
		int gx = posX * Game.tilesSize;
		int gy = posY * Game.tilesSize;
		
		// Floor
		
		gfx.drawImage(backSprite[i], gx, gy - Game.tilesSize);
		
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
			k += slot.getLoad();
		return (int) (100.f * k);
	}
	
	@Override
	public String getInfoString()
	{
		return "[" + getProperties().name + "] employees : "
			+ getNbEmployees() + "/" + getMaxEmployees()
			+ ", load : " + getLoad() + "%";
	}

	@Override
	public void onInit()
	{
	}
	
}


