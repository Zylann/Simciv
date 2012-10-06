package simciv.builds;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Game;
import simciv.Map;
import simciv.content.Content;
import simciv.units.Jobs;

public class ArchitectOffice extends PassiveWorkplace
{
	private static final long serialVersionUID = 1L;

	private static BuildProperties properties;
	
	// TODO factorize : ProductiveWorkplace
	
	static
	{
		properties = new BuildProperties("Architects office");
		properties.setCost(100).setSize(2, 2, 2).setUnitsCapacity(6).setCategory(BuildCategory.ADMINISTRATION);
	}
	
	public ArchitectOffice(Map m)
	{
		super(m);
	}

	@Override
	protected void onActivityStart()
	{
		addAndSpawnUnitsAround(Jobs.ARCHITECT, 2);
	}

	@Override
	protected void onActivityStop()
	{
		removeAllUnits();
	}

	@Override
	public BuildProperties getProperties()
	{
		return properties;
	}

	@Override
	protected void renderBuild(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		if(isActive())
			gfx.drawImage(Content.sprites.buildArchitectOffice.getSprite(1, 0), 0, -Game.tilesSize);
		else
			gfx.drawImage(Content.sprites.buildArchitectOffice.getSprite(0, 0), 0, -Game.tilesSize);
	}

}




