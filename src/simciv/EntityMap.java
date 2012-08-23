package simciv;

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
public class EntityMap
{
	private HashMap<Integer, Entity> entities;
	private HashMap<Integer, Entity> newEntities;
	private ArrayList<Integer> disposedEntities;
	
	public EntityMap()
	{
		entities = new HashMap<Integer, Entity>();
		newEntities = new HashMap<Integer, Entity>();
		disposedEntities = new ArrayList<Integer>();
	}
	
	/**
	 * Stages a new component that will be added on next update
	 * @param cmp
	 * @return
	 */
	public void add(Entity e)
	{
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

	public Entity get(int ID)
	{
		return entities.get(ID);
	}
	
	/**
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
		for(Entity e : newEntities.values())
		{
			entities.put(e.getID(), e);
			e.onInit();
		}
		newEntities.clear();
		for(Entity e : entities.values())
		{
			e.update(gc, game, delta);
			if(e.isDisposed())
				disposedEntities.add(e.getID());
		}
		for(Integer id : disposedEntities)
		{
			Entity e = entities.get(id);
			if(e != null)
				e.onDestruction();
			entities.remove(e.getID());
		}
		disposedEntities.clear();
	}

}


