package simciv.ui.base;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class ProgressBar extends BasicWidget
{
	public static final int height = 8;

	private float progress; // between 0 and 1
	
	public ProgressBar(WidgetContainer parent, int x, int y, int width)
	{
		super(parent, x, y, width, height);
		progress = 0;
	}
	
	/**
	 * Set progress from a [0, 100] integer
	 * @param p
	 */
	public void setProgress(int p)
	{
		if(p > 100)
			p = 100;
		else if(p < 0)
			p = 0;
		progress = p / 100.f;
	}
	
	/**
	 * Sets progress from a [0, 1] floating number
	 * @param p
	 */
	public void setProgressRatio(float p)
	{
		if(p < 0)
			progress = 0;
		else if(p > 1)
			progress = 1;
		else
			progress = p;
	}
	
	/**
	 * Get progress ratio
	 * @return ratio between 0 and 1
	 */
	public float getProgressRatio()
	{
		return progress;
	}
	
	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		UIRenderer.instance().renderProgressBar(gfx, this);
	}

}



