package simciv.ui.base;
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
