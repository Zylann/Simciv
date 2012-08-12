package simciv.ui.base;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import simciv.content.Content;

/**
 * Menu element. Clickable button.
 * @author Marc
 *
 */
public class MenuItem extends Button
{
	// All menu items have a common height
	public static final int HEIGHT = 16;
	
	private String text;
	private Menu parentMenu;
	
	public MenuItem(Menu parent, String text) throws SlickException
	{
		super(parent, 0, 0, 0, HEIGHT);
		parentMenu = parent;
		this.text = text;
	}
	
	public void setText(String txt)
	{
		text = txt;
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
		onAction();
		parentMenu.onItemSelect(this);
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		UIRenderer.instance().renderMenuItem(gfx, this);
	}

}
