package simciv;

import java.util.HashMap;

/**
 * Resource container used to store any types of resources, but always one slot per type.
 * @author Marc
 *
 */
public class ResourceBag
{
	private HashMap<Byte, ResourceSlot> slots; // Type, slot
	
	public ResourceBag()
	{
		slots = new HashMap<Byte, ResourceSlot>();
	}
	
	public void addFrom(ResourceSlot r, int amount)
	{
		ResourceSlot slot = slots.get(r.getType());
		if(slot == null)
		{
			slot = new ResourceSlot();
			slots.put(r.getType(), slot);
		}
		slot.addFrom(r, amount);
		if(slot.isEmpty())
			slots.remove(slot.getType());
	}
	
	public void addAllFrom(ResourceSlot r)
	{
		addFrom(r, -1);
	}

	public ResourceSlot getSlot(byte type)
	{
		return slots.get(type);
	}
	
	public ResourceSlot getFoodSlot()
	{
		for(ResourceSlot slot : slots.values())
		{
			if(slot.getSpecs().isFood())
				return slot;
		}
		return null;
	}
	
	public int getNbDifferentSlots()
	{
		return slots.size();
	}
	
}



