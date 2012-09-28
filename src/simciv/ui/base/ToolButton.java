package simciv.ui.base;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import simciv.content.Content;

/**
 * A ToolButton remains pressed after the first click.
 * If the ToolButton is part of a group, it is unpressed if another is pressed.
 * @author Marc
 *
 */
public class ToolButton extends Button
{
	private static final int SIZE = 24;
	
	private ToolButtonGroup group;
	public Image icon;
	
	public ToolButton(Widget parent, int x, int y, ToolButtonGroup group)
	{
		super(parent, x, y, SIZE, SIZE);
		this.group = group;
	}
	
	public ToolButton(Widget parent, ToolButtonGroup group)
	{
		this(parent, 0, 0, group);
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
	
	@Override
	protected void onPress()
	{
		Content.sounds.uiClick.play(1.f, 0.5f);
		if(group != null)
			group.unselectAllExcept(this);
		onAction();
	}

	@Override
	protected void onRelease()
	{
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		UIRenderer.instance().renderToolButton(gfx, this);
	}

}
