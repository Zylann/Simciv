package simciv.ui.base;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import simciv.content.Content;

public class MenuItem extends Button
{
	// All menu items have a common height
	public static final int HEIGHT = 16;
	
	private IActionListener actionListener;
	private String text;
	
	public MenuItem(WidgetContainer parent, String text) throws SlickException
	{
		super(parent, 0, 0, 0, HEIGHT);
		if(!Menu.class.isInstance(parent))
			throw new SlickException("The parent of a MenuItem must be a Menu.");
		this.text = text;
	}
	
	public String getText()
	{
		return text;
	}

	@Override
	protected void onPress()
	{
		Content.sounds.uiButtonPress.play(1.f, 0.5f);
	}

	@Override
	protected void onRelease()
	{
		if(actionListener != null)
			actionListener.actionPerformed();
		parent.setVisible(false);
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		UIRenderer.instance().renderMenuItem(gfx, this);
	}

	public void setActionListener(IActionListener actionListener)
	{
		this.actionListener = actionListener;
	}

}
