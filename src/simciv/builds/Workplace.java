package simciv.builds;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import simciv.Game;
import simciv.Map;
import simciv.Vector2i;
import simciv.maptargets.IMapTarget;
import simciv.maptargets.RoadMapTarget;
import simciv.units.Citizen;
import simciv.units.Employer;
import simciv.units.Job;

/**
 * A workplace is a build that have employees and execute specific tasks (production, services...).
 * Employees are represented as references to their house.
 * A house can be referenced more than one time if employees come from the same one.
 * Many workplaces can produce units (Citizen). A workplace cannot produce more units than its number of employees.
 * One unit can represent any of the employees.
 * Adding or removing units don't affect the population number, except if they get killed.
 * @author Marc
 *
 */
public abstract class Workplace extends Build
{
	private ArrayList<House> employees; // House of each employee
	protected TreeMap<Integer, Citizen> units; // ID of citizen, citizen unit
	private Employer employer;
	
	public Workplace(Map m)
	{
		super(m);
		employees = new ArrayList<House>();
		units = new TreeMap<Integer, Citizen>();
	}

	@Override
	protected void onDispose()
	{
		super.onDispose();
		if(employer != null)
			employer.dispose();
		removeAllEmployees();
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
	
	/**
	 * @return true if the workplace needs employees to function.
	 */
	public boolean needEmployees()
	{
		return getNbEmployees() < getMaxEmployees();
	}
	
	public int getNbNeededEmployees()
	{
		return getMaxEmployees() - getNbEmployees();
	}

	/**
	 * Adds an employee to the workplace.
	 * @param citizen
	 * @return : true if success, false otherwise
	 */
	public boolean addEmployee(House empHouse)
	{
		if(needEmployees())
		{
			employees.add(empHouse);
			mapRef.playerCity.workingPopulation++;
			return true;
		}
		return false;
	}
	
	/**
	 * Adds and spawns citizen units around the workplace on available cells.
	 * The units are created from the given job ID.
	 * @param jobID
	 * @param unitsToProduce
	 */
	protected void addAndSpawnUnitsAround(byte jobID, int unitsToProduce)
	{
		if(units.size() == getNbEmployees() || unitsToProduce <= 0)
			return;
		
		if(unitsToProduce + units.size() > getNbEmployees())
			unitsToProduce = getNbEmployees() - units.size();
		
		IMapTarget roads = new RoadMapTarget();
		List<Vector2i> availablePositions = 
			mapRef.grid.getAvailablePositionsAround(this, roads, mapRef);
		
		do
		{
			for(Vector2i pos : availablePositions)
			{
				addUnit(Job.createUnitFromJobID(jobID, mapRef, this), pos.x, pos.y);
				unitsToProduce--;
				if(unitsToProduce == 0)
					break;
			}
			
		}while(unitsToProduce > 0);
	}
	
	/**
	 * Adds and spawns citizen units around the workplace on available cells.
	 * @param list : list of units
	 */
	protected void addAndSpawnUnitsAround(Citizen list[])
	{
		if(list.length == 0)
			return;
		
		IMapTarget roads = new RoadMapTarget();
		List<Vector2i> availablePositions = 
			mapRef.grid.getAvailablePositionsAround(this, roads, mapRef);
		
		int i = 0;
		
		do
		{
			for(Vector2i pos : availablePositions)
			{
				addUnit(list[i], pos.x, pos.y);
				i++;
				if(i == list.length)
					break;
			}
			
		}while(i < list.length);
	}
	
	/**
	 * Adds an unit to the workplace, and spawns it at the given position
	 * @param c : citizen unit
	 * @param x : position X in cells
	 * @param y : position Y in cells
	 * @return true if success, false if not.
	 */
	private boolean addUnit(Citizen c, int x, int y)
	{
		if(units.size() == getNbEmployees())
			return false;
		units.put(c.getID(), c);
		mapRef.spawnUnit(c, x, y);
		return true;
	}
	
	/**
	 * Removes a citizen unit from the workplace (chosen by taking the first entry found).
	 */
	private void removeNextUnit()
	{
		if(units.isEmpty())
			return;
		Citizen c = units.remove(units.firstKey());
		if(!c.isDisposed())
			c.dispose();
	}
	
	/**
	 * Removes a citizen unit from the workplace. It must have been disposed before.
	 * @param citizen
	 */
	public void removeDisposedUnit(Citizen citizen)
	{
		units.remove(citizen.getID());
	}
	
	/**
	 * Removes all the units spawned from this workplace.
	 * This will not remove employees.
	 */
	protected void removeAllUnits()
	{
		for(Citizen c : units.values())
			c.dispose();
		units.clear();
	}
	
	/**
	 * Removes an employee from the workplace.
	 * It is not killed, only "fired".
	 * In the game, removing an employee may alter the activity of the workplace (not enough employees).
	 * @param empHouse : house of the employee
	 * @param notify : if true, the specified house will be notified.
	 */
	public void removeEmployee(House empHouse, boolean notify)
	{
		if(employees.isEmpty())
			return;
		
		if(!employees.remove(empHouse))
			return;
		
		mapRef.playerCity.workingPopulation--;
		
		removeNextUnit();
		
		if(notify)
			empHouse.removeWorker(this, false);
	}
	
	/**
	 * Changes the house of one employee.
	 * This method will simply remove oldHouse and add newHouse to workers referenced houses.
	 * @param oldHouse : old house of the employee
	 * @param newHouse : new house of the employee
	 */
	public void changeEmployeeHouse(House oldHouse, House newHouse)
	{
		if(employees.remove(oldHouse))
			employees.add(newHouse);
		else
			System.out.println("ERROR: Workplace.changeEmployeeHouse");
	}
	
	/**
	 * Removes all the employees of the workplace
	 * (They are not killed, only "fired").
	 */
	public void removeAllEmployees()
	{
		removeAllUnits();
		
		for(House h : employees)
			h.removeWorker(this, false);
		
		mapRef.playerCity.workingPopulation -= employees.size();
		employees.clear();
	}
	
	/**
	 * Called when an unit working here is killed.
	 * This will propagate the information to one of workers referenced houses.
	 * (The notified house can be any of referenced ones).
	 * @param u : the unit that got killed
	 */
	public void onUnitKilled(Citizen u)
	{
		units.remove(u.getID());
		House h = employees.get(0);
		h.removeInhabitantWorkingAt(this);
	}
	
	@Override
	public final boolean isWorkplace()
	{
		return true;
	}

	@Override
	public void onDestruction()
	{
	}
	
	/**
	 * Draws the workplace using the most common sprite pattern.
	 * @param gfx : graphics context
	 * @param sprites : sheet containing two sprites (inactive and active)
	 */
	public void renderDefault(Graphics gfx, SpriteSheet sprites)
	{
		if(state == Build.STATE_ACTIVE)
			gfx.drawImage(sprites.getSprite(1, 0), 0, -getZHeight() * Game.tilesSize);
		else
			gfx.drawImage(sprites.getSprite(0, 0), 0, -getZHeight() * Game.tilesSize);
		gfx.setColor(Color.white);
		gfx.drawString("" + getNbEmployees(), 0, 0);
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
		if(employer == null)
		{
			if(needEmployees())
				spawnEmployer();
		}
		else
		{
			if(employer.isDisposed())
				employer = null;
		}
			
		if(state == Build.STATE_NORMAL)
		{
			if(!needEmployees())
			{
				state = Build.STATE_ACTIVE;
				onActivityStart();
			}
		}
		else if(state == Build.STATE_ACTIVE)
		{
			if(needEmployees())
			{
				state = Build.STATE_NORMAL;
				onActivityStop();
			}
		}
	}
	
	/**
	 * Spawns an employer near the workplace in order to recruit employees.
	 * If there is already an employer patrolling, this method does nothing.
	 */
	public void spawnEmployer()
	{
		if(employer != null)
			return;
		
		IMapTarget roads = new RoadMapTarget();
		ArrayList<Vector2i> availablePos = mapRef.grid.getAvailablePositionsAround(this, roads, mapRef);
		
		if(!availablePos.isEmpty())
		{
			Vector2i pos = availablePos.get(0);
			employer = new Employer(mapRef, this);
			mapRef.spawnUnit(employer, pos.x, pos.y);
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

	/**
	 * Called when the workplace has enough employees to start its activity
	 */
	protected abstract void onActivityStart();
	
	/**
	 * Called when the workplace stops its activity, for example if it hasn't enough employees
	 */
	protected abstract void onActivityStop();
	
	protected abstract void tickActivity();

	/**
	 * Returns the salary for one employee working here
	 * @return
	 */
	public float getSalary()
	{
		return 12;
	}
	
}







