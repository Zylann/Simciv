package simciv.buildings;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import simciv.ContentManager;
import simciv.Game;
import simciv.Job;
import simciv.World;
import simciv.units.Citizen;

public class Warehouse extends Workplace
{
	private static BuildingProperties properties;
	private static Image backSprite;
	
	// TODO Warehouse: add 8 resource slots

	static
	{
		properties = new BuildingProperties("Farmland");
		properties.setUnitsCapacity(4).setSize(3, 3, 0).setCost(100);
	}
	
	public Warehouse(World w)
	{
		super(w);
		if(backSprite == null)
			backSprite = ContentManager.instance().getImage("city.warehouse");
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
	}

	@Override
	public void render(Graphics gfx)
	{
		// Floor
		gfx.drawImage(backSprite,
				posX * Game.tilesSize,
				posY * Game.tilesSize - 16);
		// Resources
		//...
	}

	@Override
	public Job giveNextJob(Citizen citizen)
	{
		// TODO give a job
		return null;
	}

}
