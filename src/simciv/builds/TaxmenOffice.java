package simciv.builds;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import backend.ui.Notification;

import simciv.Map;
import simciv.content.Content;
import simciv.units.Jobs;

public class TaxmenOffice extends PassiveWorkplace
{
	private static final long serialVersionUID = 1L;
	
	private static BuildProperties properties;
	
	/** Total money gained by this taxmen office **/
	private float taxMoney;
	
	static
	{
		properties = new BuildProperties("Taxmen office");
		properties.setCost(200).setSize(2, 2, 1).setUnitsCapacity(6).setCategory(BuildCategory.ADMINISTRATION);
	}
	
	public TaxmenOffice(Map m)
	{
		super(m);
	}
	
	@Override
	protected void onActivityStart()
	{
		addAndSpawnUnitsAround(Jobs.TAXMAN, 2);
	}
	
	/**
	 * Updates money contained in the vault.
	 * Should only be called by taxmen when they gain tax money.
	 * @param m : amount of money
	 */
	public void onTaxmanReceivedMoney(float m)
	{
		taxMoney += m;
	}
	
	/**
	 * Reduces the money stored in the office AND money owned by the city.
	 * Sends a "robbed !" notification to the player.
	 * @param wantedMoney
	 * @return
	 */
	public float robMoney(float wantedMoney)
	{
		if(wantedMoney > taxMoney)
		{
			wantedMoney = taxMoney;
			taxMoney = 0;
		}
		else
			taxMoney -= wantedMoney;
		
		mapRef.playerCity.buy(wantedMoney);
		
		mapRef.sendNotification(Notification.TYPE_WARNING, "The taxmen office has been robbed !");
		
		return wantedMoney;
	}
	
	@Override
	public BuildReport getReport()
	{
		BuildReport report = super.getReport();
		
		if(getMoneyInVault() <= 0.f && mapRef.playerCity.getMoney() <= 0)
			report.add(BuildReport.PROBLEM_MAJOR, "Our vault is empty, because the city has no money !");
		else
			report.add(BuildReport.INFO, "Our vault contains " + (int)taxMoney + " coins.");
		
		return report;
	}

	@Override
	protected void onActivityStop()
	{
		removeAllUnits();
	}
	
	@Override
	protected void tickActivity()
	{
		if(taxMoney > mapRef.playerCity.getMoney())
		{
			if(mapRef.playerCity.getMoney() > 0)
				taxMoney = mapRef.playerCity.getMoney();
			else
				taxMoney = 0;
		}
	}

	@Override
	public void renderBuild(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		renderDefault(gfx, Content.sprites.buildTaxmenOffice);
	}

	@Override
	public BuildProperties getProperties()
	{
		return properties;
	}

	public float getMoneyInVault()
	{
		return taxMoney;
	}

}
