package simciv.jobs;

import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

import simciv.Game;
import simciv.PlayerCity;
import simciv.builds.Build;
import simciv.builds.Workplace;
import simciv.content.Content;
import simciv.units.Citizen;

public class Architect extends Job
{
	private static SpriteSheet unitSprites;
	
	public Architect(Citizen citizen, Workplace workplace)
	{
		super(citizen, workplace);
		if(unitSprites == null)
			unitSprites = new SpriteSheet(Content.images.unitArchitect, Game.tilesSize, Game.tilesSize);
	}

	@Override
	public void tick()
	{
		List<Build> builds = me.getWorld().getBuildsAround(me.getX(), me.getY());
		PlayerCity city = me.getWorld().playerCity;
		for(Build b : builds)
		{
			if(b.needsMaintenance())
			{
				float cost = b.getSurfaceArea() * 0.3f;
				if(city.getMoney() >= cost && b.onMaintenance())
					city.buy(cost);
			}
		}
	}

	@Override
	public void onBegin()
	{
		me.enterBuilding(workplaceRef);
	}

	@Override
	public byte getID()
	{
		return Job.ARCHITECT;
	}

	@Override
	public void renderUnit(Graphics gfx)
	{
		me.defaultRender(gfx, unitSprites);
	}

	@Override
	public int getIncome()
	{
		return 15;
	}

	@Override
	public int getTickTimeOverride()
	{
		return 200;
	}

}


