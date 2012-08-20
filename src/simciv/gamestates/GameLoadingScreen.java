package simciv.gamestates;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import simciv.CityBuilder;
import simciv.Game;
import simciv.MapGenerator;
import simciv.Nature;
import simciv.Resource;
import simciv.Road;
import simciv.Terrain;
import simciv.World;
import simciv.buildings.BuildCategory;
import simciv.ui.base.ProgressBar;
import simciv.ui.base.RootPane;
import simciv.ui.base.UIBasicGameState;
import simciv.ui.base.UIRenderer;
import simciv.ui.base.Widget;

public class GameLoadingScreen extends UIBasicGameState
{
	private int stateID = -1;
	private MapGenerator mapGenerator;
	private World world;
	private ProgressBar progressBar;
	
	public GameLoadingScreen(int stateID)
	{
		this.stateID = stateID;
	}
	
	@Override
	public int getID()
	{
		return stateID;
	}
	
	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException
	{
	}

	@Override
	protected void createUI(GameContainer container, StateBasedGame game)
			throws SlickException
	{
		UIRenderer.instance().setGlobalScale(2);
		int gs = UIRenderer.instance().getGlobalScale();
		ui = new RootPane(container.getWidth() / gs, container.getHeight() / gs);

		progressBar = new ProgressBar(ui, 0, 0, 300);
		progressBar.setAlign(Widget.ALIGN_CENTER);
		ui.add(progressBar);
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException
	{
		super.enter(container, game);
		
		Terrain.initialize();
		Resource.initialize();
		Road.loadContent();
		CityBuilder.loadContent();
		Nature.loadContent();
		BuildCategory.initialize();
		
		world = new World(256, 256);
		mapGenerator = new MapGenerator(131183, world.map);
		mapGenerator.start();
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta)
			throws SlickException
	{
		progressBar.setProgress(mapGenerator.getProgress());
		
		if(mapGenerator.isFinished())
			game.enterState(Game.STATE_GAMEPLAY);
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
			throws SlickException
	{
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException
	{
		super.leave(container, game);		
		((simciv.Game)game).gamePlay.setWorld(world);
	}

}



