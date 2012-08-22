package simciv.units;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import simciv.Direction2D;
import simciv.Game;
import simciv.MathHelper;
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
	
	public static final int TICK_TIME_BASIC = 400;
	private static final float TICK_TIME_VARIATION = 100;
	private static final int TICK_TIME_MIN = 200;
	
	// Feed levels (in citizen ticks)
	public static final int FEED_MAX = 400;
	public static final int FEED_HUNGRY = 250;
	public static final int FEED_STARVING = 100;
	public static final int FEED_MIN = 0;
	
	private Building buildingRef; // reference to the building the citizen currently is in
	private House houseRef; // if null, the Citizen is homeless
	private Job job; // Job of the Citizen
	private int tickTimeRandom; // Tick time variation constant in milliseconds
	private int tickTime; // Tick time interval in milliseconds (modified value)
	private boolean beenTaxed; // true if the citizen have been asked to pay his taxes this month
	private int feedLevel;
	private ResourceBag ownedResources;

	public Citizen(World w)
	{
		super(w);
		// Each citizen have a slightly different basic tickTime
		tickTimeRandom = (int)(TICK_TIME_VARIATION * (Math.random() - 0.5f));
		setTickTimeWithRandom(TICK_TIME_BASIC);
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

	public void setTickTimeWithRandom(int newTickTime)
	{
		tickTime = newTickTime + tickTimeRandom;
		if(tickTime < TICK_TIME_MIN)
			tickTime = TICK_TIME_MIN;
	}
	
	private void updateTickTime()
	{
		int btt = isStarving() ? TICK_TIME_BASIC * 2 : TICK_TIME_BASIC;
		if(job != null)
			setTickTimeWithRandom(btt + job.getTickTimeOverride());
		else
			setTickTimeWithRandom(btt);
	}
	
	public boolean isBeenTaxed()
	{
		return beenTaxed;
	}
	
	public float payTax()
	{
		beenTaxed = true;
		if(job != null)
		{
			playPaySound();
			return worldRef.playerCity.getIncomeTaxRatio() * (float)job.getIncome();
		}
		return 0;
	}
	
	protected static void playPaySound()
	{
		Content.sounds.unitPay.play((float) (1.f + MathHelper.randS(0.1f)), 0.05f);
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
	
	public boolean isStarving()
	{
		return feedLevel <= FEED_STARVING;
	}
	
	@Override
	public void tick()
	{
		// Hunger
		tickHunger();
		
		// Taxes
		if(beenTaxed && worldRef.time.isFirstDayOfMonth())
			beenTaxed = false;
		
		// Job
		if(job == null)
			searchJob();
		else
			job.tick();
	}
	
	/**
	 * Updates citizen's hunger.
	 */
	private void tickHunger()
	{
		feedLevel--;
		if(feedLevel <= FEED_HUNGRY)
		{
			byte foodType = ownedResources.getContainedFoodType();
			if(foodType != Resource.NONE)
			{
				ownedResources.subtract(foodType, 1);
				feedLevel = FEED_MAX;
				updateTickTime();
			}
			else if(feedLevel == FEED_STARVING)
			{
				updateTickTime();
			}
			else if(feedLevel == FEED_MIN)
			{
				if(Math.random() < 0.5f)
					kill(); // Death...
				else
					transformToNomad(); // Or quit the city
			}
		}
	}

	@Override
	public void kill()
	{
		super.kill();
		worldRef.addGraphicalEffect(
				new RisingIcon(posX, posY, Content.images.effectDeath));
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
						setTickTimeWithRandom(TICK_TIME_BASIC + job.getTickTimeOverride());
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
	
	/**
	 * Sets the house of the citizen (doesn't update anything else, it's just a basic setter).
	 * @param h
	 */
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
	}
	
	/**
	 * Makes the citizen quit his job.
	 * @param notifyWorkplace : if true, the workplace will be notified
	 * (do not set to true if the workplace has already been updated).
	 */
	public void quitJob(boolean notifyWorkplace)
	{
		if(job != null)
		{
			job.onQuit(notifyWorkplace);
			job = null;
			totalWithJob--;
			updateTickTime();
			setMovement(new RandomRoadMovement());
		}
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
		ownedResources.addAllFrom(new ResourceSlot(Resource.WHEAT, 5));
	}
	
	/**
	 * Returns a ratio representing the hunger status of the citizen.
	 * It will be 1 if the citizen is fine, and a value in [0, 1[ if he is hungry.
	 * @return
	 */
	public float getHungerRatio()
	{
		if(feedLevel > FEED_HUNGRY)
			return 1;
		return (float)feedLevel / (float)FEED_HUNGRY;
	}
	
	/**
	 * Called when a market delivery reaches citizen's house.
	 * (The citizen must have a house)
	 * @param r : resource available to buy
	 * @return true if the citizen bought something, false otherwise
	 */
	public boolean onDistributedResource(ResourceSlot r)
	{
		if(!ownedResources.containsFood() && r.getSpecs().isFood())
		{
			buyResource(r, 4);
			return true;
		}
		return false;
	}
	
	protected void buyResource(ResourceSlot r, int amount)
	{
		ownedResources.addFrom(r, amount);
		worldRef.playerCity.gainMoney(0.5f);
		playPaySound();
	}
	
	/**
	 * Destroys the citizen and spawns a new nomad at the same place
	 */
	public void transformToNomad()
	{
		dispose();
		worldRef.spawnUnit(new Nomad(worldRef), posX, posY);
	}
	
}


