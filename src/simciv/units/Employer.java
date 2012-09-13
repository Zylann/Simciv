package simciv.units;

import java.util.List;

import org.newdawn.slick.Graphics;

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
	private static final long serialVersionUID = 1L;
	
	public Employer(Map m, Workplace w)
	{
		super(m, w);
		setMovement(new RandomRoadMovement());
	}

	@Override
	public byte getJobID()
	{
		return Job.EMPLOYER;
	}

	@Override
	protected void renderUnit(Graphics gfx)
	{
		defaultRender(gfx, Content.sprites.unitEmployer);
	}

	@Override
	public void tick()
	{
		Workplace workplace = getWorkplace();
		
		if(!workplace.needEmployees())
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
					int neededEmployees = workplace.getNbNeededEmployees();					
					if(availablePeople > neededEmployees)
						availablePeople = neededEmployees;

					for(int i = 0; i < availablePeople; i++)
					{
						h.addWorker(workplace);
						workplace.addEmployee(h);
					}
					
					// Visual feedback
					mapRef.addGraphicalEffect(
						new RisingIcon(
							b.getX(), b.getY(), 
							Content.sprites.effectGreenStar));
				}
			}
		}
	}

}



