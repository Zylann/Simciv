package simciv;

/**
 * Global Informations about player's city
 * @author Marc
 *
 */
public class PlayerCity
{
	protected int money;
//	protected float incomeTaxRatio;
	
	public PlayerCity()
	{
		money = 3000;
	}
	
	public int getMoney()
	{
		return money;
	}
		
	public void setMoney(int m)
	{
		money = m;
	}
	
	public void buy(int cost)
	{
		if(!Cheats.isInfiniteMoney())
			money -= cost;
	}
	
//	public float getIncomeTaxRatio()
//	{
//		return incomeTaxRatio;
//	}
//	
//	public void setIncomeTaxRatio(float r)
//	{
//		if(r > 1)
//			incomeTaxRatio = 1;
//		else if(r < 0)
//			incomeTaxRatio = 0;
//		else
//			incomeTaxRatio = r;
//	}

}



