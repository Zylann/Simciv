package simciv.ui.base;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.SlickException;

public class MenuBar extends WidgetContainer
{
	private List<MenuBarButton> buttons;
	
	public MenuBar(Widget parent, int x, int y)
	{
		super(parent, x, y, 0, 0);
		buttons = new ArrayList<MenuBarButton>();
	}
	
	@Override
	public void add(Widget child) throws SlickException
	{
		throw new SlickException("UI: only MenuBarButton objects can be added to a MenuBar.");
	}

	@Override
	public boolean mousePressed(int button, int x, int y)
	{
		if(!super.mousePressed(button, x, y))
		{
			unpressButtons();
			return false;
		}
		return true;
	}
	
	public void unpressButtons()
	{
		for(MenuBarButton b : buttons)
			b.press(false);
	}

	public void unpressButtons(MenuBarButton except)
	{
		for(MenuBarButton b : buttons)
		{
			if(b != except)
				b.press(false);
		}
	}

	public void add(MenuBarButton b, Menu m) throws SlickException
	{
		b.setMenu(m);
		if(m != null)
		{
			m.setVisible(false);
			m.addActionListener(new ItemSelectListener());
		}
		
		if(children.isEmpty())
			setSize(b.getWidth(), b.getHeight());
		else
		{
			Widget last = buttons.get(buttons.size() - 1);
			b.setPosition(last.getX() + last.getWidth(), 0);
			setSize(getWidth() + b.getWidth(), getHeight());
		}
		
		super.add(b);
		if(m != null)
			super.add(m);
		buttons.add(b);
	}
	
	class ItemSelectListener implements IActionListener
	{
		@Override
		public void actionPerformed(Widget sender) {
			unpressButtons();
		}	
	}

}



