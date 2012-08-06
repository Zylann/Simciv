package simciv.ui.base;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class WindowCloseButton extends Button
{
	public static final int width = 24;
	public static final int height = 16;
	
	IActionListener action;
	
	public WindowCloseButton(Window parent)
	{
		super(parent, parent.width - width, 0, width, height);
	}

	public void setActionListener(IActionListener action)
	{
		this.action = action;
	}

	@Override
	protected void onPress()
	{
	}
	
	@Override
	protected void onRelease()
	{
		((Window)parent).setVisible(false);
		if(!parent.isVisible() && action != null)
			action.actionPerformed();
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		UIRenderer.instance().renderWindowCloseButton(gfx, this);
	}

}
