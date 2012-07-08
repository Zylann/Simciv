package simciv.buildings;

import java.util.HashMap;

import simciv.World;
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
	
	public abstract int getProductionProgress(); // returns a value between 0 and 100
		
	protected int getMaxEmployees()
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

	public boolean addEmployee(Citizen citizen)
	{
		if(needEmployees())
		{
			return employees.put(citizen.getID(), citizen) == null;
		}
		return false;
	}

	public void removeEmployee(int id)
	{
		employees.remove(id);
	}

	@Override
	public void onDestruction()
	{
		// All citizen working here are made redundant
		for(Citizen c : employees.values())
		{
			c.setWorkplace(null);
		}
	}

}







