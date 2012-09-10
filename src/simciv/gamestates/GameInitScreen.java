package simciv.gamestates;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import simciv.CityBuilder;
import simciv.Resource;
import simciv.Road;
import simciv.Terrain;
import simciv.builds.BuildCategory;
import simciv.ui.base.ProgressBar;
import simciv.ui.base.RootPane;
import simciv.ui.base.UIBasicGameState;
import simciv.ui.base.UIRenderer;

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
		UIRenderer.instance().setGlobalScale(2);
		int gs = UIRenderer.instance().getGlobalScale();
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
		CityBuilder.loadContent();
		BuildCategory.initialize();
	}

}


