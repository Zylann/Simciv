package simciv.builds;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Map;
import simciv.content.Content;
import simciv.units.Jobs;

public class TaxmenOffice extends PassiveWorkplace
{
	private static final long serialVersionUID = 1L;
	
	private static BuildProperties properties;
	
	static
	{
		properties = new BuildProperties("Taxmen office");
		properties.setCost(200).setSize(2, 2, 1).setUnitsCapacity(6).setCategory(BuildCategory.ADMINISTRATION);
	}
	
	public TaxmenOffice(Map m)
	{
		super(m);
	}
	
	@Override
	protected void onActivityStart()
	{
		addAndSpawnUnitsAround(Jobs.TAXMAN, 2);
	}

	@Override
	protected void onActivityStop()
	{
		removeAllUnits();
	}

	@Override
	public void renderBuild(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		renderDefault(gfx, Content.sprites.buildTaxmenOffice);
	}

	@Override
	public BuildProperties getProperties()
	{
		return properties;
	}

}
