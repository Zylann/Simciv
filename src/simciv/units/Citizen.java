package simciv.units;

import java.util.ArrayList;

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
	
	private Building buildingRef; // reference to the building the citizen currently is in
	private House houseRef; // if null, the Citizen is homeless
	private Job job; // Job of the Citizen
	private int tickTime; // Tick time interval in milliseconds

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
		if(job == null)
		{
			move(Road.getAvailableDirections(worldRef.map, posX, posY));
			searchJob();
		}
		else
			job.tick();
	}
	
	/**
	 * Searches a job by querying all buildings around the current position.
	 * (Will modify the job attribute.)
	 */
	private void searchJob()
	{
		if(job != null)
			return;
		ArrayList<Building> builds = worldRef.map.getBuildingsAround(worldRef, posX, posY);
		for(Building b : builds)
		{
			if(b.isWorkplace())
			{
				Workplace workplace = (Workplace)b;
				if(workplace.needEmployees())
				{
					job = workplace.giveNextJob(this);
					if(job != null)
					{
						job.onBegin();
						break;
					}
				}
			}
		}
	}
	
	public void setHouse(House h)
	{
		houseRef = h;
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
	
	public void quitJob(boolean notifyWorkplace)
	{
		if(job != null)
			job.onQuit(notifyWorkplace);
		job = null;
	}

	/**
	 * When a Citizen is destroyed, it must also be removed from his
	 * workplace, his house and eventually the building where he currently is.
	 */
	@Override
	public void onDestruction()
	{
		exitBuilding();
		quitJob(true); // true : notify the workplace
		if(houseRef != null)
			houseRef.removeInhabitant(this.getID());
	}

	@Override
	protected int getTickTime()
	{
		return tickTime;
	}
}
