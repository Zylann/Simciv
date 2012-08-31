package simciv.builds;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Game;
import simciv.Map;
import simciv.content.Content;
import simciv.units.Job;

public class TaxmenOffice extends PassiveWorkplace
{
	private static BuildProperties properties;
	private static SpriteSheet sprites;
	
	static
	{
		properties = new BuildProperties("Taxmen office");
		properties.setCost(200).setSize(2, 2, 1).setUnitsCapacity(6).setCategory(BuildCategory.ADMINISTRATION);
	}
	
	public TaxmenOffice(Map m)
	{
		super(m);
		state = Build.STATE_NORMAL;

		if(sprites == null)
		{
			sprites = new SpriteSheet(Content.images.buildTaxmenOffice, 
					Game.tilesSize * getWidth(),
					Game.tilesSize * (getHeight() + getZHeight()));
		}
	}
	
	@Override
	protected void onActivityStart()
	{
		addAndSpawnUnitsAround(Job.TAXMAN, 2);
	}

	@Override
	protected void onActivityStop()
	{
		removeAllUnits();
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

}
