package simciv;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Entity container, providing easier methods for handling a group of entities
 * @author Marc
 *
 */
public class EntityMap implements Externalizable
{
	private static final long serialVersionUID = 1L;
	
	// The main entity container
	private HashMap<Integer, Entity> entities;
	// Entities added while iterating on the main container
	private transient HashMap<Integer, Entity> newEntities;
	// Entities removed while iterating on the main container
	private transient ArrayList<Integer> disposedEntities;
	
	public EntityMap()
	{
		entities = new HashMap<Integer, Entity>();
		newEntities = new HashMap<Integer, Entity>();
		disposedEntities = new ArrayList<Integer>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException
	{
		entities = (HashMap<Integer, Entity>)oi.readObject(); // we know it's a map (see writeExternal)
		newEntities = new HashMap<Integer, Entity>();
		disposedEntities = new ArrayList<Integer>();
	}

	@Override
	public void writeExternal(ObjectOutput oo) throws IOException
	{
		flush();
		oo.writeObject(entities);
	}
	
	/**
	 * Stages a new component that will be added on next update
	 * @param cmp
	 * @return
	 */
	public void add(Entity e)
	{
		if(e == null)
		{
			System.out.println("ERROR: null entity added to an entity map !");
			return;
		}
		newEntities.put(e.getID(), e);
	}
	
	/**
	 * Prepares a component to be disposed on next update
	 * @param cmpID : ID of the entity to remove
	 */
	public void remove(int ID)
	{
		GameComponent e = get(ID);
		if(e != null)
			e.dispose();
	}

	/**
	 * Gets an entity from its ID
	 * @param ID : ID of the entity
	 * @return the entity, null if not in the container
	 */
	public Entity get(int ID)
	{
		return entities.get(ID);
	}
	
	/**
	 * Gets the contained entities as a collection.
	 * Note : do not modify this collection, better use it read-only
	 * @return game components contained in the map as a Collection
	 */
	public Collection<Entity> asCollection()
	{
		return entities.values();
	}
	
	/**
	 * Updates all game components contained in the map.
	 * Staged components are added and updated, and disposed component are erased.
	 * @param gc
	 * @param game
	 * @param delta
	 */
	public void updateAll(GameContainer gc, StateBasedGame game, int delta)
	{
		for(Entity e : entities.values())
		{
			if(e.isDisposed())
				disposedEntities.add(e.getID());
			else
				e.update(gc, game, delta);
		}
		
		flush();
	}
	
	/**
	 * Puts spawned entities in the main map and call their onInit() method,
	 * calls onDestruction() to each disposed entities and clears them.
	 * It is called before serialization to make sure that all relevant entities will be saved.
	 */
	public void flush()
	{
		for(Entity e : newEntities.values())
		{
			entities.put(e.getID(), e);
			if(!e.isInitialized())
				e.onInit();
		}
		newEntities.clear();
		
		for(Integer id : disposedEntities)
		{
			Entity e = entities.get(id);
			e.onDestruction();
			entities.remove(id);
		}
		disposedEntities.clear();
	}
		
}


