package simciv.builds;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Map;
import simciv.MapGrid;
import simciv.content.Content;

public class WaterSource extends PassiveWorkplace
{
	private static final long serialVersionUID = 1L;
	private static BuildProperties properties;
	
	static
	{
		properties = new BuildProperties("Water source");
		properties.setUnitsCapacity(2)
			.setCategory(BuildCategory.FOOD)
			.setCost(20)
			.setSize(1, 1, 0)
			.setFlamable(false);
	}

	public WaterSource(Map m)
	{
		super(m);
	}

	@Override
	protected void onActivityStart()
	{
	}

	@Override
	protected void onActivityStop()
	{
	}

	@Override
	public BuildProperties getProperties()
	{
		return properties;
	}

	@Override
	protected void renderBuild(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		renderDefault(gfx, Content.sprites.buildWaterSource);
	}

	@Override
	public boolean canBePlaced(MapGrid map, int x, int y)
	{
		return super.canBePlaced(map, x, y) && map.isArable(x, y);
	}

	@Override
	public BuildReport getReport()
	{
		BuildReport report = super.getReport();
		
		if(isActive())
			report.add(BuildReport.INFO, "We provide water for the city.");
		
		return report;
	}

}
