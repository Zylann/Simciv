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

public class ContentLoadingScreen extends BasicGameState
{
	private int stateID = -1;
	private int totalLoaded = 0;
	private String lastLoaded = "";
	
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
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
			throws SlickException
	{
		// Display what is being loaded
		gfx.setColor(Color.white);
		gfx.drawString("Loading : " + lastLoaded, 100, 100);
		
		// Progress bar
		int w = 400;
		int h = 8;
		int x = 100;
		int y = 150;
		int t = (int) (w * (float)totalLoaded / (float)Content.getTotalCount());
		gfx.setColor(Color.green);
		gfx.fillRect(x, y, t, h);
		gfx.setColor(Color.gray);
		gfx.fillRect(x + t, y, w - t, h);
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
			totalLoaded++;
			lastLoaded = nextResource.getDescription();
		}
		else
		{
			// loading finished, entering next state
			LoadingList.setDeferredLoading(false);
			Content.indexAll();
			game.enterState(Game.STATE_MAIN_MENU);
		}
	}

	@Override
	public int getID()
	{
		return stateID;
	}
	
}
