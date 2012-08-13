package simciv.ui.base;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import simciv.content.Content;

public class MenuBarButton extends Button
{
	private MenuBar parentMenuBar;
	private Menu menu;
	private Label label;
	
	public MenuBarButton(MenuBar parent, int w, int h, Image icon)
	{
		super(parent, 0, 0, w, h);
		label = new Label(this, 0, 0, icon);
		label.setAlign(Widget.ALIGN_CENTER);
		parentMenuBar = parent;
	}
	
	public MenuBarButton(MenuBar parent, int w, int h, String text)
	{
		super(parent, 0, 0, w, h);
		label = new Label(this, 0, 0, text);
		label.setAlign(Widget.ALIGN_CENTER);
		parentMenuBar = parent;
	}
	
	public void setMenu(Menu m)
	{
		menu = m;
		if(menu != null)
		{
			if(m.getParent() == this)
				menu.setPosition(0, getHeight());
		}
	}

	public Menu getMenu()
	{
		return menu;
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
		if(menu != null)
			menu.setVisible(true);
		parentMenuBar.unpressButtons(this);
		Content.sounds.uiClick.play(1.f, 0.5f);
		onAction();
	}
	
	@Override
	protected void onRelease()
	{
		if(menu != null)
			menu.setVisible(false);
	}

	public Label getLabel()
	{
		return label;
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		UIRenderer.instance().renderMenuBarButton(gfx, this);
	}

}


