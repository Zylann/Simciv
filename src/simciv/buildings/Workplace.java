package simciv.buildings;

import java.util.HashMap;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import simciv.Game;
import simciv.World;
import simciv.jobs.Job;
import simciv.units.Citizen;

public abstract class Workplace extends Building
{	
	// References to citizen working here
	HashMap<Integer,Citizen> employees = new HashMap<Integer,Citizen>();
	
	public Workplace(World w)
	{
		super(w);
		employees = new HashMap<Integer,Citizen>();
	}
	
	/**
	 * May return a value between 0 and 100 indicating the production progress.
	 * (This is only required for production workplaces.)
	 * @return
	 */
	public abstract int getProductionProgress();
		
	public int getMaxEmployees()
	{
		return getProperties().unitsCapacity;
	}
	
	public int getNbEmployees()
	{
		return employees.size();
	}
	
	public boolean needEmployees()
	{
		return getNbEmployees() < getMaxEmployees();
	}
	
	/**
	 * Creates a job from the workplace (depending on available tasks).
	 * If the job is available, the given citizen is added as an employee to the workplace.
	 * Otherwise, the method returns null.
	 * @param citizen : citizen who wants the job
	 * @return : a new job if available, null if not
	 */
	public abstract Job giveNextJob(Citizen citizen);

	/**
	 * Adds an employee to the workplace (he should have been given a job before)
	 * @param citizen
	 * @return : true if success, false otherwise
	 */
	protected boolean addEmployee(Citizen citizen)
	{
		if(needEmployees())
		{
			return employees.put(citizen.getID(), citizen) == null;
		}
		return false;
	}
	
	/**
	 * Removes an employee (a citizen) from the workplace, also make it redundant if prompted.
	 * @param id : ID of the citizen
	 * @param makeRedundant : if true, this method will notify the citizen to quit his job.
	 * Do not set it to true if it is already done.
	 */
	public void removeEmployee(int id, boolean makeRedundant)
	{
		Citizen oldEmployee = employees.remove(id);
		if(oldEmployee != null && makeRedundant)
			oldEmployee.quitJob(false); // false : don't notify the workplace, this is already done.
	}
	
	@Override
	public final boolean isWorkplace()
	{
		return true;
	}

	@Override
	public void onDestruction()
	{
		// All citizen working here are made redundant
		for(Citizen c : employees.values())
			c.quitJob(false); // false : don't notify the workplace, this is already done.
	}
	
	public void renderDefault(Graphics gfx, SpriteSheet sprites)
	{
		if(state == Building.STATE_ACTIVE)
			gfx.drawImage(sprites.getSprite(1, 0), 0, -getZHeight() * Game.tilesSize);
		else
			gfx.drawImage(sprites.getSprite(0, 0), 0, -getZHeight() * Game.tilesSize);
	}

	@Override
	public String getInfoString()
	{
		String info = "[" + getProperties().name + "] employees : " + getNbEmployees() + "/" + getMaxEmployees();
		if(state == STATE_ACTIVE)
			info += ", production : " + getProductionProgress() + "%";
		return info;
	}

	@Override
	protected void tick()
	{
		if(state == Building.STATE_NORMAL)
		{
			if(!needEmployees())
			{
				state = Building.STATE_ACTIVE;
				onActivityStart();
			}
		}
		else if(state == Building.STATE_ACTIVE)
		{
			if(needEmployees())
			{
				state = Building.STATE_NORMAL;
				onActivityStop();
			}
		}
	}
	
	@Override
	public boolean onMaintenance()
	{
		if(employees.isEmpty())
			return false;
		repair();
		return true;
	}

	protected abstract void onActivityStart();
	
	protected abstract void onActivityStop();
	
	protected abstract void tickActivity();
	
}







