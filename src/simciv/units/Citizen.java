package simciv.units;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

import simciv.Game;
import simciv.Map;
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
	
	public static final int TICK_TIME_BASIC = 400;
	private static final float TICK_TIME_VARIATION = 100;
	private static final int TICK_TIME_MIN = 200;
	
	private int tickTimeRandom; // Tick time variation constant in milliseconds
	private int tickTime; // Tick time interval in milliseconds (modified value)
	private int workplaceID;
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
			workplace = (Workplace)mapRef.getBuild(workplaceID);
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
	protected int getTickTime()
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
		getWorkplace().removeDisposedUnit(this);
	}
	
	public abstract byte getJobID();
	
}


