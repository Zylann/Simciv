package simciv.builds;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Game;
import simciv.Map;
import simciv.content.Content;
import simciv.units.Job;

/**
 * A market allows distributing resources to the populaion
 * @author Marc
 *
 */
public class Market extends PassiveWorkplace
{
	private static SpriteSheet sprites;
	private static BuildProperties properties;
	
	static
	{
		properties = new BuildProperties("Market");
		properties.setCost(50).setSize(2, 2, 1).setUnitsCapacity(6).setCategory(BuildCategory.TRADE);
	}
	
	public Market(Map m)
	{
		super(m);
		state = Build.STATE_NORMAL;
		if(sprites == null)
		{
			sprites = new SpriteSheet(Content.images.buildMarket,
					getWidth() * Game.tilesSize,
					(getHeight() + getZHeight()) * Game.tilesSize);
		}
	}

	@Override
	public void renderBuild(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		renderDefault(gfx, sprites);
	}

	@Override
	public BuildProperties getProperties()
	{
		return properties;
	}

	@Override
	protected void onActivityStart()
	{
		addAndSpawnUnitsAround(Job.MARKET_DELIVERY, 3);
	}

	@Override
	protected void onActivityStop()
	{
		removeAllUnits();
	}

}



