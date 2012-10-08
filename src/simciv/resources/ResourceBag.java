package simciv.resources;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Resource container used to store any types of resources, but always one slot per type.
 * @author Marc
 *
 */
public class ResourceBag implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private HashMap<Byte, ResourceSlot> slots; // Type, slot
	
	public ResourceBag()
	{
		slots = new HashMap<Byte, ResourceSlot>();
	}
	
	public void addFrom(ResourceSlot r, int amount)
	{
		if(amount == 0 || r.isEmpty())
			return;
		ResourceSlot slot = slots.get(r.getType());
		if(slot == null)
		{
			slot = new ResourceSlot();
			slots.put(r.getType(), slot);
		}
		slot.addFrom(r, amount);
	}
	
	public void addAllFrom(ResourceSlot r)
	{
		addFrom(r, -1);
	}
	
	public void addAllFrom(ResourceBag resources)
	{
		for(ResourceSlot s : resources.slots.values())
			addAllFrom(s);
	}

	public int getAmount(byte type)
	{
		ResourceSlot slot = slots.get(type);
		if(slot == null)
			return 0;
		return slot.getAmount();
	}
	
	/**
	 * If the bag contains food, returns the first food type.
	 * Returns NONE if no non-empty slot was found.
	 * @return
	 */
	public byte getContainedFoodType()
	{
		for(ResourceSlot slot : slots.values())
		{
			if(slot.getSpecs().isFood() && !slot.isEmpty())
				return slot.getType();
		}
		return Resource.NONE;
	}

	public boolean containsFood()
	{
		return getContainedFoodType() != Resource.NONE;
	}
	
	public boolean subtract(byte type, int amount)
	{
		ResourceSlot slot = slots.get(type);
		if(slot == null)
			return false;
		slot.subtract(amount);
		if(slot.isEmpty())
			slots.remove(type);
		return true;
	}
	
	public boolean addToExisting(byte type, int amount)
	{
		ResourceSlot slot = slots.get(type);
		if(slot == null)
			return false;
		slot.add(amount);
		return true;
	}

	public int getNbDifferentSlots()
	{
		return slots.size();
	}
		
}



