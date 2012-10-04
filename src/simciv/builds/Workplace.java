package simciv.builds;

import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;

import backend.geom.Vector2i;
import simciv.Game;
import simciv.Map;
import simciv.maptargets.IExplicitMapTarget;
import simciv.maptargets.RoadMapTarget;
import simciv.units.Citizen;
import simciv.units.Employer;
import simciv.units.Jobs;
import simciv.units.Unit;

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
	private static final long serialVersionUID = 1L;
	
	/** House ID of each employee **/
	private ArrayList<Integer> employees;
	
	/** ID of each units that belongs to this workplace **/
	protected ArrayList<Integer> units;
	
	/** Employees recruiter **/
	private Employer employer;
	
	/** Is the workplace active? **/
	private boolean active;
	
	public Workplace(Map m)
	{
		super(m);
		employees = new ArrayList<Integer>();
		units = new ArrayList<Integer>();
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
			employees.add(empHouse.getID());
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
		
		IExplicitMapTarget roads = new RoadMapTarget();
		List<Vector2i> availablePositions = 
			mapRef.grid.getPositionsAround(this, roads, mapRef);
		
		do
		{
			for(Vector2i pos : availablePositions)
			{
				addUnit(Jobs.createUnitFromJobID(jobID, mapRef, this), pos.x, pos.y);
				unitsToProduce--;
				if(unitsToProduce == 0)
					break;
			}
			
		}while(unitsToProduce > 0);
	}
	
	protected void addAndSpawnUnitAround(Citizen u)
	{
		Citizen list[] = {u};
		addAndSpawnUnitsAround(list);
	}
	
	/**
	 * Adds and spawns citizen units around the workplace on available cells.
	 * @param list : list of units
	 */
	protected void addAndSpawnUnitsAround(Citizen list[])
	{
		if(list.length == 0)
			return;
		
		IExplicitMapTarget roads = new RoadMapTarget();
		List<Vector2i> availablePositions = 
			mapRef.grid.getPositionsAround(this, roads, mapRef);
		
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
		units.add(c.getID());
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
		
		int cID = units.get(0);
		Unit u = mapRef.getUnit(cID);
		u.dispose();
		units.remove(0);
	}
	
	/**
	 * Removes a citizen unit from the workplace. It must have been disposed before.
	 * @param citizen
	 */
	public void removeDisposedUnit(Citizen citizen)
	{
		units.remove((Object)(citizen.getID()));
	}
	
	/**
	 * Removes all the units spawned from this workplace.
	 * This will not remove employees.
	 */
	protected void removeAllUnits()
	{
		for(Integer cID : units)
		{
			Unit u = mapRef.getUnit(cID);
			u.dispose();
		}
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
		Log.debug(this + " removing employee living at " + empHouse);

		if(employees.isEmpty())
		{
			Log.debug("--- but the workplace has no employees");
			return;
		}
		
		if(!employees.remove((Object)(empHouse.getID())))
		{
			Log.debug("--- but the given house is not referenced");
			return;
		}
		
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
		if(employees.remove((Object)(oldHouse.getID())))
			employees.add(newHouse.getID());
		else
			Log.error("Workplace.changeEmployeeHouse");
	}
	
	/**
	 * Removes all the employees of the workplace
	 * (They are not killed, only "fired").
	 */
	public void removeAllEmployees()
	{
		removeAllUnits();
		
		Log.debug(this + " removing all employees");
		for(Integer hID : employees)
		{
			House h = (House)(mapRef.getBuild(hID));
			h.removeWorker(this, false);
		}
		
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
		units.remove((Object)(u.getID()));
		int hID = employees.get(0);
		House h = (House)mapRef.getBuild(hID);
		h.removeInhabitantWorkingAt(this);
	}
	
	@Override
	public final boolean isWorkplace()
	{
		return true;
	}
	
	public final boolean isActive()
	{
		return active;
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
		if(isActive())
			gfx.drawImage(sprites.getSprite(1, 0), 0, -getZHeight() * Game.tilesSize);
		else
			gfx.drawImage(sprites.getSprite(0, 0), 0, -getZHeight() * Game.tilesSize);
		// Debug
//		gfx.setColor(Color.white);
//		gfx.drawString("" + getNbEmployees(), 0, 0);
	}

	@Override
	public String getInfoLine()
	{
		String info = "[" + getProperties().name + "] employees : " 
				+ getNbEmployees() + "/" + getMaxEmployees();
		if(isActive())
			info += ", production : " + getProductionProgress() + "%";
		return info;
	}

	@Override
	public void tick()
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
			
		if(!active)
		{
			if(!needEmployees())
			{
				active = true;
				onActivityStart();
			}
		}
		else if(active)
		{
			if(needEmployees())
			{
				active = false;
				onActivityStop();
			}
			else
				tickActivity();
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
		
		IExplicitMapTarget roads = new RoadMapTarget();
		ArrayList<Vector2i> availablePos = 
			mapRef.grid.getPositionsAround(this, roads, mapRef);
		
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
	
	@Override
	public BuildReport getReport()
	{
		BuildReport report = super.getReport();
		
		if(needEmployees())
			report.add(BuildReport.PROBLEM_MINOR,
				"We need " + getNbNeededEmployees() + " more employees.");
		
		return report;
	}

}







