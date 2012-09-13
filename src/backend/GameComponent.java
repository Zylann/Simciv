package backend;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
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

	/** Used to generate unique IDs **/
	/* It MUST start with 1, 0 is reserved for "null" (map storage convenience) */
	private static int nextID = 1;
	
	/** Unique numeric identifier **/
	private int ID = -1;
	
	/** True if the component has been initialized **/
	private boolean initialized;
	
	/** True if the component has been disposed 
	 * (Means that we don't need this component anymore) **/
	private boolean disposed;
	
	/**
	 * Computes an unique ID for a new game component.
	 * @return new ID
	 */
	public static final int makeUniqueID()
	{
		return nextID++;
	}
	
	/**
	 * Constructs a game component with a new unique ID.
	 * (also default constructor)
	 */
	public GameComponent()
	{
		ID = makeUniqueID();
		initialized = false;
		disposed = false;
	}
	
	/**
	 * Reads the component from an object input (custom Serializable method)
	 * @param oi
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void readObject(ObjectInput oi) throws IOException, ClassNotFoundException
	{
		ID = oi.readInt();
		initialized = oi.readBoolean();
		disposed = oi.readBoolean();
		
		// Prevents generation of double IDs
		if(nextID <= ID)
			nextID = ID + 1;
	}

	/**
	 * Writes the component into an object output (custom Serializable method)
	 * @param oo
	 * @throws IOException
	 */
	public void writeObject(ObjectOutput oo) throws IOException
	{
		oo.writeInt(ID);
		oo.writeBoolean(initialized);
		oo.writeBoolean(disposed);
	}

	/**
	 * @return true if the component is initialized
	 */
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

	/**
	 * True if the component has been disposed 
	 * (Means that we don't need this component anymore)
	 * @return isDisposed flag
	 */
	public final boolean isDisposed()
	{
		return disposed;
	}
	
	/**
	 * Called when we start using the component
	 * Override : Don't forget to call super.onInit()
	 * Only works in a context where the component is stored in a GameComponentMap.
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
	 * Only works in a context where the component is stored in a GameComponentMap.
	 */
	public abstract void onDestruction();
	
}


