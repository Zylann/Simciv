package simciv;

/**
 * Game settings
 * @author Marc
 *
 */
public class Settings
{
	// TODO serialize to XML
	// Video settings
	private int screenWidth;
	private int screenHeight;
	private int targetFramerate;
	private boolean useVSync;
	private boolean smoothDeltasEnabled;
	private boolean renderFancyUnitMovements;
	
	public Settings()
	{
		targetFramerate = 30;
		screenWidth = Game.defaultScreenHeight;
		screenHeight = Game.defaultScreenHeight;
		useVSync = true;
		smoothDeltasEnabled = true;
		renderFancyUnitMovements = true;
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

	public boolean isRenderFancyUnitMovements() {
		return renderFancyUnitMovements;
	}

	public void setRenderFancyUnitMovements(boolean renderFancyUnitMovements) {
		this.renderFancyUnitMovements = renderFancyUnitMovements;
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

	public boolean isSmoothDeltasEnabled() {
		return smoothDeltasEnabled;
	}

	public void setSmoothDeltasEnabled(boolean smoothDeltasEnabled) {
		this.smoothDeltasEnabled = smoothDeltasEnabled;
	}

}


