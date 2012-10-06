package simciv.units;

import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.Log;

import backend.Direction2D;
import backend.pathfinding.IMapSpec;
import backend.pathfinding.IMapTarget;

import simciv.Map;
import simciv.builds.Build;
import simciv.builds.TaxmenOffice;
import simciv.content.Content;
import simciv.movement.RandomRoadMovement;

/**
 * A Robber walk at random and search for a place to rob money.
 * If he finds money, he rob it and tries to run away if he's seen by a policeman.
 * @author Marc
 *
 */
public class Robber extends Unit
{
	private static final long serialVersionUID = 1L;
	
	private static final int HIDE_TIME = 16; // in seconds
	private static final int MONEY_TO_ROB = 400;
	
	// States
	private static final byte WANDER = 0;
	private static final byte FIND_MONEY = 1;
	private static final byte RUN_AWAY = 2;
	
	/** How many money the robber picked **/
	private float robbedMoney;
	
	/** Ticks left before the robber to hide **/
	private int ticksBeforeHide;
	
	private byte lastState;

	public Robber(Map m)
	{
		super(m);
		setState(WANDER);
	}
	
	/**
	 * Returns how many money the robber picked
	 * @return
	 */
	public float getRobbedMoney()
	{
		return robbedMoney;
	}

	@Override
	public void tick()
	{
		byte stateTemp = getState();
		
		switch(getState())
		{
		case WANDER : tickWander(); break;
		case FIND_MONEY : tickFindMoney(); break;
		case RUN_AWAY : tickRunAway(); break;
		}
		
		lastState = stateTemp;
	}

	private void tickWander()
	{
		if(!isMovement())
			setMovement(new RandomRoadMovement());
		
		if(getTicks() % 16 == 0)
		{
			setMovement(null);
			setState(FIND_MONEY);
		}
	}

	private void tickFindMoney()
	{
		if(!isMovement())
		{
			if(!findAndGoTo(new RoadsPass(), new MoneyTarget(), -1))
			{
				if(Math.random() < 0.2f)
					setState(WANDER);
				else
				{
					Log.debug("A robber disappeared. Cause : nothing to rob");
					dispose();
				}
			}
		}
		if(isMovementFinished())
		{
			if(robMoney())
				setState(RUN_AWAY);
			else
				setState(WANDER);
		}
	}

	private void tickRunAway()
	{
		if(lastState != getState())
		{
			ticksBeforeHide = secondsToTicks(HIDE_TIME);
			setMovement(new RandomRoadMovement());
		}
		
		ticksBeforeHide--;
		if(ticksBeforeHide <= 0)
		{
			Log.debug(this + " hidden with " + getRobbedMoney() + " coins.");
			dispose();
		}
	}
	
	@Override
	public int getTickTime()
	{
		return getState() == RUN_AWAY ? 260 : super.getTickTime();
	}

	@Override
	public boolean isAnimal()
	{
		return false;
	}

	@Override
	protected void renderUnit(Graphics gfx)
	{
		this.renderDefault(gfx,
				Content.sprites.unitRobber,
				robbedMoney > 0 ? 4 : 0);
	}
	
	private boolean robMoney()
	{
		float money = 0;
		List<Build> builds = mapRef.getBuildsAround(getX(), getY());
		
		for(Build b : builds)
		{
			if(TaxmenOffice.class.isInstance(b))
			{
				TaxmenOffice t = (TaxmenOffice)b;
				money = t.robMoney(MONEY_TO_ROB);
				if(money > 0)
					break;
			}
		}
		
		robbedMoney += money;
		return money > 0;
	}

	@Override
	public String getDisplayableName()
	{
		return "Stealer";
	}
	
	@Override
	public String getInfoLine()
	{
		return super.getInfoLine() + " shut up, I take your money HUHU !";
	}
	
	public void onSeenByPoliceman(Policeman p)
	{
		if(getDirection() == Direction2D.NONE)
			return;
		
		if(Direction2D.opposite[getDirection()] == p.getDirection())
		{
			// I've seen him seeing me !
			setMovement(null);
			ticksBeforeHide = secondsToTicks(HIDE_TIME);
			setState(RUN_AWAY);
		}
	}
	
	public boolean isBad()
	{
		return robbedMoney > 0;
	}

	@Override
	public void onDestruction()
	{
	}
	
	private class RoadsPass implements IMapSpec
	{
		@Override
		public boolean canPass(int x, int y) {
			return mapRef.grid.isRoad(x, y);
		}
	}
	
	private class MoneyTarget implements IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y) {
			Build b = mapRef.getBuild(x, y);
			if(b != null && TaxmenOffice.class.isInstance(b))
				return ((TaxmenOffice)b).getMoneyInVault() > 0;
			return false;
		}	
	}

}


