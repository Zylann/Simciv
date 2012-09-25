package simciv.units;

import java.util.List;

import org.newdawn.slick.Graphics;
import simciv.Map;
import simciv.builds.Build;
import simciv.builds.House;
import simciv.builds.Workplace;
import simciv.content.Content;
import simciv.movement.RandomRoadMovement;

public class Taxman extends Citizen
{
	private static final long serialVersionUID = 1L;
	
	public Taxman(Map m, Workplace workplace)
	{
		super(m, workplace);
		setMovement(new RandomRoadMovement());
	}

	@Override
	public void tick()
	{
		float totalMoneyCollected = 0;
		List<Build> builds = mapRef.getBuildsAround(getX(), getY());
		for(Build b : builds)
		{
			if(b.isHouse())
			{
				House h = (House)b;
				if(!h.isBeenTaxed())
				{
					float moneyCollected = h.payTaxes();
					totalMoneyCollected += moneyCollected;
				}
			}
		}
		mapRef.playerCity.gainMoney((int) totalMoneyCollected);
	}

	@Override
	public void renderUnit(Graphics gfx)
	{
		defaultRender(gfx, Content.sprites.unitTaxman);
	}

}



