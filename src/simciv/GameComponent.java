package simciv;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Base class for all game elements
 * @author Marc
 *
 */
public abstract class GameComponent implements IRenderable
{
	// Used to generate unique IDs
	// It MUST start with 1 (map storage convenience)
	private static int nextID = 1;
	
	private int ID = -1;
	private transient boolean dispose = false;
	
	public static final int makeUniqueID()
	{
		return nextID++;
	}
	
	public GameComponent()
	{
		ID = makeUniqueID();
	}
	
	public final int getID()
	{
		return ID;
	}
	
	public void dispose()
	{
		dispose = true;
	}
	
	public boolean isDisposed()
	{
		return dispose;
	}

	/**
	 * Called when we start using the component
	 * (Note that it is always the first method to be called after construction)
	 */
	public abstract void onInit();
	
	public abstract void update(GameContainer gc, StateBasedGame game, int delta);
	
	/**
	 * Called just before the object to be deleted.
	 * (Note that it is always the last method to be called)
	 */
	public abstract void onDestruction();
	
}


