package simciv.gamestates;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Game;
import simciv.content.Content;

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
		System.out.println("Loading game content...");
		
		Content.loadFromContentFile("data/content.xml");
		Content.indexAll();
		
		System.out.println("Game content loaded");
		
		game.enterState(Game.STATE_MAIN_MENU);
	}

	@Override
	public int getID()
	{
		return stateID;
	}
	
}
