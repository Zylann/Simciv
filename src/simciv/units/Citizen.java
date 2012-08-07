package simciv.units;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import simciv.Direction2D;
import simciv.Game;
import simciv.Resource;
import simciv.ResourceBag;
import simciv.ResourceSlot;
import simciv.World;
import simciv.buildings.Building;
import simciv.buildings.House;
import simciv.buildings.Workplace;
import simciv.effects.RisingIcon;
import simciv.jobs.Job;
import simciv.movements.RandomRoadMovement;

import simciv.content.Content;

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
	private static SpriteSheet thinkingAnim;
	private static SpriteSheet sprites;

	public static int totalCount = 0;
	public static int totalWithJob = 0;
	
	private static final int TICK_TIME_MEAN = 500;
	private static final float TICK_TIME_VARIATION = 100;
	
	// Feed levels (in citizen ticks)
	public static final int FEED_MAX = 350;
	public static final int FEED_HUNGRY = 200;
	public static final int FEED_STARVING = 100;
	public static final int FEED_MIN = 0;
	
	private Building buildingRef; // reference to the building the citizen currently is in
	private House houseRef; // if null, the Citizen is homeless
	private Job job; // Job of the Citizen
	private int basicTickTime; // Tick time interval in milliseconds (basic value)
	private int tickTime; // Tick time interval in milliseconds (modified value)
	private boolean beenTaxed; // true if the citizen have been asked to pay his taxes this month
	private int feedLevel;
	private ResourceBag ownedResources;

	public Citizen(World w)
	{
		super(w);
		// Each citizen have a slightly different basic tickTime
		basicTickTime = TICK_TIME_MEAN + (int)(TICK_TIME_VARIATION * (Math.random() - 0.5f));
		tickTime = basicTickTime;
		setMovement(new RandomRoadMovement());
		beenTaxed = false;
		ownedResources = new ResourceBag();
		feedLevel = FEED_MAX;
		
		if(thinkingAnim == null)
		{
			Image thinkingSprite = Content.images.unitThinking;
			int b = thinkingSprite.getHeight();
			thinkingAnim = new SpriteSheet(thinkingSprite, b, b);
		}
		
		if(sprites == null)
			sprites = new SpriteSheet(Content.images.unitCitizen, Game.tilesSize, Game.tilesSize);
	}
	
	public boolean isBeenTaxed()
	{
		return beenTaxed;
	}
	
	public float payTax()
	{
		beenTaxed = true;
		if(job != null)
			return worldRef.playerCity.getIncomeTaxRatio() * (float)job.getIncome();
		return 0;
	}
	
	@Override
	protected void renderUnit(Graphics gfx)
	{
		if(job == null)
			defaultRender(gfx, sprites);
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
		// Hunger
		if(feedLevel > 0)
			feedLevel--;
		if(feedLevel <= FEED_HUNGRY)
		{
			byte foodType = ownedResources.getContainedFoodType();
			if(foodType != Resource.NONE)
			{
				ownedResources.subtract(foodType, 1);
				feedLevel = FEED_MAX;
			}
			
			if(feedLevel <= FEED_STARVING)
			{
				// TODO reduce citizen perfs
			}
		}
		
		// Taxes
		if(beenTaxed && worldRef.time.getDay() == 0)
			beenTaxed = false;
		
		// Job
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
	 * (Will modify the job attribute if one is found.)
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
					totalWithJob++;
					if(job != null) // I got the job !
					{
						job.onBegin();
						tickTime = basicTickTime + job.getTickTimeOverride();
						// Visual feedback
						worldRef.addGraphicalEffect(
								new RisingIcon(
										workplace.getX(), workplace.getY(),
										Content.images.effectGreenStar));
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
		setMovement(null);
		setDirection(Direction2D.NONE);
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
		totalWithJob--;
		tickTime = basicTickTime;
		state = Unit.NORMAL;
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
		super.onInit();
		totalCount++; // TODO put citizen count in PlayerCity
		
		// Initial resources
		ownedResources.addAllFrom(new ResourceSlot(Resource.WHEAT, 4));
	}
	
	public float getFeedRatio()
	{
		return (float)feedLevel / (float)FEED_MAX;
	}
	
	public boolean onDistributedResource(ResourceSlot r)
	{
		// The citizen must have a job.
		// If he has housemates with a job, the citizen can buy resources.
		if(job == null && !houseRef.isInhabitantHaveJob())
			return false;
		if(!ownedResources.containsFood() && r.getSpecs().isFood())
		{
			buyResource(r, 4);
			return true;
		}
		return false;
	}
		
	protected void buyResource(ResourceSlot r, int amount)
	{
//		int oldSlotAmount = r.getAmount();
		ownedResources.addFrom(r, amount);
//		int diff = oldSlotAmount - r.getAmount();
		worldRef.playerCity.gainMoney(0.5f);
	}
	
}


