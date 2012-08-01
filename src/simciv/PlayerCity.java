package simciv;

/**
 * Global Informations about player's city
 * @author Marc
 *
 */
public class PlayerCity
{
	protected float money;
	protected float incomeTaxRatio;
	
	public PlayerCity()
	{
		money = 3000;
		incomeTaxRatio = 0.09f; // 9 %
	}
	
	public float getMoney()
	{
		return money;
	}
		
	public void setMoney(float m)
	{
		money = m;
	}
	
	public void buy(float cost)
	{
		if(!Cheats.isInfiniteMoney())
			money -= cost;
	}
	
	public float getIncomeTaxRatio()
	{
		return incomeTaxRatio;
	}
	
	public void setIncomeTaxRatio(float r)
	{
		if(r > 1)
			incomeTaxRatio = 1;
		else if(r < 0)
			incomeTaxRatio = 0;
		else
			incomeTaxRatio = r;
	}

	public void gainMoney(float amount)
	{
		money += amount;
	}

}



