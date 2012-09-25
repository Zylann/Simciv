package simciv.units;

import java.util.List;

import org.newdawn.slick.Graphics;
import simciv.Map;
import simciv.PlayerCity;
import simciv.builds.Build;
import simciv.builds.Workplace;
import simciv.content.Content;
import simciv.movement.RandomRoadMovement;

/**
 * Architects walks randomly through the city, and repair builds.
 * Each maintenance costs money.
 * @author Marc
 *
 */
public class Architect extends Citizen
{
	private static final long serialVersionUID = 1L;
	
	public Architect(Map m, Workplace w)
	{
		super(m, w);
		setMovement(new RandomRoadMovement());
	}

	@Override
	public void tick()
	{
		PlayerCity city = mapRef.playerCity;
		List<Build> builds = mapRef.getBuildsAround(getX(), getY());
		
		// Repair builds around the current position
		for(Build b : builds)
		{
			if(b.needsMaintenance())
			{
				b.repair();
				float cost = b.getSurfaceArea() * 0.3f;
				city.buy(cost);
			}
		}
	}

	@Override
	public void renderUnit(Graphics gfx)
	{
		renderDefault(gfx, Content.sprites.unitArchitect);
	}

}


