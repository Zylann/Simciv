package simciv;

/**
 * Game settings
 * @author Marc
 *
 */
public class Settings
{
	public int framerate;
	public int screenWidth;
	public int screenHeight;
	public boolean useVSync;
	public boolean smoothDeltasEnabled;
	public boolean renderFancyUnitMovements;
	
	public Settings()
	{
		framerate = 30;
		screenWidth = Game.defaultScreenHeight;
		screenHeight = Game.defaultScreenHeight;
		useVSync = true;
		smoothDeltasEnabled = true;
		renderFancyUnitMovements = true;
	}
	
}


