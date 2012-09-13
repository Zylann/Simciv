package simciv;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

import backend.GameComponent;

import simciv.builds.House;
import simciv.builds.Warehouse;

/**
 * Global Informations about player's city
 * @author Marc
 *
 */
public class PlayerCity extends City implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	/** Total money available for the player on this city **/
	protected float money;
	
	/** Tax ratio based on the income of each citizen **/
	protected float incomeTaxRatio;
	
	// Computed data
	
	/** Total population (computed) **/
	public transient int population;
	
	/** Total population with a job (computed) **/
	public transient int workingPopulation;
	
	/** List of all warehouses in the city (computed) **/
	protected transient HashMap<Integer, Warehouse> warehouses;
	
	/**
	 * Constructs global informations about a city controlled by a player
	 */
	public PlayerCity()
	{
		super();
		money = 3000;
		incomeTaxRatio = 0.09f; // 9 %
		warehouses = new HashMap<Integer, Warehouse>();
		name = "My city";
	}
	
	public float getMoney()
	{
		return money;
	}
	
	public void setMoney(float m)
	{
		money = m;
	}
	
	public void buy(float cost)
	{
		if(!Cheats.isInfiniteMoney())
			money -= cost;
	}
	
	public float getIncomeTaxRatio()
	{
		return incomeTaxRatio;
	}
	
	public void setIncomeTaxRatio(float r)
	{
		if(r > 1)
			incomeTaxRatio = 1;
		else if(r < 0)
			incomeTaxRatio = 0;
		else
			incomeTaxRatio = r;
	}

	public void gainMoney(float amount)
	{
		money += amount;
	}
	
	public void registerWarehouse(Warehouse w)
	{
		warehouses.put(w.getID(), w);
	}
	
	public void unregisterWarehouse(Warehouse w)
	{
		warehouses.remove(w.getID());
	}
	
	public int getFreeSpaceForResource(Map m, byte type)
	{
		int total = 0;
		for(Warehouse w : warehouses.values())
		{
			total += w.getFreeSpaceForResource(type);
		}
		return total;
	}
	
	public int getResourceTotal(byte type)
	{
		int total = 0;
		for(Warehouse w : warehouses.values())
		{
			total += w.getResourceTotal(type);
		}
		return total;
	}
	
	/**
	 * Recompute computed data of the city from the map.
	 * Must be called after map and city deserialization.
	 * @param builds : all builds of the map
	 */
	public void recomputeData(Collection<GameComponent> builds)
	{
		warehouses = new HashMap<Integer, Warehouse>();
		population = 0;
		workingPopulation = 0;
		
		for(GameComponent b : builds)
		{
			if(Warehouse.class.isInstance(b))
			{
				Warehouse w = (Warehouse)b;
				warehouses.put(w.getID(), w);
			}
			else if(House.class.isInstance(b))
			{
				House h = (House)b;
				population += h.getNbInhabitants();
				workingPopulation += h.getNbWorkers();
			}
		}
	}

}



