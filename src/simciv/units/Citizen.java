package simciv.units;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import simciv.ContentManager;
import simciv.Game;
import simciv.Job;
import simciv.World;
import simciv.buildings.Building;
import simciv.buildings.House;
import simciv.buildings.Workplace;
import simciv.effects.RisingIcon;
import simciv.movements.RandomRoadMovement;

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
	private static SpriteSheet thinkingAnim = null;
	public static int totalCount = 0;
	
	private Building buildingRef; // reference to the building the citizen currently is in
	private House houseRef; // if null, the Citizen is homeless
	private Job job; // Job of the Citizen
	private int tickTime; // Tick time interval in milliseconds

	public Citizen(World w)
	{
		super(w);
		if(sprite == null)
			sprite = ContentManager.instance().getImage("unit.citizen");			
		if(thinkingAnim == null)
		{
			Image thinkingSprite = ContentManager.instance().getImage("unit.thinking");
			int b = thinkingSprite.getHeight();
			thinkingAnim = new SpriteSheet(thinkingSprite, b, b);
		}
		// Each citizen have a slightly different tickTime
		tickTime = 500 + (int)(100.f * Math.random()) - 50;
		
		setMovement(new RandomRoadMovement());
	}
	
	@Override
	protected void renderUnit(Graphics gfx)
	{
		if(job == null)
			defaultRender(gfx, sprite);
		else
			job.renderUnit(gfx);
		if(state == Unit.THINKING)
			renderThinkingIcon(gfx);
	}
	
	public void renderThinkingIcon(Graphics gfx)
	{
		int gx = Game.tilesSize - thinkingAnim.getWidth() / thinkingAnim.getHorizontalCount();
		gfx.drawImage(thinkingAnim.getSprite(getTicks() % 2 == 0 ? 0 : 1, 0), gx, 0);
	}

	@Override
	public void tick()
	{
		if(job == null)
			searchJob();
		else
			job.tick();
	}
	
	public Job getJob()
	{
		return job;
	}
	
	/**
	 * Searches a job by querying all buildings around the current position.
	 * (Will modify the job attribute.)
	 */
	private void searchJob()
	{
		if(job != null)
			return;
		ArrayList<Building> builds = worldRef.getBuildingsAround(posX, posY);
		for(Building b : builds)
		{
			if(b.isWorkplace())
			{
				Workplace workplace = (Workplace)b;
				if(workplace.needEmployees())
				{
					job = workplace.giveNextJob(this);
					if(job != null) // I got the job !
					{
						job.onBegin();
						// Visual feedback
						worldRef.addGraphicalEffect(
								new RisingIcon(
										workplace.getX(), workplace.getY(),
										ContentManager.instance().getImage("effect.greenStar")));
						break; // stop searching
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
	public boolean enterBuilding(Building b)
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
	public boolean exitBuilding()
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
		totalCount--;
	}

	@Override
	protected int getTickTime()
	{
		return tickTime;
	}

	@Override
	public void onInit()
	{
		totalCount++;
	}
	
}
