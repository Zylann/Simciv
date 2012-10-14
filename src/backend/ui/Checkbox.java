package backend.ui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class Checkbox extends Button
{
	private static final int WIDTH = 16;
	private static final int HEIGHT = 16;
	
	private boolean checked;
	
	public Checkbox(Widget parent, int x, int y)
	{
		super(parent, x, y, WIDTH, HEIGHT);
	}
	
	public void setChecked(boolean check)
	{
		checked = check;
	}
	
	public boolean isChecked()
	{
		return checked;
	}

	@Override
	protected void onPress()
	{
		checked = !checked;
	}

	@Override
	protected void onRelease()
	{
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		UIRenderer.getTheme().renderCheckBox(gfx, this);
	}

}

