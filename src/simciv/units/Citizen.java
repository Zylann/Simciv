package simciv.units;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

import backend.pathfinding.IMapSpec;
import backend.pathfinding.IMapTarget;

import simciv.Game;
import simciv.Map;
import simciv.builds.Build;
import simciv.builds.Workplace;
import simciv.effects.RisingIcon;
import simciv.content.Content;

/**
 * A citizen unit have a job and works mainly outside builds (visible).
 * @author Marc
 *
 */
public abstract class Citizen extends Unit
{
	private static final long serialVersionUID = 1L;
	
	// Tick time constants
	public static final int TICK_TIME_BASIC = 400;
	private static final float TICK_TIME_VARIATION = 100;
	private static final int TICK_TIME_MIN = 200;
	
	/** Tick time variation in milliseconds (constant specific to each unit) **/
	private int tickTimeRandom;
	
	/** Tick time interval in milliseconds (modified value) **/
	private int tickTime;
	
	/** ID of the place where the citizen works **/
	private int workplaceID;
	
	/** Workplace reference based on workplaceID (computed) **/
	private transient Workplace workplace;

	public Citizen(Map m, Workplace w)
	{
		super(m);
		workplaceID = w.getID();
		// Each citizen have a slightly different basic tickTime
		tickTimeRandom = (int)(TICK_TIME_VARIATION * (Math.random() - 0.5f));
		setTickTimeWithRandom(TICK_TIME_BASIC);		
	}
	
	protected int getWorkplaceID()
	{
		return workplaceID;
	}
	
	protected Workplace getWorkplace()
	{
		if(workplace == null)
		{
			Build b = mapRef.getBuild(workplaceID);
			if(b != null)
				workplace = (Workplace)b;
		}
		return workplace;
	}

	public void setTickTimeWithRandom(int newTickTime)
	{
		tickTime = newTickTime + tickTimeRandom;
		if(tickTime < TICK_TIME_MIN)
			tickTime = TICK_TIME_MIN;
	}
	
	public void renderThinkingIcon(Graphics gfx)
	{
		SpriteSheet thinkingAnim = Content.sprites.unitThinking;
		int gx = Game.tilesSize - thinkingAnim.getWidth() / thinkingAnim.getHorizontalCount();
		gfx.drawImage(thinkingAnim.getSprite(getTicks() % 2 == 0 ? 0 : 1, 0), gx, 0);
	}
	
	@Override
	public void kill()
	{
		super.kill();
		
		getWorkplace().onUnitKilled(this);
		
		mapRef.addGraphicalEffect(
			new RisingIcon(
				getX(), getY(), Content.sprites.effectDeath));
	}

	@Override
	public int getTickTime()
	{
		return tickTime;
	}
	
	/**
	 * Destroys the citizen and spawns a new nomad at the same place
	 */
	public void transformToNomad()
	{
		dispose();
		mapRef.spawnUnit(new Nomad(mapRef), getX(), getY());
	}
	
	@Override
	public void onDestruction()
	{
		Workplace w = getWorkplace();
		if(w != null)
			getWorkplace().removeDisposedUnit(this);
	}
		
	@Override
	public boolean findAndGoTo(IMapTarget target, int maxDistance)
	{
		return findAndGoTo(new DefaultPass(), target, maxDistance);
	}
	
	public boolean isOnRoad()
	{
		return mapRef.grid.isRoad(getX(), getY());
	}
	
	public boolean isMyWorkplaceNearby()
	{
		return mapRef.grid.isBuildAroundWithID(getWorkplaceID(), getX(), getY());
	}
	
	protected void goBackToRoad(int pathfindingDistance)
	{
		// Find the nearest road if I am not on it
		if(!isOnRoad())
		{
			findAndGoTo(
				new WalkableFloor(),
				new RoadTarget(),
				pathfindingDistance);
		}
	}

	/**
	 * Debug : draws a line between the citizen and its workplace
	 */
	protected void renderLineToWorkplace(Graphics gfx)
	{		
		Workplace w = getWorkplace();
		
		// Get relative pos
		int rx = w.getX() - getX();
		int ry = w.getY() - getY();
		
		gfx.setLineWidth(2);
		gfx.setColor(Color.white);
		gfx.drawLine(0, 0, Game.tilesSize * rx, Game.tilesSize * ry);
	}

	/**
	 * Default map pass for citizen.
	 * Defines where a citizen can move.
	 * @author Marc
	 *
	 */
	private class DefaultPass implements IMapSpec
	{
		@Override
		public boolean canPass(int x, int y) {
			return mapRef.grid.isRoad(x, y);
		}
	}
	
	private class RoadTarget implements IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y) {
			return mapRef.grid.isRoad(x, y);
		}
	}
	
	private class WalkableFloor implements IMapSpec
	{
		@Override
		public boolean canPass(int x, int y) {
			return mapRef.isWalkable(x, y);
		}
	}

}


