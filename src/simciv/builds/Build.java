package simciv.builds;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import backend.MathHelper;
import backend.Vector2i;

import simciv.Game;
import simciv.MapGrid;
import simciv.ResourceSlot;
import simciv.Map;
import simciv.TickableEntity;
import simciv.content.Content;
import simciv.effects.SmokeExplosion;
import simciv.maptargets.RoadMapTarget;
import simciv.ui.base.Notification;

// FIXME sometimes, the map is left unconstructible. Bug origin is unknown...

/**
 * Each object that can be constructed by the player.
 * Buildings cannot move.
 * @author Marc
 *
 */
public abstract class Build extends TickableEntity
{
	private static final long serialVersionUID = 1L;

	// Common states
	public static final byte STATE_CONSTRUCTION = 0;
	public static final byte STATE_NORMAL = 1;
	public static final byte STATE_ACTIVE = 2;
	
	// Fire levels
	public static final byte FIRE_MIN = 0;
	public static final byte FIRE_SMOKE = 80;
	public static final byte FIRE_BURN = 90;
	public static final byte FIRE_RUINS = 100;
		
	/** Solidness level **/
	protected int solidness;
	
	/**
	 * Fire risk information :
	 * 0 to 90 : no fire
	 * 90 : smoke
	 * 100 : fire
	 */
	protected byte fireRisk;
	
	public Build(Map m)
	{
		super(m);
		state = STATE_NORMAL;
		solidness = getSolidnessMax();
	}
	
	public abstract BuildProperties getProperties();
	
	public Color getMinimapColor()
	{
		return BuildCategory.get(getProperties().category).getColor();
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
				Debris d = new Debris(mapRef, burning);
				d.setPropertiesFromBuild(this);
				mapRef.placeBuild(d, x, y);
				
				mapRef.addGraphicalEffect(
						new SmokeExplosion(x, y, 8, 1.5f, Game.tilesSize/2));
			}
		}
		
		mapRef.sendNotification(Notification.TYPE_WARNING, "A build has collapsed !");
	}
		
	@Override
	public void onInit()
	{
		super.onInit();
		mapRef.grid.markBuilding(this, true);
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
		tickFireRisk();
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
	 * Returns a brief information about the building
	 * @return
	 */
	public abstract String getInfoString();

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
	 * Draws a progress bar representing the solidness ratio.
	 * @param gfx
	 * @param x : relative x bar pos in pixels
	 * @param y : relative y bar pos in pixels
	 */
	private void renderFireRiskRatio(Graphics gfx)
	{
		renderBar(gfx, getFireRiskRatio(), Color.darkGray, Color.orange);
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
		
	public float getFireRiskRatio()
	{
		if(fireRisk < FIRE_BURN)
			return (float)fireRisk / (float)FIRE_BURN;
		return 1;
	}
	
	public boolean isFireSmoke()
	{
		return fireRisk >= FIRE_SMOKE && fireRisk < FIRE_BURN;
	}
	
	public boolean isFireBurning()
	{
		return fireRisk >= FIRE_BURN && fireRisk != FIRE_RUINS;
	}
	
	public boolean isFlamable()
	{
		return getProperties().isFlamable;
	}
	
	public void reduceFireRisk(byte r)
	{
		if(fireRisk > r)
			fireRisk -= r;
		else
			fireRisk = 0;
	}
	
	public void extinguishFire()
	{
		fireRisk = FIRE_MIN;
	}
	
	protected void tickFireRisk()
	{
		if(!isFlamable())
		{
			if(fireRisk < FIRE_SMOKE)
				return;
		}
		
		if(fireRisk < FIRE_BURN)
		{
			if(Math.random() < 0.15f)
			{
				fireRisk++;
				if(fireRisk == FIRE_BURN)
					destroy(true);
			}
		}
		else
		{	
			if(Math.random() < 0.4f)
			{
				fireRisk++;
				if(fireRisk == FIRE_RUINS)
					fireRisk = FIRE_MIN;
			}
		}
	}
	
	protected void renderSmoke(Graphics gfx)
	{
		// TODO renderSmoke
	}
	
	protected void renderFire(Graphics gfx)
	{
		gfx.drawAnimation(Content.sprites.effectFire, 0, -4);
	}
	
}

