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
	
	public boolean isFull()
	{
		return type != Resource.NONE && amount == getSpecs().getStackLimit();
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
	public boolean addFrom(ResourceSlot other)
	{
		// If the slot is compatible or free
		if(type == other.type || isEmpty())
		{
			if(other.isEmpty())
				return false;
			
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
			
			return true;
		}
		return false;
	}
	
	public void renderCarriage(Graphics gfx, int x, int y, byte direction)
	{
		Resource.get(type).renderCarriage(gfx, x, y, amount, direction);
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

	public float getLoadRatio()
	{
		if(type == Resource.NONE)
			return 0;
		return (float)amount / (float)(getSpecs().getStackLimit());
	}
	
	public void add(short amount)
	{
		this.amount += amount;
		if(this.amount > getSpecs().getStackLimit())
			this.amount = getSpecs().getStackLimit();
	}

	public void subtract(short amount)
	{
		this.amount -= amount;
		if(this.amount < 0)
		{
			this.amount = 0;
			type = Resource.NONE;
		}
	}
	
}



