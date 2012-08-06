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
	 * @param amountToTransfert : amount to transfert. -1 means all.
	 * @return true if more than 0 resources were moved.
	 */
	public boolean addFrom(ResourceSlot other, int amountToTransfert)
	{
		// If the slot is compatible or free
		if(type == other.type || !isFull())
		{
			if(other.isEmpty() || amountToTransfert == 0)
				return false;
			
			if(amountToTransfert < 0 || amountToTransfert > other.amount)
				amountToTransfert = other.amount;
			
			// Calculate how much space the current slot have
			type = other.type; // in case the current slot type is NONE
			int spaceLeft = Resource.get(type).getStackLimit() - amount;
			
			if(amountToTransfert > spaceLeft) // Not enough space in current slot
			{
				amount += spaceLeft;
				other.amount -= spaceLeft;
				if(other.amount == 0)
					other.type = Resource.NONE;
			}
			else // enough space
			{
				amount += amountToTransfert;
				other.amount -= amountToTransfert;
				other.type = Resource.NONE;
			}
			
			return true;
		}
		return false;
	}
	
	public boolean addAllFrom(ResourceSlot other)
	{
		return addFrom(other, -1);
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
	
	public void add(int amount)
	{
		this.amount += amount;
		if(this.amount > getSpecs().getStackLimit())
			this.amount = getSpecs().getStackLimit();
	}

	public void subtract(int amount)
	{
		this.amount -= amount;
		if(this.amount < 0)
		{
			this.amount = 0;
			type = Resource.NONE;
		}
	}
	
}



