package simciv;

import java.util.HashMap;

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
	
	public abstract int getProductionProgress();
		
	protected int getMaxEmployees()
	{
		return getProperties().capacity;
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

	@Override
	public void onDestruction()
	{
		for(Citizen c : employees.values())
		{
			c.setWorkplace(null);
		}
	}
}







