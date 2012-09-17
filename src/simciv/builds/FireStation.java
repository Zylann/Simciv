package simciv.builds;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Map;
import simciv.content.Content;

public class FireStation extends PassiveWorkplace
{
	private static final long serialVersionUID = 1L;
	private static BuildProperties properties;
	
	static
	{
		properties = new BuildProperties("Fire station");
		properties.setCategory(BuildCategory.ADMINISTRATION)
			.setCost(100)
			.setSize(2, 2, 1)
			.setUnitsCapacity(6);
	}
	
	public FireStation(Map m)
	{
		super(m);
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
	public BuildProperties getProperties()
	{
		return properties;
	}

	@Override
	protected void renderBuild(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		this.renderDefault(gfx, Content.sprites.buildFireStation);
	}

}


