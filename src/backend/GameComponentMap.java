package backend;

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
 * GameComponent container, providing easier methods for handling a group of components.
 * This container must be used in a context where it can be updated at each frame of the game.
 * Note : it behaves similarly to a map, but all add/remove methods are replaced by stage and dispose methods.
 * @author Marc
 *
 */
public class GameComponentMap implements Externalizable
{
	private static final long serialVersionUID = 1L;
	
	/** The main entity container **/
	private HashMap<Integer, GameComponent> components;
	
	/** Entities added while iterating on the main container **/
	private transient HashMap<Integer, GameComponent> stagedComponents;
	
	/** Entities removed while iterating on the main container **/
	private transient ArrayList<Integer> disposedComponentsIDs;
	
	/**
	 * Constructs an empty GameComponent map
	 */
	public GameComponentMap()
	{
		components = new HashMap<Integer, GameComponent>();
		stagedComponents = new HashMap<Integer, GameComponent>();
		disposedComponentsIDs = new ArrayList<Integer>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException
	{
		components = (HashMap<Integer, GameComponent>)oi.readObject(); // we know it's a map (see writeExternal)
		stagedComponents = new HashMap<Integer, GameComponent>();
		disposedComponentsIDs = new ArrayList<Integer>();
	}

	@Override
	public void writeExternal(ObjectOutput oo) throws IOException
	{
		flush();
		oo.writeObject(components);
	}
	
	/**
	 * Stages a new component that will be added on next update
	 * @param cmp
	 * @return
	 */
	public void stageComponent(GameComponent e)
	{
		if(e == null)
		{
			System.out.println("ERROR: null entity added to an entity map !");
			return;
		}
		stagedComponents.put(e.getID(), e);
	}
	
	/**
	 * Prepares a component to be disposed on next update
	 * @param cmpID : ID of the entity to remove
	 */
	public void disposeComponent(int ID)
	{
		GameComponent e = get(ID);
		if(e != null)
			e.dispose();
	}
	
	/**
	 * Gets a component from its ID
	 * @param ID : ID of the entity
	 * @return the component, null if not in the container
	 */
	public GameComponent get(int ID)
	{
		return components.get(ID);
	}
	
	/**
	 * Gets the contained entities as a collection.
	 * Note : do not modify this collection, better use it read-only
	 * @return game components contained in the map as a Collection
	 */
	public Collection<GameComponent> asCollection()
	{
		return components.values();
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
		for(GameComponent e : components.values())
		{
			if(e.isDisposed())
				disposedComponentsIDs.add(e.getID());
			else
				e.update(gc, game, delta);
		}
		
		flush();
	}
	
	/**
	 * Puts staged components in the main map and call their onInit() method,
	 * calls onDestruction() to each disposed components and clears them.
	 * It is called before serialization to make sure that all relevant components will be saved.
	 */
	public void flush()
	{
		for(GameComponent e : stagedComponents.values())
		{
			components.put(e.getID(), e);
			if(!e.isInitialized())
				e.onInit();
		}
		stagedComponents.clear();
		
		for(Integer id : disposedComponentsIDs)
		{
			GameComponent e = components.get(id);
			e.onDestruction();
			components.remove(id);
		}
		disposedComponentsIDs.clear();
	}

}




