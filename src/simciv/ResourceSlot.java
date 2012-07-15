package simciv;

import org.newdawn.slick.Graphics;

/**
 * A slot that can contain a certain amount of resource of the same type
 * @author Marc
 *
 */
public class ResourceSlot
{
	private byte type; // resource type
	private int amount;
	
	public ResourceSlot()
	{
		type = Resource.NONE;
		amount = 0;
	}
	
	public ResourceSlot(byte type, int amount)
	{
		set(type, amount);
	}
	
	public void set(byte type, int amount)
	{
		if(amount == 0)
			this.type = Resource.NONE;
		else
			this.type = type;
		this.amount = amount;
	}
	
	public byte getType()
	{
		return type;
	}
	
	public int getAmount()
	{
		return amount;
	}
	
	public boolean isEmpty()
	{
		return type == Resource.NONE;
	}
	
	public Resource getSpecs()
	{
		return Resource.get(type);
	}
	
	/**
	 * Moves resources from another slot to this slot.
	 * They must store the same resource type.
	 * Stack limits are applied.
	 * @param other
	 */
	public void addFrom(ResourceSlot other)
	{
		// If the slot is compatible or free
		if(type == other.type || isEmpty())
		{
			// Put resources in it as much as possible
			int spaceLeft = Resource.get(other.type).getStackLimit() - amount;
			type = other.type;
			
			if(other.amount > spaceLeft)
			{
				amount += spaceLeft;
				other.amount -= spaceLeft;
				if(other.amount == 0)
					other.type = Resource.NONE;
			}
			else
			{
				amount += other.amount;
				other.amount = 0;
				other.type = Resource.NONE;
			}
		}
	}
	
	public void renderCarriage(Graphics gfx, int x, int y)
	{
		Resource.get(type).renderCarriage(gfx, x, y, amount);
	}
	
	public void renderStorage(Graphics gfx, int x, int y)
	{
		Resource.get(type).renderStorage(gfx, x, y, amount);
	}	
	
	public String toString()
	{
		if(type == Resource.NONE)
			return "Empty";
		String resourceName = new String(getSpecs().getName());
		if(resourceName.isEmpty())
			resourceName = "unknown";
		else
			resourceName.toLowerCase();
		return amount + " " + resourceName;
	}

	public float getLoad()
	{
		return (float)amount / getSpecs().getStackLimit();
	}
	
}



