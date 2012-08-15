package simciv.buildings;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Game;
import simciv.World;
import simciv.content.Content;
import simciv.jobs.Job;
import simciv.units.Citizen;

public class ArchitectOffice extends Workplace
{
	private static BuildingProperties properties;
	private static SpriteSheet sprites;
	
	// TODO ProductiveWorkplace
	// TODO PassiveWorkplace
	
	static
	{
		properties = new BuildingProperties("Architects office");
		properties.setCost(100).setSize(2, 2, 2).setUnitsCapacity(4);
	}
	
	public ArchitectOffice(World w)
	{
		super(w);
		if(sprites == null)
		{
			sprites = new SpriteSheet(Content.images.buildArchitectOffice,
					getWidth() * Game.tilesSize,
					3 * Game.tilesSize);
		}
	}

	@Override
	public int getProductionProgress()
	{
		return 0;
	}

	@Override
	public Job giveNextJob(Citizen citizen)
	{
//		if(needEmployees())
//		{
//			if(employees.size() < 2)
//				return new InternalJob(citizen, this, Job.ARCHITECT_INTERNAL);
//			else
//				return new Architect(citizen, this);
//		}
		return null;
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
	protected void tickActivity()
	{
	}

	@Override
	public BuildingProperties getProperties()
	{
		return properties;
	}

	@Override
	protected void renderBuilding(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		if(state == Building.ACTIVE)
			gfx.drawImage(sprites.getSprite(1, 0), 0, -Game.tilesSize);
		else
			gfx.drawImage(sprites.getSprite(0, 0), 0, -Game.tilesSize);
	}

	@Override
	protected int getTickTime()
	{
		return 1000;
	}

}




