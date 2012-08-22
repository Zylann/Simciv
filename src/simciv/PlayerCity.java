package simciv;

import java.util.HashMap;

/**
 * Global Informations about player's city
 * @author Marc
 *
 */
public class PlayerCity
{
	protected float money;
	protected float incomeTaxRatio;
	private HashMap<Byte, Integer> storedResources; // resource type, total amount
	
	public PlayerCity()
	{
		money = 3000;
		incomeTaxRatio = 0.09f; // 9 %
		storedResources = new HashMap<Byte, Integer>();
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
	
	public void onResourceStored(byte type, int amount)
	{
		if(amount < 0)
			return;
		Integer total = storedResources.get(type); // Find the associated total
		if(total == null) // If not mapped
		{
			// Create mapping
			storedResources.put(type, amount);
		}
		else // The mapping exist
		{
			// Add amount to the mapped value
			storedResources.put(type, total + amount);
		}
	}
	
	public void onResourceUsed(byte type, int amount)
	{
		if(amount < 0)
			return;
		Integer total = storedResources.get(type);
		if(total != null)
		{
			total -= amount;
			storedResources.put(type, total);
			if(total == 0)
				storedResources.remove(type);
			else if(total < 0)
				System.out.println("ERR: resource total reached negative value");
		}
	}
	
	public int getResourceTotal(byte type)
	{
		Integer total = storedResources.get(type);
		if(total == null)
			return 0;
		return total;
	}

}



