package simciv.gamestates;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Game;
import simciv.MapGeneratorThread;
import simciv.Map;

public class GameCreatingScreen extends GameInitScreen
{
	private MapGeneratorThread mapGenerator;
	private Map map;
	
	public GameCreatingScreen(int stateID)
	{
		super(stateID);
	}
	
	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException
	{
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException
	{
		super.enter(container, game);
		
		map = new Map(256, 256);
		mapGenerator = new MapGeneratorThread(131183, map);
		mapGenerator.start();
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta)
			throws SlickException
	{
		progressBar.setProgress(mapGenerator.getProgress());
		
		if(mapGenerator.isFinished())
			game.enterState(Game.STATE_CITY_VIEW);
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
			throws SlickException
	{
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame sbg)
			throws SlickException
	{
		super.leave(container, sbg);		
		
		simciv.Game game = (simciv.Game)sbg;
		game.cityView.setMap(map);
		game.cityView.setIsGameBeginning(true);
	}

}



