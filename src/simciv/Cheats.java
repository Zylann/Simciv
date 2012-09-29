package simciv;

/**
 * Well... originally, this was for debug :D
 * This is a set of flags that make things happen quicker.
 * @author Marc
 *
 */
public class Cheats
{
	/** The cheat codes below will be active only if enabled is true. **/
	private static boolean enabled = false;
	
	/** Increases house construction speed **/
	private static boolean fastCitizenProduction = false;
	
	/** Increases farmland crops growing speed **/
	private static boolean fastFarmlandGrow = true;
	
	/** Money will not drop by being used **/
	private static boolean infiniteMoney = false;
	
	/** Allows erasing un-eraseable builds (like fires) **/
	private static boolean superEraser = true;
	
	/** The erase tool will also set builds on fire **/
	private static boolean burnOnErase = false;
	
	/**
	 * Method called when cheats are requested from the ingame command prompt
	 * (Not implemented yet).
	 * @param cmd : cheat command
	 */
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
			else if(cmd == "bulldozer")
				superEraser = !superEraser;
			else if(cmd == "charmander")
				burnOnErase = !burnOnErase;
		}
		else if(cmd == "icheat")
		{
			enabled = true;
		}
	}
	
	public static boolean isEnabled() {
		return enabled;
	}

	public static boolean isFastCitizenProduction() {
		return enabled && fastCitizenProduction;
	}

	public static boolean isFastFarmlandGrow() {
		return enabled && fastFarmlandGrow;
	}

	public static boolean isInfiniteMoney() {
		return enabled && infiniteMoney;
	}
	
	public static boolean isSuperEraser() {
		return enabled && superEraser;
	}
	
	public static boolean isBurnOnErase() {
		return enabled && burnOnErase;
	}

}


