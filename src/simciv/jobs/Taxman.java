package simciv.jobs;

import java.util.List;

import org.newdawn.slick.Graphics;

import simciv.buildings.Building;
import simciv.buildings.House;
import simciv.buildings.Workplace;
import simciv.content.Content;
import simciv.effects.RisingIcon;
import simciv.units.Citizen;

public class Taxman extends Job
{
	public Taxman(Citizen citizen, Workplace workplace)
	{
		super(citizen, workplace);
	}

	@Override
	public void tick()
	{
		float totalMoneyCollected = 0;
		List<Building> builds = me.getWorld().getBuildingsAround(me.getX(), me.getY());
		for(Building b : builds)
		{
			if(b.isHouse())
			{
				float moneyCollected = ((House)b).payTaxes();
				if(moneyCollected > 0)
					me.getWorld().addGraphicalEffect(new RisingIcon(b.getX(), b.getY(), Content.images.effectGold));
				totalMoneyCollected += moneyCollected;
			}
		}
		me.getWorld().playerCity.gainMoney((int) totalMoneyCollected);
	}

	@Override
	public void onBegin()
	{
		me.enterBuilding(workplaceRef);
	}

	@Override
	public byte getID()
	{
		return Job.TAXMAN;
	}

	@Override
	public void renderUnit(Graphics gfx)
	{
		me.defaultRender(gfx, Content.images.unitTaxman);
	}

	@Override
	public int getIncome()
	{
		return 16;
	}

	@Override
	public int getTickTimeOverride()
	{
		return 200; // Taxmen are a bit slower
	}

}
