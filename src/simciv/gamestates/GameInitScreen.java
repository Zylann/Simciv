package simciv.gamestates;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import backend.ui.ProgressBar;
import backend.ui.RootPane;
import backend.ui.UIBasicGameState;
import backend.ui.UIRenderer;

import simciv.Road;
import simciv.Terrain;
import simciv.builds.BuildCategory;
import simciv.resources.Resource;

public abstract class GameInitScreen extends UIBasicGameState
{
	private int stateID = -1;
	protected ProgressBar progressBar;

	public GameInitScreen(int stateID)
	{
		this.stateID = stateID;
	}

	@Override
	public int getID()
	{
		return stateID;
	}
	
	@Override
	protected void createUI(GameContainer container, StateBasedGame game)
			throws SlickException
	{
		UIRenderer.setGlobalScale(2);
		int gs = UIRenderer.getGlobalScale();
		ui = new RootPane(container.getWidth() / gs, container.getHeight() / gs);

		progressBar = new ProgressBar(ui, 0, 0, 300);
		progressBar.alignToCenter();
		ui.add(progressBar);
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException
	{
		super.enter(container, game);
		
		Terrain.initialize();
		Resource.initialize();
		Road.initialize();
		BuildCategory.initialize();
	}

}


