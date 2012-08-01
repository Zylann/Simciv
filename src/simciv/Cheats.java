package simciv;

/**
 * Well... originally, this was for debug :D
 * This is a set of flags that make things happen quicker.
 * @author Marc
 *
 */
public class Cheats
{
	private static boolean enabled = false;
	private static boolean fastCitizenProduction = true;
	private static boolean fastFarmlandGrow = false;
	private static boolean infiniteMoney = false;
	private static boolean fastTime = true;
	
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
			else if(cmd == "eldorado")
				infiniteMoney = !infiniteMoney;
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

	public static boolean isInfiniteMoney()
	{
		return enabled && infiniteMoney;
	}

	public static boolean isFastTime()
	{
		return enabled && fastTime;
	}

}


