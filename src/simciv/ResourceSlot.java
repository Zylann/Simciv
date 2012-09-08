package simciv;

import java.io.Serializable;

import org.newdawn.slick.Graphics;

/**
 * A slot that can contain a certain amount of resource of the same type
 * @author Marc
 *
 */
public class ResourceSlot implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private byte type; // resource type (or last type stored)
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
	
	/**
	 * Sets the type and the amount of resource in the slot.
	 * Stack limits are not checked.
	 * @param type : resource type
	 * @param amount : resource amount
	 */
	private void set(byte type, int amount)
	{
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
		return amount == 0;
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
	 * Returns the available space left in the slot
	 * @return
	 */
	public int getFreeSpace()
	{
		return getSpecs().getStackLimit() - amount;
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
		// The slot must have free space
		if(isFull())
			return false;

		if(other.isEmpty() || amountToTransfert == 0)
			return false;
		
		// Types must be compatible
		if(amount > 0 && type != other.type)
			return false;
		
		if(amountToTransfert < 0 || amountToTransfert > other.amount)
			amountToTransfert = other.amount;
		
		// Calculate how much space the current slot have
		type = other.type; // in case the current slot type is NONE
		int spaceLeft = Resource.get(type).getStackLimit() - amount;
		
		// Transfert
		if(amountToTransfert > spaceLeft) // Not enough space in current slot
		{
			amount += spaceLeft;
			other.amount -= spaceLeft;
		}
		else // enough space
		{
			amount += amountToTransfert;
			other.amount -= amountToTransfert;
		}
					
		return true;
	}
	
	/**
	 * Adds as much as possible resources from the given slot.
	 * @param other : source
	 * @return : true if more than 0 resources has been moved
	 */
	public boolean addAllFrom(ResourceSlot other)
	{
		return addFrom(other, -1);
	}
	
	public void renderCarriage(Graphics gfx, int x, int y, byte direction)
	{
		if(isEmpty())
			Resource.renderEmptyCarriage(gfx, x, y, direction);
		else
			Resource.get(type).renderCarriage(gfx, x, y, amount, direction);
	}
	
	public void renderStorage(Graphics gfx, int x, int y)
	{
		if(!isEmpty())
			Resource.get(type).renderStorage(gfx, x, y, amount);
	}	
	
	public String toString()
	{
		if(isEmpty())
			return "Empty (" + amount + ")";
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
	
	/**
	 * Adds a positive amount to the slot.
	 * Does nothing if the slot type is NONE.
	 * @param a
	 */
	public void add(int a)
	{
		if(a <= 0 || type == Resource.NONE)
			return;
		this.amount += a;
		if(this.amount > getSpecs().getStackLimit())
			this.amount = getSpecs().getStackLimit();
	}

	/**
	 * Subtracts a positive amount to the slot.
	 * Does nothing if the slot type is NONE.
	 * @param a
	 */
	public void subtract(int a)
	{
		if(a <= 0 || type == Resource.NONE)
			return;
		this.amount -= a;
		if(this.amount <= 0)
		{
			this.amount = 0;
			type = Resource.NONE;
		}
	}
	
}



