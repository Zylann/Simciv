package simciv.builds;

import simciv.ResourceSlot;

/**
 * Objects implementing this interface can hold resources.
 * @author Marc
 *
 */
public interface IResourceHolder
{
	// TODO move all resource-related stuff into a resources package
	
	/**
	 * Counts how much resource of the given type are contained
	 * @param type : resource type
	 * @return
	 */
	public int getResourceTotal(byte type);
	
	/**
	 * Tests if the container contains food
	 * @return
	 */
	public boolean containsFood();
	
	public boolean store(ResourceSlot s, int amount);
	
	public boolean retrieve(ResourceSlot s, byte type, int amount);
	
	public boolean retrieveFood(ResourceSlot s, int amount);
	
	/**
	 * Computes the free space for storing the given type of resource.
	 * It may differ depending on the resource type.
	 * @param type : resource type
	 * @return free space in the container
	 */
	public int getFreeSpaceForResource(byte resourceType);
	
	/**
	 * Tests if the resource container is empty
	 * @return
	 */
	public boolean isEmpty();
	
	/**
	 * Tests if the resource container is full
	 * @return
	 */
	public boolean isFull();
	
	public boolean allowsStoring();
	
	public boolean allowsRetrieving();
	
}
