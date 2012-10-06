package simciv.units;

import simciv.Map;

public abstract class Animal extends Unit
{
	private static final long serialVersionUID = 1L;

	public Animal(Map m)
	{
		super(m);
	}
	
	@Override
	public boolean isAnimal()
	{
		return true;
	}
	
	/**
	 * Creates new animal units from this one
	 */
	protected abstract void breed();

}
