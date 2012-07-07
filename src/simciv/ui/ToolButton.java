package simciv.ui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;

import simciv.ContentManager;

/**
 * A ToolButton remains pressed after the first click.
 * If the ToolButton is part of a group, it is unpressed if another is pressed.
 * @author Marc
 *
 */
public class ToolButton extends Button
{
	private static Sound pressSound;
	private static final int SIZE = 24;
	
	private IActionListener actionListener;
	private ToolButtonGroup group;
	public Image icon;
	
	public ToolButton(WidgetContainer parent, int x, int y, ToolButtonGroup group)
	{
		super(parent, x, y, SIZE, SIZE);
		if(pressSound == null)
			pressSound = ContentManager.instance().getSound("ui.click");
		this.group = group;
	}
		
	public void select(boolean s)
	{
		if(s)
		{
			if(group != null)
				group.unselectAllExcept(this);
			press(true);
		}
		else
			press(false);
	}
	
	@Override
	public boolean mouseReleased(int button, int x, int y)
	{
		// The button remains pressed
		return false;
	}

	/**
	 * Sets the action executed on button press
	 * @param callback
	 */
	public void setActionListener(IActionListener listener)
	{
		this.actionListener = listener;
	}
	
	@Override
	protected void onPress()
	{
		pressSound.play(1.f, 0.5f);
		if(group != null)
			group.unselectAllExcept(this);
		if(actionListener != null)
			actionListener.actionPerformed();
	}

	@Override
	protected void onRelease()
	{
	}

	@Override
	public void render(Graphics gfx)
	{
		UIRenderer.instance().renderToolButton(gfx, this);
	}

}
