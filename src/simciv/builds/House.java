package simciv.builds;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import backend.MathHelper;
import backend.geom.Vector2i;
import backend.pathfinding.IMapSpec;

import simciv.Cheats;
import simciv.Entity;
import simciv.Game;
import simciv.Resource;
import simciv.ResourceBag;
import simciv.ResourceSlot;
import simciv.Map;
import simciv.content.Content;
import simciv.effects.RisingIcon;
import simciv.maptargets.RoadMapTarget;
import simciv.units.Robber;

/**
 * Houses produce citizen. They can evoluate with some conditions.
 * There is no object to hold citizen information : houses just have a population counter.
 * If they get a job, the corresponding workplace gets referenced.
 * The number of workplaces referenced this way musn't exceed the population counter.
 * Each workplace reference means that 1 inhabitant is working here.
 * Then, the same workplace might be referenced more than one time.
 * @author Marc
 *
 */
public class House extends Build
{
	private static final long serialVersionUID = 1L;

	private static BuildProperties properties[];
	private static byte MAX_LEVEL = 1; // Note : in code, the first level is 0.
	
	// States
	public static final byte CONSTRUCTING = 0;
	
	// Feed levels (in house ticks)
	public static final int FEED_MAX = 200;
	public static final int FEED_HUNGRY = 125;
	public static final int FEED_STARVING = 50;
	public static final int FEED_MIN = 0;
	
	// Initial food units per inhabitant
	private static final int INITIAL_FOOD_PER_INHABITANT = 7;
	
	// Criminality levels
	public static final byte CRIME_MIN = 0;
	public static final byte CRIME_MAX = 10;

	static
	{
		properties = new BuildProperties[MAX_LEVEL+1];
		
		properties[0] = new BuildProperties("House lv.1")
			.setUnitsCapacity(1).setCost(10).setSize(1, 1, 1)
			.setCategory(BuildCategory.HOUSES)
			.setRepeatable(true);
		
		properties[1] = new BuildProperties("House lv.2")
			.setUnitsCapacity(5).setCost(50).setSize(2, 2, 2)
			.setCategory(BuildCategory.HOUSES);
	}

	/** Social level of the house **/
	private byte level;
	
	/** How many citizen the house is about to produce **/
	private byte nbCitizensToProduce;
	
	/** How many inhabitants live here **/
	private byte nbInhabitants;
	
	/**
	 * IDs of workplaces where the inhabitants of the house work. Size is <= nbInhabitants. 
	 */
	private ArrayList<Integer> workers;
	
	/** Resources owned by the house (food...) **/
	private ResourceBag resources;
	
	/** Feed level of the house. determines when food is consumed. **/
	private int feedLevel;
	
	/** Criminality level. **/
	private byte crimeLevel;
	
	/** Is the house been taxed this month? **/
	private boolean beenTaxed;

	public House(Map m)
	{
		super(m);
		direction = (byte) (4 * Math.random());
		level = 0;
		nbCitizensToProduce = 1;
		state = CONSTRUCTING;
		workers = new ArrayList<Integer>();
		resources = new ResourceBag();
		crimeLevel = CRIME_MIN;
	}
	
	@Override
	public boolean isHouse()
	{
		return true;
	}
	
	@Override
	protected float getFireRisk()
	{
		if(nbInhabitants == 0)
			return 0;
		else
			return super.getFireRisk();
	}

	public boolean isBeenTaxed()
	{
		return beenTaxed;
	}
	
	public float getTotalSalary()
	{
		float total = 0;
		for(Integer wID : workers)
			total += ((Workplace)mapRef.getBuild(wID)).getSalary();
		return total;
	}
	
	public float payTaxes()
	{
		float totalMoneyCollected =
			getTotalSalary() * mapRef.playerCity.getIncomeTaxRatio();

		if(totalMoneyCollected > 0)
			mapRef.addGraphicalEffect(
				new RisingIcon(getX(), getY(), Content.sprites.effectGold));
		
		beenTaxed = true;
		
		return totalMoneyCollected;
	}

	@Override
	public void tick()
	{
		if(getState() == CONSTRUCTING)
		{
			if((getTicks() > 15 || Cheats.isFastCitizenProduction()))
				setState(Entity.DEFAULT_STATE);
		}
		else if(getState() == Entity.DEFAULT_STATE)
		{
			if(getTicks() % 20 == 0)
			{				
				if(level != MAX_LEVEL)
					tryLevelUp();
				
				if(getNbInhabitants() < getMaxInhabitants())
				{
					byte foodType = resources.getContainedFoodType();
					if(foodType != Resource.NONE)
						nbCitizensToProduce++;
				}
			}
			
			if(nbCitizensToProduce != 0)
				nbCitizensToProduce -= produceCitizens(nbCitizensToProduce);
			
			tickHunger();
			tickCriminality();
		}
		
		if(mapRef.time.isFirstDayOfMonth())
			beenTaxed = false;
	}

	protected void tickHunger()
	{
		if(getNbInhabitants() == 0)
			return;
		
		if(feedLevel > FEED_MIN)
		{
			feedLevel -= getNbInhabitants();
			if(feedLevel < FEED_MIN)
				feedLevel = FEED_MIN;
		}
		
		if(feedLevel <= FEED_HUNGRY)
		{
			byte foodType = resources.getContainedFoodType();
			if(foodType != Resource.NONE)
			{
				resources.subtract(foodType, 1);
				feedLevel = FEED_MAX;
			}
			else
			{
				if(feedLevel <= FEED_STARVING)
				{
					if(feedLevel == FEED_MIN)
					{
						if(Math.random() < 0.02f)
						{
							// Citizen death
							removeInhabitant();
							mapRef.addGraphicalEffect(
								new RisingIcon(
									getX(), getY(),
									Content.sprites.effectDeath));
						}
					}
				}
			}
		}
	}
	
	/**
	 * Puts all the content of this house into another, and make it disappear
	 * @param other
	 */
	protected void mergeTo(House other)
	{
		// Merge inhabitants
		other.nbCitizensToProduce += this.nbCitizensToProduce;
		other.nbInhabitants += this.nbInhabitants;
		nbCitizensToProduce = 0;
		nbInhabitants = 0;
		
		// Merge resources
		other.resources.addAllFrom(resources);
		
		// Update workers
		for(Integer wID : workers)
			((Workplace)mapRef.getBuild(wID)).changeEmployeeHouse(this, other);
		other.workers.addAll(workers);
		
		// Remove this house (now empty)
		dispose();
	}
	
	/**
	 * Tries to level up the house.
	 * If it's possible, the level up is performed and true is returned.
	 * If not, returns false.
	 * @return
	 */
	protected boolean tryLevelUp()
	{
		if(level == 0)
		{
			// Check if 4 1x1 houses are forming a quad
			Build b[] = new Build[3]; // quad neighbors
			int nxy[][] = {{1, 0}, {0, 1}, {1, 1}}; // neighboring
			for(int i = 0; i < 3; i++)
			{
				b[i] = mapRef.getBuild(getX() + nxy[i][0], getY() + nxy[i][1]);
				if(b[i] == null ||
					!b[i].isHouse() ||
					!b[i].is1x1() ||
					b[i].getState() != Entity.DEFAULT_STATE)
				{
					return false;
				}
			}
			
			// Check if the future 2x2 house will have roads nearby
			RoadMapTarget roads = new RoadMapTarget();
			if(mapRef.grid.getPositionsAround(getX(), getY(), 2, 2, roads, mapRef).isEmpty())
				return false;
			
			// Merge houses			
			House housesToMerge[] = new House[3];
			for(int i = 0; i < 3; i++)
				housesToMerge[i] = (House)(b[i]);
			for(House h : housesToMerge)
				h.mergeTo(this);
			
			level++;

			// Mark the map (we know that cells are free, as they were occupied by houses)
			//mapRef.grid.markBuilding(this, true);
			track();
			
			return true;
		}
		return false;
	}
	
	public boolean isRoomForInhabitant()
	{
		return nbInhabitants < getProperties().unitsCapacity;
	}
	
	/**
	 * Produces nbToProduce citizens, following conditions :
	 * - There must be room for them in the house,
	 * - The map must comport roads nearby.
	 * The population total will be increased.
	 * @param nbToProduce : number of citizen to produce
	 * @return the amount effectively produced
	 */
	protected byte produceCitizens(byte nbToProduce)
	{		
		if(nbToProduce <= 0 || !isRoomForInhabitant() || !isRoadNearby())
			return 0; // Cannot produce citizen
		
		// Produce citizens
		byte oldNbInhabitants = nbInhabitants;
		nbInhabitants += nbToProduce;
		if(nbInhabitants > getProperties().unitsCapacity)
			nbInhabitants = (byte) getProperties().unitsCapacity;
		byte nbProduced = (byte) (nbInhabitants - oldNbInhabitants);
		
		// Initial food for new inhabitants
		resources.addAllFrom(new ResourceSlot(
				Resource.WHEAT, INITIAL_FOOD_PER_INHABITANT * nbProduced));
		
		mapRef.playerCity.population += nbProduced;
		
		return nbProduced;
	}
	
	public void addWorker(Workplace w)
	{
		workers.add(w.getID());
	}
	
	/**
	 * Remvoves an inhabitant from the house.
	 * If all of them have a job, the referenced workplaces will be notified.
	 * The population total will be decreased.
	 */
	public void removeInhabitant()
	{
		removeInhabitantWorkingAt(null);
	}

	/**
	 * Removes a working inhabitant from the house.
	 * The workplace will be notified.
	 * The population total will be decreased.
	 * @param workplace : the place where the inhabitant works.
	 * 		If null, this method will only remove an inhabitant, but :
	 * 		if null and there is only workers, one of them will be removed.
	 */
	public void removeInhabitantWorkingAt(Workplace workplace)
	{
		if(nbInhabitants == 0)
			return;
		
		if(workplace != null)
		{
			if(!workers.remove((Object)(workplace.getID())))
				return;
			workplace.removeEmployee(this, false); // false : do not propagate to the house (circular)
		}
		else if(!workers.isEmpty() && workers.size() == nbInhabitants)
		{
			int wID = workers.get(0);
			Workplace w = (Workplace)mapRef.getBuild(wID);
			w.removeEmployee(this, false); // false : do not propagate to the house (circular)
			workers.remove((Object)wID);
		}

		nbInhabitants--;
		mapRef.playerCity.population--;
	}
	
	/**
	 * Removes all the inhabitants of the house.
	 * If they have a job, the corresponding workplaces will be notified.
	 * The population total will be decreased.
	 */
	public void removeAllInhabitants()
	{
		if(nbInhabitants == 0)
			return;
		
		mapRef.playerCity.population -= nbInhabitants;
		nbInhabitants = 0;
		
		removeAllWorkers();
	}
	
	private void removeAllWorkers()
	{
		Log.debug(this + "removing all its workers :");
		for(Integer wID : workers)
		{
			Build b = mapRef.getBuild(wID);
			((Workplace)b).removeEmployee(this, false); // false : do not propagate to the house (circular)
		}
		workers.clear();
	}
	
	/**
	 * Makes a worker living in this house to loose his job.
	 * @param w : corresponding workplace
	 * @param notify : if true, the workplace will be notified.
	 */
	public void removeWorker(Workplace w, boolean notify)
	{
		Log.debug(this + " removing one employee working at " + w);
		workers.remove((Object)(w.getID()));
		if(notify)
			w.removeEmployee(this, false);
	}
	
	/**
	 * @return true if the house has no inhabitants and will not produce them soon.
	 */
	public boolean isAbandonned()
	{
		return nbCitizensToProduce == 0 && nbInhabitants == 0;
	}

	@Override
	public void renderBuild(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		if(getState() == CONSTRUCTING)
			renderAsConstructing(gfx);
		else
		{
			SpriteSheet sprites;			
			int shift = 0;
			
			if(level == 0)
			{
				if(isAbandonned())
					shift = 4;
				sprites = Content.sprites.buildHouseLv1;
				// Note : directionnal sprites are only supported with the first level yet
				gfx.drawImage(sprites.getSprite(direction + shift, 0), 0, -Game.tilesSize);
			}
			else
			{
				if(isAbandonned())
					shift = 1;
				sprites = Content.sprites.buildHouseLv2;
				gfx.drawImage(sprites.getSprite(shift, 0), 0, -Game.tilesSize);
			}
			
			if(gc.getInput().isKeyDown(Input.KEY_3))
				renderHungerRatio(gfx, 0, 0);
			else if(gc.getInput().isKeyDown(Input.KEY_6))
				renderCrimeRatio(gfx);
		}
		
		// debug
//		gfx.setColor(Color.white);
//		gfx.drawString("" + getNbInhabitants(), 0, 0);
	}
	
	private void renderHungerRatio(Graphics gfx, int x, int y)
	{
		float w = (float)(getWidth() * Game.tilesSize - 1);
		float t = getHungerRatio() * w;
		gfx.setColor(Color.green);
		gfx.fillRect(x, y, t, 2);
		gfx.setColor(Color.red);
		gfx.fillRect(x + t, y, w - t, 2);
	}

	@Override
	public BuildProperties getProperties()
	{
		return properties[level];
	}

	@Override
	public void onDestruction()
	{
//		removeAllInhabitants();
//		nbCitizensToProduce = 0;
	}
	
	@Override
	public void onDispose()
	{
		super.onDispose();
		removeAllInhabitants();
		nbCitizensToProduce = 0;
	}

	public int getNbInhabitants()
	{
		return nbInhabitants;
	}
	
	/**
	 * @return how many inhabitants have a job
	 */
	public int getNbWorkers()
	{
		return workers.size();
	}
	
	public int getMaxInhabitants()
	{
		return getProperties().unitsCapacity;
	}

	@Override
	public String getInfoLine()
	{
		String str = "[" + getProperties().name + "] inhabitants : " + getNbInhabitants();
		if(getNbInhabitants() > 0)
			str += ", " + workers.size() + " with a job";
		return str;
	}
	
	/**
	 * Returns true if at least one inhabitant has a job
	 * @return
	 */
	public boolean isInhabitantHaveJob()
	{
		return !workers.isEmpty();
	}
	
	public boolean onDistributedResource(ResourceSlot r)
	{
		// At least one inhabitant of the house must have a job,
		// in order to the others to benefit of resources distribution.
		if(!isInhabitantHaveJob())
			return false;
		
		if(!resources.containsFood() && r.getSpecs().isFood())
		{
			buyResource(r, 4);
			mapRef.addGraphicalEffect(
				new RisingIcon(
					getX(), getY(), Content.sprites.effectGold));
			return true;
		}
		
		return false;
	}
	
	protected void buyResource(ResourceSlot r, int amount)
	{
		resources.addFrom(r, amount);
		mapRef.playerCity.gainMoney(0.5f);
	}
	
	public float getHungerRatio()
	{
		if(resources.containsFood())
			return 1;
		else
			return (float)feedLevel / (float)FEED_MAX;
	}

	@Override
	public boolean onMaintenance()
	{
		if(!isInhabitantHaveJob())
			return false;
		repair();
		return true;
	}
	
	@Override
	public BuildReport getReport()
	{
		BuildReport report = super.getReport();
		
		if(getState() == CONSTRUCTING)
			return report;
		
		if(isAbandonned())
			report.add(BuildReport.PROBLEM_MINOR, "This house is abandonned.");
		else
		{
			if(!resources.containsFood() && getNbInhabitants() != 0)
			{
				String msg = "We need some food !";
				if(getNbWorkers() == 0)
					msg += " But we can't even buy it...";
				report.add(BuildReport.PROBLEM_MAJOR, msg);
			}
			
			if(getWorkersRatio() < 0.5f)
				report.add(BuildReport.PROBLEM_MINOR, "Many of us have no jobs :( ");
		}
		
		return report;
	}

	private float getWorkersRatio()
	{
		return (float)getNbWorkers() / (float)getNbInhabitants();
	}
	
	public float getCrimeRatio()
	{
		return (float)crimeLevel / (float)CRIME_MAX;
	}
	
	public void increaseCrimeLevel(int a)
	{
		if(a < 0)
			return;
		int c = crimeLevel + a;
		if(c > CRIME_MAX)
			c = CRIME_MAX;
		crimeLevel = (byte) c;
	}
	
	public void decreaseCrimeLevel(int d)
	{
		if(d < 0)
			return;
		int c = crimeLevel - d;
		if(c < CRIME_MIN)
			c = CRIME_MIN;
		crimeLevel = (byte) c;
	}
	
	private void tickCriminality()
	{
		if(getTicks() % 8 == 0)
		{
			float workRatio = getWorkersRatio();
//			float foodRatio = this.getHungerRatio();
			
			float increaseChance = 1.f - (0.4f * workRatio + 0.4f);
			
			if(Math.random() < increaseChance)
				increaseCrimeLevel(1);
			else
				decreaseCrimeLevel(1);
		}

		float crimeChance = MathHelper.sq(0.02f * getCrimeRatio());
		
		if(Math.random() < crimeChance)
		{
			List<Vector2i> posList = mapRef.grid.getPositionsAround(
				getX(), getY(), getWidth(), getHeight(), new RoadsTarget());
			
			if(posList.isEmpty())
				return;
			
			Vector2i pos = posList.get(0);
			mapRef.spawnUnit(new Robber(mapRef), pos.x, pos.y);
			Log.debug("Robber spawned at " + pos);
			
			decreaseCrimeLevel(2);
		}		
	}
	
	private void renderCrimeRatio(Graphics gfx)
	{
		renderBar(gfx, getCrimeRatio(), Color.blue, Color.red);
	}
	
	private class RoadsTarget implements IMapSpec
	{
		@Override
		public boolean canPass(int x, int y) {
			return mapRef.grid.isRoad(x, y);
		}		
	}

}




