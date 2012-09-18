package simciv.gamestates;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import simciv.Game;
import simciv.persistence.GameLoaderThread;
import simciv.persistence.GameSaveData;
import simciv.ui.base.IActionListener;
import simciv.ui.base.MessageBox;
import simciv.ui.base.Widget;

public class GameLoadingScreen extends GameInitScreen
{
	private GameSaveData gameData;
	private GameLoaderThread gameLoader;
	private MessageBox errorBox;
	private boolean backOnError;
	
	public GameLoadingScreen(int stateID)
	{
		super(stateID);
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
		backOnError = false;
		
		super.createUI(container, game);
		
		errorBox = new MessageBox(ui, 0, 0, 150, 70, "Loading error");
		errorBox.alignToCenter();
		errorBox.setVisible(false);
		errorBox.addCloseListener(new CancelAction());
		ui.add(errorBox);
		errorBox.popup();
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException
	{
		backOnError = false;
		
		super.enter(container, game);
		
		gameData = new GameSaveData("map");
		gameLoader = new GameLoaderThread(gameData);
		gameLoader.start();
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics gfx)
			throws SlickException
	{
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException
	{
		if(gameLoader.isFinished())
		{
			if(gameLoader.isSuccess())
			{
				simciv.Game game = (simciv.Game)sbg;
				game.cityView.setMap(gameData.map);
				game.cityView.setIsGameBeginning(true);
				game.enterState(Game.STATE_CITY_VIEW);
			}
			else if(!errorBox.isVisible() && !backOnError)
			{
				Log.error("Couldn't load saved game.\n The save file is corrupted or\n not compatible.");
				errorBox.setText("Couldn't load saved game.\n The save file is corrupted or\n not compatible.");
				errorBox.setVisible(true);
			}
			else if(backOnError)
			{
				sbg.enterState(Game.STATE_MAIN_MENU);
			}
		}		
	}
	
	class CancelAction implements IActionListener
	{
		@Override
		public void actionPerformed(Widget sender) {
			backOnError = true;
		}
	}

}


