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
import simciv.movements.RandomRoadMovement;

public class Taxman extends Citizen
{
	private static SpriteSheet unitSprites;
	
	public Taxman(Map m, Workplace workplace)
	{
		super(m, workplace);
		setMovement(new RandomRoadMovement());
		
		if(unitSprites == null)
			unitSprites = new SpriteSheet(Content.images.unitTaxman, Game.tilesSize, Game.tilesSize);
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
	public byte getJobID()
	{
		return Job.TAXMAN;
	}

	@Override
	public void renderUnit(Graphics gfx)
	{
		defaultRender(gfx, unitSprites);
	}

}



