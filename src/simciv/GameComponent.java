package simciv;

import java.io.Serializable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Base class for all game elements.
 * Game components life is like this : 
 * 1) Component creation, constructor called
 * 2) Start using the component, onInit is called
 * 3) Call to update
 * 4) Call to render, and on next frame go to 3) until not disposed
 * 5) Stop using component, call to onDestruction
 * 6) The component is removed from memory
 * @author Marc
 *
 */
public abstract class GameComponent implements IRenderable, Serializable
{
	private static final long serialVersionUID = 1L;

	// Used to generate unique IDs
	// It MUST start with 1, 0 is reserved for "null" (map storage convenience)
	private static int nextID = 1;
	
	private int ID = -1; // Unique numeric identifier
	private boolean initialized;
	private boolean disposed; // If true, the object must be destroyed
	
	public static final int makeUniqueID()
	{
		return nextID++;
	}
	
	/**
	 * Constructs a game component with a new unique ID.
	 */
	public GameComponent()
	{
		ID = makeUniqueID();
		initialized = false;
		disposed = false;
	}
	
	public final boolean isInitialized()
	{
		return initialized;
	}
	
	/**
	 * Returns the unique numerical identifier of the component.
	 * @return
	 */
	public final int getID()
	{
		return ID;
	}
	
	/**
	 * Sets the disposed flag to false, i.e it will be deleted as soon as possible by the component manager.
	 * Note 1 : in many cases, the object will not be deleted immediately.
	 * Note 2 : this is more convenient than set the component to null, as we can control the object's lifespan.
	 * Overriding : don't forget to call super.dispose() !
	 */
	public final void dispose()
	{
		if(!disposed)
			onDispose();
		disposed = true;
	}
	
	/**
	 * Called when dispose() is called and the game component were not disposed before.
	 * If dispose is called again, this method will not be called again.
	 */
	protected void onDispose()
	{
	}

	public final boolean isDisposed()
	{
		return disposed;
	}
	
	/**
	 * Called when we start using the component
	 * (Note that it is always the first method to be called after construction)
	 * Override : Don't forget to call super.onInit()
	 */
	public void onInit()
	{
		initialized = true;
	}
	
	/**
	 * Called regularly to make the component active during each game loop
	 * @param gc
	 * @param game
	 * @param delta : frame time in milliseconds
	 */
	public abstract void update(GameContainer gc, StateBasedGame game, int delta);
	
	/**
	 * Called just before the object to be deleted (nulled).
	 * Note that it is always the last method to be called.
	 */
	public abstract void onDestruction();
	
}


