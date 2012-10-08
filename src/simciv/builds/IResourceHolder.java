package simciv.builds;

import simciv.resources.ResourceSlot;

/**
 * Builds implementing this interface can hold resources.
 * @author Marc
 *
 */
public interface IResourceHolder
{
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
	
	/**
	 * Moves resources from the given slot to the container.
	 * @param s : source slot
	 * @param amount : resource amount to move. -1 means as much as possible.
	 * @return true if resources has been moved.
	 */
	public boolean store(ResourceSlot s, int amount);
	
	/**
	 * Moves resources from the container to the given slot.
	 * @param s : destination slot
	 * @param type : resource type we want to move
	 * @param amount : resource amount to move. -1 means as much as possible.
	 * @return true if resources has been moved.
	 */
	public boolean retrieve(ResourceSlot s, byte type, int amount);
	
	/**
	 * Same as retrieve, but the resource type will be food.
	 * The food type is undetermined.
	 * Will do nothing if the container contains no food.
	 * @param s : destination slot
	 * @param amount : food amount to move. -1 means as much as possible.
	 * @return true if we found food and moved it.
	 */
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
	
	/**
	 * @return true if storing resources is allowed 
	 * in this container, false otherwise.
	 */
	public boolean allowsStoring();
	
	/**
	 * @return true if getting resources from this container 
	 * is allowed, false otherwise.
	 */
	public boolean allowsRetrieving();
	
}


