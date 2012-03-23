package simciv.units;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import simciv.Road;
import simciv.Workplace;
import simciv.World;
import simciv.buildings.Building;
import simciv.buildings.House;

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
	private static Image sprite = null;
	
	Building buildingRef; // reference to the building the citizen currently is in
	Workplace workplaceRef;
	House houseRef;

	public static final void loadContent() throws SlickException
	{
		sprite = new Image("data/citizen.png");
		sprite.setFilter(Image.FILTER_NEAREST);
	}

	public Citizen(World w)
	{
		super(w);
	}
	
	@Override
	public void render(Graphics gfx)
	{
		defaultRender(gfx, sprite);
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

	@Override
	public void onDestruction()
	{
	}

	@Override
	protected int getTickTime()
	{
		return 500;
	}
}
