package simciv.units;

import java.util.List;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

import simciv.Game;
import simciv.Map;
import simciv.builds.Build;
import simciv.builds.House;
import simciv.builds.Workplace;
import simciv.content.Content;
import simciv.effects.RisingIcon;
import simciv.movements.RandomRoadMovement;

/**
 * An employer will search for inactive persons in the city,
 * in order to hire them in their workplace.
 * @author Marc
 *
 */
public class Employer extends Citizen
{
	private static SpriteSheet unitSprites;
	
	public Employer(Map m, Workplace w)
	{
		super(m, w);
		setMovement(new RandomRoadMovement());
		
		if(unitSprites == null)
			unitSprites = new SpriteSheet(Content.images.unitEmployer, Game.tilesSize, Game.tilesSize);
	}

	@Override
	public byte getJobID()
	{
		return Job.EMPLOYER;
	}

	@Override
	protected void renderUnit(Graphics gfx)
	{
		defaultRender(gfx, unitSprites);
	}

	@Override
	protected void tick()
	{
		if(!workplaceRef.needEmployees())
			dispose();
		
		// Search for redundant people
		List<Build> builds = mapRef.getBuildsAround(getX(), getY());
		for(Build b : builds)
		{
			if(b.isHouse())
			{
				House h = (House)b;
				
				int availablePeople = h.getNbInhabitants() - h.getNbWorkers();					
				if(availablePeople > 0)
				{
					int neededEmployees = workplaceRef.getNbNeededEmployees();					
					if(availablePeople > neededEmployees)
						availablePeople = neededEmployees;

					for(int i = 0; i < availablePeople; i++)
					{
						h.addWorker(workplaceRef);
						workplaceRef.addEmployee(h);
					}
					
					// Visual feedback
					mapRef.addGraphicalEffect(
							new RisingIcon(
									b.getX(), b.getY(), 
									Content.images.effectGreenStar));
				}
			}
		}
	}

}



