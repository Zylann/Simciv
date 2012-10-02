package simciv.builds;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import backend.MathHelper;
import backend.geom.Vector2i;
import backend.ui.Notification;

import simciv.Cheats;
import simciv.Game;
import simciv.MapGrid;
import simciv.ResourceSlot;
import simciv.Map;
import simciv.TickableEntity;
import simciv.content.Content;
import simciv.effects.SmokeExplosion;
import simciv.maptargets.RoadMapTarget;

// FIXME (rare) sometimes, the map is left unconstructible. Bug origin is unknown...

/**
 * Each object that can be constructed by the player.
 * Buildings cannot move.
 * @author Marc
 *
 */
public abstract class Build extends TickableEntity
{
	private static final long serialVersionUID = 1L;
	
	// Fire levels
	public static final byte FIRE_MIN = 0;
	public static final byte FIRE_SMOKE = 80;
	public static final byte FIRE_BURN = 90;
	public static final byte FIRE_RUINS = 100;
		
	/** Solidness level. The build collapses if it reaches 0. **/
	protected int solidness;
	
	/**
	 * Fire risk information : <br/>
	 * [FIRE_MIN, FIRE_SMOKE[ : no fire <br/>
	 * [FIRE_SMOKE, FIRE_BURN[ : smoke <br/>
	 * [FIRE_BURN, FIRE_RUINS[ : fire is burning <br/>
	 * FIRE_RUINS : fire stops, back to 0
	 */
	protected byte fireLevel;
	
	public Build(Map m)
	{
		super(m);
		solidness = getSolidnessMax();
	}
	
	public abstract BuildProperties getProperties();
	
	public Color getMinimapColor()
	{
		return BuildCategory.get(getProperties().category).getColor();
	}
	
	@Override
	public String getDisplayableName()
	{
		return getProperties().name;
	}
	
	/**
	 * Destroys the building (as gameplay meaning, for example with a bomb) with destruction effects.
	 * Note : to erase a building without "destroying" it, use dispose().
	 */
	public void destroy(boolean burning)
	{
		// Remove the building from the world
		dispose();

		// Make a sound
		Content.sounds.buildCollapse.play((float) (1.0 + MathHelper.randS(0.1f)), 0.5f);
		
		// Leave ruins
		for(int y = getY(); y < getY() + getHeight(); y++)
		{
			for(int x = getX(); x < getX() + getWidth(); x++)
			{
				Ruins d = new Ruins(mapRef, burning);
				d.setPropertiesFromBuild(this);
				mapRef.placeBuild(d, x, y);
				
				if(burning)
					mapRef.playerCity.fireAlerts.registerFire(x, y);
				
				mapRef.addGraphicalEffect(
						new SmokeExplosion(x, y, 8, 1.5f, Game.tilesSize/2));
			}
		}
		
		mapRef.sendNotification(
				Notification.TYPE_WARNING,
				"A build " + (burning ? "burned" : "collapsed") + " !");
	}
		
	@Override
	public void onInit()
	{
		super.onInit();
		mapRef.grid.markBuilding(this, true);
	}
	
	@Override
	protected void onDispose()
	{
		super.onDispose();
		if(isFireBurning())
			mapRef.playerCity.fireAlerts.unregisterFire(getX(), getY());
	}

	@Override
	protected final void track()
	{
		mapRef.grid.markBuilding(this, true);
	}

	@Override
	protected final void untrack()
	{
		mapRef.grid.markBuilding(this, false);
	}

	@Override
	protected void tickEntity()
	{
		tickSolidness();		
		tickFireLevel();
		super.tickEntity();
	}

	protected void tickSolidness()
	{
		if(Math.random() < 0.5)
			solidness--;
		if(solidness == 0)
			destroy(false);
	}
	
	public final boolean needsMaintenance()
	{
		return solidness < getMaintenanceThreshold();
	}
	
	public int getMaintenanceThreshold()
	{
		return 200;
	}
	
	public int getSolidnessMax()
	{
		return 250;
	}
	
	/**
	 * Returns the floating solidness ratio in [0,1].
	 * @return
	 */
	public float getSolidnessRatio()
	{
		return (float)solidness / (float)getSolidnessMax();
	}
	
	/**
	 * Called each time an architect passes near the building.
	 * @return true if success, false if not maintainable
	 */
	public abstract boolean onMaintenance();
	
	/**
	 * Restores the building's HP and solidness
	 */
	public void repair()
	{
		solidness = getSolidnessMax();
		healthPoints = 100;
	}
	
	/**
	 * Returns true if there is at least one road connected to the build
	 * @return
	 */
	protected boolean isRoadNearby()
	{
		RoadMapTarget roads = new RoadMapTarget();
		ArrayList<Vector2i> availablePositions = 
			mapRef.grid.getAvailablePositionsAround(this, roads, mapRef);		
		return !availablePositions.isEmpty();
	}
	
	/**
	 * Returns the on-floor width of the building in cells
	 * @return
	 */
	@Override
	public final int getWidth()
	{
		return getProperties().width;
	}
	
	/**
	 * Returns the on-floor height of the building in cells
	 * @return
	 */
	@Override
	public final int getHeight()
	{
		return getProperties().height;
	}
	
	/**
	 * Returns the 3D-height of the building in cells
	 * (can be used for rendering or future stuff)
	 * @return
	 */
	public final int getZHeight()
	{
		return getProperties().zHeight;
	}
	
	/**
	 * Returns true if the building occupies only one cell on the floor
	 * @return
	 */
	public final boolean is1x1()
	{
		return getWidth() == 1 && getHeight() == 1;
	}
	
	public final void renderAsConstructing(Graphics gfx)
	{
		// TODO handle size upper than 1x1
		gfx.drawImage(Content.sprites.buildConstructing1x1, 0, 0);
	}

	/**
	 * Returns true if the building is a house (class House)
	 * @return
	 */
	public boolean isHouse()
	{
		return false;
	}

	/**
	 * Returns true if the building is a workplace (inherits Workplace)
	 * @return
	 */
	public boolean isWorkplace()
	{
		return false;
	}
	
	@Override
	public boolean isVisible()
	{
		return true;
	}
	
	/**
	 * Returns a brief information about the building, in one line.
	 * @return
	 */
	public abstract String getInfoLine();
	
	/**
	 * Returns a list of problem messages concerning this building.
	 * If no problems, returns an empty report.
	 * @return
	 */
	public ProblemsReport getProblemsReport()
	{
		ProblemsReport problems = new ProblemsReport();
		byte severe = ProblemsReport.SEVERE;
		byte minor = ProblemsReport.MINOR;
		
		if(!isRoadNearby() && !Ruins.class.isInstance(this))
			problems.add(severe, "This build is not connected to a road !");
		
		if(getSolidnessRatio() < 0.2f)
			problems.add(severe, "This build may collapse soon ! We need architects !");
		else if(getSolidnessRatio() < 0.5f)
			problems.add(minor, "This build is creaky. We need architects.");
		
		if(!isFireBurning())
		{
			float r = getFireLevelRatio();
			
			if(r > 0.8f)
				problems.add(severe, "This build may take fire soon ! We need firemen !");
			else if(r > 0.5f)
				problems.add(minor, "The risk of fire is increasing...");
		}
		
		if(isFireBurning())
			problems.add(severe, "It's burning ! We need firemen to save the neighborhood !");
		
		return problems;
	}

	/**
	 * Return true if the building can store resources
	 * @return
	 */
	public boolean isAcceptResources()
	{
		return false;
	}

	/**
	 * Stores a resource in the building. Depending on if the building is accepting
	 * resources, the given slot will or will not be modified.
	 * @param r : resource to store
	 */
	public void storeResource(ResourceSlot r)
	{
	}
	
	@Override
	public final void render(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		gfx.pushTransform();
		gfx.translate(getX() * Game.tilesSize, getY() * Game.tilesSize);
				
		renderBuild(gc, game, gfx);
		
		if(isFireSmoke())
			renderSmoke(gfx);
		
		if(isFireBurning())
			renderFire(gfx);
		
		if(gc.getInput().isKeyDown(Input.KEY_4))
			renderSolidnessRatio(gfx);
		
		if(gc.getInput().isKeyDown(Input.KEY_5))
			renderFireRiskRatio(gfx);

		gfx.popTransform();
	}
	
	/**
	 * Draws a progress bar above the build.
	 * @param gfx : Graphics context
	 * @param r : progress ratio in [0, 1]
	 * @param clr0 : back color
	 * @param clr1 : fill color
	 */
	private void renderBar(Graphics gfx, float r, Color clr0, Color clr1)
	{
		float w = (float)(getWidth() * Game.tilesSize - 1);
		float t = r * w;
		gfx.setColor(clr1);
		gfx.fillRect(0, 0, t, 2);
		gfx.setColor(clr0);
		gfx.fillRect(t, 0, w - t, 2);
	}
	
	/**
	 * Draws a progress bar representing the fire risk ratio.
	 * @param gfx
	 * @param x : relative x bar pos in pixels
	 * @param y : relative y bar pos in pixels
	 */
	private void renderFireRiskRatio(Graphics gfx)
	{
		// FIXME (rare) fire bar not being rendered properly in rare cases
		renderBar(gfx, getFireLevelRatio(), Color.darkGray, Color.orange);
	}

	/**
	 * Draws a progress bar representing the solidness ratio.
	 * @param gfx
	 * @param x : relative x bar pos in pixels
	 * @param y : relative y bar pos in pixels
	 */
	private void renderSolidnessRatio(Graphics gfx)
	{
		renderBar(gfx, getSolidnessRatio(), Color.darkGray, Color.white);
	}

	protected void renderDefault(Graphics gfx, Image sprite)
	{
		gfx.drawImage(sprite, 0, -getZHeight() * Game.tilesSize);
	}

	protected abstract void renderBuild(GameContainer gc, StateBasedGame game, Graphics gfx);

	/**
	 * Determines if the building can be placed on a certain position on the map.
	 * @param map
	 * @param x : origin X in cells
	 * @param y : origin Y in cells
	 * @return true if can be placed, false otherwise
	 */
	public boolean canBePlaced(MapGrid map, int x, int y)
	{
		return map.canPlaceObject(x, y, getWidth(), getHeight());
	}

	@Override
	public int getTickTime()
	{
		return 1000; // default is 1s
	}

	/**
	 * Returns the surface occupied by the building on the floor in cells^2
	 * @return
	 */
	public int getSurfaceArea()
	{
		return getWidth() * getHeight();
	}
	
	/**
	 * Gets the fire risk ratio in [0, 1] (If 1, the build takes fire).
	 * This is a computed value used for display.
	 * If the build is on fire, the ratio will decrease until the fire extinguishes itself.
	 * @return ratio in [0, 1]
	 */
	public float getFireLevelRatio()
	{
		if(fireLevel < FIRE_BURN)
			return (float)fireLevel / (float)FIRE_BURN;
		else if(fireLevel == 0)
			return 0;
		else
			return (float)(FIRE_RUINS - FIRE_BURN) / (float)(fireLevel - FIRE_BURN);
	}
	
	/**
	 * Returns true if the build produces smoke from flames (will take fire soon)
	 * @return
	 */
	public boolean isFireSmoke()
	{
		return fireLevel >= FIRE_SMOKE && fireLevel < FIRE_BURN;
	}
	
	/**
	 * Returns true if the build is burning
	 * @return
	 */
	public boolean isFireBurning()
	{
		return fireLevel >= FIRE_BURN && fireLevel != FIRE_RUINS;
	}
	
	/**
	 * Decreases fire level of this build.
	 * Minimum is 0.
	 * @param r : reduction level
	 */
	public void reduceFireLevel(byte r)
	{
		if(fireLevel > r)
			fireLevel -= r;
		else
			fireLevel = 0;
	}
	
	/**
	 * Increases fire level of this build.
	 * Can set the build on fire.
	 * If the build is already burning or is not flamable,
	 * this method will have no effect.
	 * @param r
	 */
	public void increaseFireLevel(byte r)
	{
		if(!getProperties().isFlamable)
			return;
		
		if(fireLevel < FIRE_BURN)
		{
			if(r >= FIRE_BURN - fireLevel)
			{
				fireLevel = FIRE_BURN;
				destroy(true);
			}
			else
			{
				fireLevel += r;
			}
		}
	}
	
	public void extinguishFire()
	{
		fireLevel = FIRE_MIN;
		mapRef.playerCity.fireAlerts.unregisterFire(getX(), getY());
	}
		
	protected float getFireRisk()
	{
		return 0.1f;
	}

	private void tickFireLevel()
	{		
		if(fireLevel < FIRE_BURN) // The build is not burning
		{
			if(!getProperties().isFlamable)
				return;

			// Self fire risk
			if(getProperties().canTakeFire && Math.random() < getFireRisk())
			{
				fireLevel++;
				if(fireLevel == FIRE_BURN)
					destroy(true);
			}
			
			if(fireLevel > FIRE_SMOKE && fireLevel < FIRE_BURN && Math.random() < 0.4f)
			{
				// There is a fire growing inside...
				fireLevel++;
				if(fireLevel == FIRE_BURN)
					destroy(true);
			}
		}
		else // The build is entirely on fire
		{
			// TODO propagate through roads
			
			// Fire propagation
			List<Build> builds = mapRef.getBuildsAround(getX(), getY(), getWidth(), getHeight());
			for(Build b : builds)
			{
				b.increaseFireLevel((byte) (Math.random() < 0.5f ? 1 : 2));
			}
			
			// Fire extinction
			if(Math.random() < 0.4f)
			{
				fireLevel++;
				if(fireLevel == FIRE_RUINS)
				{
					fireLevel = FIRE_MIN; // Fire stops
					mapRef.playerCity.fireAlerts.unregisterFire(getX(), getY());
				}
			}
		}
	}
	
	protected void renderSmoke(Graphics gfx)
	{
		// TODO renderSmoke
		
		// Placeholder : the build will blink to red
		if(getTicks() % 2 == 0)
		{
			gfx.setColor(new Color(255, 0, 0, 32));
			gfx.fillRect(0, 0, Game.tilesSize * getWidth(), Game.tilesSize * getHeight());
		}
	}
	
	protected void renderFire(Graphics gfx)
	{
		gfx.drawAnimation(Content.sprites.effectFire, 0, -4);
	}
	
	public boolean canBeErasedByPlayer()
	{
		if(Cheats.isSuperEraser())
			return true;
		return !isFireBurning();
	}
	
	public boolean isWalkable()
	{
		return false;
	}
	
}

