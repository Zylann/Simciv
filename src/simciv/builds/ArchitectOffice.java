package simciv.builds;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Game;
import simciv.Map;
import simciv.content.Content;
import simciv.units.Job;

public class ArchitectOffice extends PassiveWorkplace
{
	private static BuildProperties properties;
	private static SpriteSheet sprites;
	
	// TODO factorize : ProductiveWorkplace
	
	static
	{
		properties = new BuildProperties("Architects office");
		properties.setCost(100).setSize(2, 2, 2).setUnitsCapacity(4).setCategory(BuildCategory.ADMINISTRATION);
	}
	
	public ArchitectOffice(Map m)
	{
		super(m);
		if(sprites == null)
		{
			sprites = new SpriteSheet(Content.images.buildArchitectOffice,
					getWidth() * Game.tilesSize,
					3 * Game.tilesSize);
		}
	}

	@Override
	protected void onActivityStart()
	{
		addAndSpawnUnitsAround(Job.ARCHITECT, 2);
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
		if(state == Build.STATE_ACTIVE)
			gfx.drawImage(sprites.getSprite(1, 0), 0, -Game.tilesSize);
		else
			gfx.drawImage(sprites.getSprite(0, 0), 0, -Game.tilesSize);
	}

}




