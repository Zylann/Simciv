package simciv.gamestates;

import java.io.IOException;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Game;
import simciv.content.Content;

public class LoadingScreen extends BasicGameState
{
	int stateID = -1;	
	String lastLoaded = "";
	
	public LoadingScreen(int stateID)
	{
		this.stateID = stateID;
	}

	@Override
	public void init(GameContainer gc, StateBasedGame game)
			throws SlickException
	{
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
			throws SlickException
	{
		gfx.setColor(Color.white);
		gfx.drawString("Loading : " + lastLoaded, 100, 100);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta)
			throws SlickException
	{
		if(LoadingList.get().getRemainingResources() > 0)
		{
			// loading queued resources
			DeferredResource nextResource = LoadingList.get().getNext();
			try
			{
				nextResource.load();
			}
			catch(IOException e)
			{
				throw new SlickException("Could not load resource", e);
			}
			lastLoaded = nextResource.getDescription();
		}
		else
		{
			// loading finished, entering next state
			Content.indexAll();
			game.enterState(Game.STATE_GAMEPLAY);
		}
	}

	@Override
	public int getID()
	{
		return stateID;
	}
	
}
