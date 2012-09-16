package simciv.ui.base;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class WindowCloseButton extends Button
{
	public static final int width = 24;
	public static final int height = 16;
	
	public WindowCloseButton(Window parent)
	{
		super(parent, parent.width - width, 0, width, height);
		addActionListener(new CloseAction());
	}

	@Override
	protected void onPress()
	{
	}
	
	@Override
	protected void onRelease()
	{
		onAction();
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		UIRenderer.instance().renderWindowCloseButton(gfx, this);
	}
	
	class CloseAction implements IActionListener
	{
		@Override
		public void actionPerformed(Widget sender) {
			parent.setVisible(false);
		}		
	}

}
