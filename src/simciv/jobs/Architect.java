package simciv.jobs;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

import simciv.Game;
import simciv.buildings.Workplace;
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
		// TODO builds maintenance
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


