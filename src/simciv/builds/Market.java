package simciv.builds;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Map;
import simciv.content.Content;
import simciv.units.Jobs;

/**
 * A market allows distributing resources to the populaion
 * @author Marc
 *
 */
public class Market extends PassiveWorkplace
{
	private static final long serialVersionUID = 1L;

	private static BuildProperties properties;
	
	static
	{
		properties = new BuildProperties("Market");
		properties.setCost(50).setSize(2, 2, 1).setUnitsCapacity(8).setCategory(BuildCategory.TRADE);
	}
	
	public Market(Map m)
	{
		super(m);
	}

	@Override
	public void renderBuild(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		renderDefault(gfx, Content.sprites.buildMarket);
	}

	@Override
	public BuildProperties getProperties()
	{
		return properties;
	}

	@Override
	protected void onActivityStart()
	{
		addAndSpawnUnitsAround(Jobs.MARKET_DELIVERY, 3);
	}

	@Override
	protected void onActivityStop()
	{
		removeAllUnits();
	}

}



