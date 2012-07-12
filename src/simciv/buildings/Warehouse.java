package simciv.buildings;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

import simciv.ContentManager;
import simciv.Game;
import simciv.Job;
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
	
	// TODO Warehouse: add 8 resource slots

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
		// Floor
		gfx.drawImage(backSprite[i],
				posX * Game.tilesSize,
				posY * Game.tilesSize - 16);
		// Resources
		//...
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
	
	@Override
	public String getInfoString()
	{
		return "[" + getProperties().name + "] employees : " + getNbEmployees() + "/" + getMaxEmployees();
	}

	@Override
	public void onInit()
	{
	}
	
}


