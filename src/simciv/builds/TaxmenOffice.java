package simciv.builds;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Game;
import simciv.World;
import simciv.content.Content;
import simciv.jobs.InternalJob;
import simciv.jobs.Job;
import simciv.jobs.Taxman;
import simciv.movements.RandomRoadMovement;
import simciv.units.Citizen;

public class TaxmenOffice extends PassiveWorkplace
{
	private static BuildProperties properties;
	private static SpriteSheet sprites;
	
	static
	{
		properties = new BuildProperties("Taxmen office");
		properties.setCost(200).setSize(2, 2, 1).setUnitsCapacity(6).setCategory(BuildCategory.ADMINISTRATION);
	}
	
	public TaxmenOffice(World w)
	{
		super(w);
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
		for(Citizen emp : employees.values())
		{
			if(emp.getJob().getID() == Job.TAXMAN && !emp.isOut())
			{
				emp.exitBuilding(); // mission begin
				emp.setMovement(new RandomRoadMovement());
			}
		}
	}

	@Override
	protected void onActivityStop()
	{
		// TODO out employees must end their mission
	}

	@Override
	public void renderBuilding(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		renderDefault(gfx, sprites);
	}

	@Override
	public Job giveNextJob(Citizen citizen)
	{
		if(needEmployees())
		{
			Job job;
			if(employees.size() < 4) // 4 internals,
				job = new InternalJob(citizen, this, Job.TAXMEN_OFFICE_INTERNAL);
			else // and 2 for patrol
				job = new Taxman(citizen, this);
				
			addEmployee(citizen);
			return job;
		}
		return null;
	}

	@Override
	public BuildProperties getProperties()
	{
		return properties;
	}

}
