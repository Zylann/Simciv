package simciv.gamestates;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import backend.ui.UIRenderer;

import simciv.Game;
import simciv.content.Content;
import simciv.ui.UITheme;

// TODO replace this state by a Swing splash screen
public class ContentLoadingScreen extends BasicGameState
{
	private int stateID = -1;
	
	public ContentLoadingScreen(int stateID)
	{
		this.stateID = stateID;
	}

	@Override
	public void init(GameContainer gc, StateBasedGame game)
			throws SlickException
	{
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException
	{
		super.enter(container, game);		
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
			throws SlickException
	{
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta)
			throws SlickException
	{
		Log.info("Loading game content...");
		
		Content.loadFromContentFile("data/content.xml");
		Content.indexAll();
		
		UITheme customTheme = new UITheme();
		customTheme.loadContent();
		UIRenderer.setTheme(customTheme);

		Log.info("Game content loaded");
		
		
		game.enterState(Game.STATE_MAIN_MENU);
	}

	@Override
	public int getID()
	{
		return stateID;
	}
	
}
