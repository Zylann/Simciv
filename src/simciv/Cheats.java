package simciv;

/**
 * Well...
 * @author Marc
 *
 */
public class Cheats
{
	private static boolean enabled = true;
	private static boolean fastCitizenProduction = false;
	private static boolean fastFarmlandGrow = false;
	
	public static void onCommand(String cmd)
	{
		if(enabled)
		{
			if(cmd == "idontcheat" || cmd == "icheat")
				enabled = false;
			else if(cmd == "ogm")
				fastFarmlandGrow = !fastFarmlandGrow;
			else if(cmd == "reproduce")
				fastCitizenProduction = !fastCitizenProduction;
		}
		else if(cmd == "icheat")
		{
			enabled = true;
		}
	}
	
	public static boolean isEnabled()
	{
		return enabled;
	}

	public static boolean isFastCitizenProduction()
	{
		return enabled && fastCitizenProduction;
	}

	public static boolean isFastFarmlandGrow()
	{
		return enabled && fastFarmlandGrow;
	}

}


