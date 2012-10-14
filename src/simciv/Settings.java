package simciv;

import java.awt.Dimension;

import org.newdawn.slick.util.Log;

/**
 * Game settings
 * @author Marc
 *
 */
public class Settings
{
	public static final String SETTINGS_FILE_PATH = "settings.xml";
	
	private int screenWidth;
	private int screenHeight;
	private int targetFramerate;
	private boolean useVSync;
	private boolean smoothDeltas;
	private boolean fancyUnitMovements;
	
	// Note : don't forget tu update the settings.xml file after modifying attributes
	
	public Settings()
	{
		// Note : these values are just set to construct a compliant Settings object.
		// they are not used by the game (see settings.xml file).
		targetFramerate = 30;
		screenWidth = 800;
		screenHeight = 600;
		useVSync = true;
		smoothDeltas = true;
		fancyUnitMovements = true;
	}
	
	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public boolean isFancyUnitMovements() {
		return fancyUnitMovements;
	}

	public void setFancyUnitMovements(boolean fancyUnitMovements) {
		this.fancyUnitMovements = fancyUnitMovements;
	}

	public int getTargetFramerate() {
		return targetFramerate;
	}

	public void setTargetFramerate(int targetFramerate) {
		this.targetFramerate = targetFramerate;
	}

	public boolean isUseVSync() {
		return useVSync;
	}

	public void setUseVSync(boolean useVSync) {
		this.useVSync = useVSync;
	}

	public boolean isSmoothDeltas() {
		return smoothDeltas;
	}

	public void setSmoothDeltas(boolean smoothDeltasEnabled) {
		this.smoothDeltas = smoothDeltasEnabled;
	}
	
	public void check()
	{
		Log.info("Check settings");
		
		// Check screen size
		
		Dimension desktopSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int dw = (int)desktopSize.getWidth();
		int dh = (int)desktopSize.getHeight();
		Log.debug("Desktop size : " + desktopSize);
		
		if(screenWidth > dw) 
		{
			Log.warn("Choosen screen width is to high. Max is " + dw + ".");
			screenWidth = dw;
		}
		if(screenHeight > dh) 
		{
			Log.warn("Choosen screen height is to high. Max is " + dh + ".");
			screenHeight = dh;
		}

		// Check target framerate
		
		if(targetFramerate < 25)
		{
			Log.warn("Choosen framerate is too low (" 
					+ targetFramerate + "), " + "will be set to 25.");
			targetFramerate = 25;
		}

	}
	
}


