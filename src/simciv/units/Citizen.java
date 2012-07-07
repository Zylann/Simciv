package simciv.units;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import simciv.ContentManager;
import simciv.Job;
import simciv.Road;
import simciv.World;
import simciv.buildings.Building;
import simciv.buildings.House;
import simciv.buildings.Workplace;

/**
 * A citizen is a city member.
 * He lives in a House and can have a job.
 * If his house get destroyed, he die.
 * If he lose his job, he has to find another one.
 * @author Marc
 *
 */
public class Citizen extends Unit
{
	private static Image sprite = null; // default appearance
	
	Building buildingRef; // reference to the building the citizen currently is in
	Workplace workplaceRef; // if null, the Citizen is redundant
	House houseRef; // if null, the Citizen is homeless
	Job job; // Job of the Citizen
	int tickTime; // Tick time interval in milliseconds

	public Citizen(World w)
	{
		super(w);
		if(sprite == null)
			sprite = ContentManager.instance().getImage("city.citizen");			
		// Each citizen have a slightly different tickTime
		tickTime = 500 + (int)(100.f * Math.random()) - 50;
	}
	
	@Override
	public void render(Graphics gfx)
	{
		if(job == null)
			defaultRender(gfx, sprite);
		else
			defaultRender(gfx, job.getSprites());
	}

	@Override
	public void tick()
	{
		move(Road.getAvailableDirections(worldRef.map, posX, posY));
	}
	
	public void setHouse(House h)
	{
		houseRef = h;
	}
	
	public void setWorkplace(Workplace wp)
	{
		workplaceRef = wp;
	}

	@Override
	public boolean isOut()
	{
		return buildingRef == null;
	}

	/**
	 * Makes the unit come in a building
	 * Note : the building's units list is not affected.
	 * @param b : building
	 * @return true if success
	 */
	protected boolean enterBuilding(Building b, boolean keepReference)
	{
		if(buildingRef != null)
			return false;
		buildingRef = b;
		return true;
		//return buildingRef.addCitizen(this);
	}
	
	/**
	 * Makes the unit come out a building
	 * @return true if the unit was in a building
	 */
	protected boolean exitBuilding()
	{
		if(buildingRef == null)
			return false;
		buildingRef = null;
		return true;
		//return buildingRef.removeCitizen(getID());
	}

	/**
	 * When a Citizen is destroyed, it must also be removed from his
	 * workplace, his house and eventually the building where he currently is.
	 */
	@Override
	public void onDestruction()
	{
		exitBuilding();
		if(workplaceRef != null)
			workplaceRef.removeEmployee(this.getID());
		if(houseRef != null)
			houseRef.removeInhabitant(this.getID());
	}

	@Override
	protected int getTickTime()
	{
		return tickTime;
	}
}
