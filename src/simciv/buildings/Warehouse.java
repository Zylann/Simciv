package simciv.buildings;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
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
public class Warehouse extends PassiveWorkplace
{
	private static BuildingProperties properties;
	private static SpriteSheet sprites;
	private static int NB_SLOTS = 8;
	
	private ResourceSlot resourceSlots[] = new ResourceSlot[NB_SLOTS];
	private boolean full;

	static
	{
		properties = new BuildingProperties("Warehouse");
		properties.setUnitsCapacity(4).setSize(3, 3, 1).setCost(50).setCategory(BuildCategory.INDUSTRY);
	}
	
	public Warehouse(World w)
	{
		super(w);
		state = Building.STATE_NORMAL;
		full = false;
		
		for(int i = 0; i < resourceSlots.length; i++)
			resourceSlots[i] = new ResourceSlot();
		
		if(sprites == null)
		{
			sprites = new SpriteSheet(Content.images.buildWarehouse,
					getWidth() * Game.tilesSize,
					(getHeight() + getZHeight())* Game.tilesSize);
		}
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
			slot.addAllFrom(r);
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
			if(r.addAllFrom(slot))
				retrieved = true;
			if(r.isFull())
				break;
		}
		if(retrieved)
			full = false;
		return retrieved;
	}

	@Override
	public void renderBuilding(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		// Floor
		if(state == Building.STATE_ACTIVE)
			gfx.drawImage(sprites.getSprite(1, 0), 0, -Game.tilesSize);
		else
			gfx.drawImage(sprites.getSprite(0, 0), 0, -Game.tilesSize);
			
		// Resources
		
		renderSlot(gfx, 0, Game.tilesSize, 		0);
		renderSlot(gfx, 1, Game.tilesSize * 2, 	0);
		
		renderSlot(gfx, 2, 0, 					Game.tilesSize);
		renderSlot(gfx, 3, Game.tilesSize, 		Game.tilesSize);
		renderSlot(gfx, 4, Game.tilesSize * 2, 	Game.tilesSize);

		renderSlot(gfx, 5, 0, 					Game.tilesSize * 2);
		renderSlot(gfx, 6, Game.tilesSize, 		Game.tilesSize * 2);
		renderSlot(gfx, 7, Game.tilesSize * 2, 	Game.tilesSize * 2);
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
		return super.getInfoString() + ", load : " + getLoad() + "%";
	}

	@Override
	protected void onActivityStart()
	{
	}

	@Override
	protected void onActivityStop()
	{
	}
	
}


