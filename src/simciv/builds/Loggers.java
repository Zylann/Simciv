package simciv.builds;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Map;
import simciv.content.Content;

public class Loggers extends Workplace
{
	private static final long serialVersionUID = 1L;
	
	private static BuildProperties properties;
	
	static
	{
		properties = new BuildProperties("Loggers");
		properties.setCost(150).setCategory(BuildCategory.INDUSTRY)
			.setSize(3, 3, 1).setUnitsCapacity(8);
	}

	public Loggers(Map m)
	{
		super(m);
	}

	@Override
	public int getProductionProgress()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void onActivityStart()
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void onActivityStop()
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void tickActivity()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public BuildProperties getProperties()
	{
		return properties;
	}

	@Override
	protected void renderBuild(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		renderDefault(gfx, Content.sprites.buildLoggers);
	}

}


