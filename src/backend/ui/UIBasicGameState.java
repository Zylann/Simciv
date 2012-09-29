package backend.ui;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * A BasicGameState that holds a graphical user interface.
 * @author Marc
 *
 */
public abstract class UIBasicGameState extends BasicGameState
{
	public RootPane ui;
	
	/**
	 * Creates the UI associated with this game state.
	 * If the ui member is null, this method is called each time the state is entered.
	 * @param container
	 * @param game
	 * @throws SlickException
	 */
	protected abstract void createUI(GameContainer container, final StateBasedGame game)
			throws SlickException;

	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException
	{
		if (ui == null)
		{
			createUI(container, game);
		}
		((UIStateBasedGame) game).setUI(ui);
	}
	
}
