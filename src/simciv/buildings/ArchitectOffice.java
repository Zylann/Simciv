package simciv.buildings;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Game;
import simciv.World;
import simciv.content.Content;
import simciv.jobs.Architect;
import simciv.jobs.InternalJob;
import simciv.jobs.Job;
import simciv.movements.RandomRoadMovement;
import simciv.units.Citizen;

public class ArchitectOffice extends PassiveWorkplace
{
	private static BuildingProperties properties;
	private static SpriteSheet sprites;
	
	// TODO factorize : ProductiveWorkplace
	// TODO factorize : PassiveWorkplace
	
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
	public Job giveNextJob(Citizen citizen)
	{
		if(needEmployees())
		{
			Job job;
			if(employees.size() < 2)
				job = new InternalJob(citizen, this, Job.ARCHITECT_INTERNAL);
			else
				job = new Architect(citizen, this);
			
			addEmployee(citizen);
			return job;
		}
		return null;
	}

	@Override
	protected void onActivityStart()
	{
		for(Citizen emp : employees.values())
		{
			if(emp.getJob().getID() == Job.ARCHITECT && !emp.isOut())
			{
				emp.exitBuilding(); // mission begin
				emp.setMovement(new RandomRoadMovement());
			}
		}
	}

	@Override
	protected void onActivityStop()
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

}




