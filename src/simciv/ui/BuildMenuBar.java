package simciv.ui;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import simciv.CityBuilder;
import simciv.ui.base.IActionListener;
import simciv.ui.base.MenuBar;
import simciv.ui.base.MenuBarButton;
import simciv.ui.base.Widget;

public class BuildMenuBar extends MenuBar
{
	public CityBuilder cityBuilderRef;
	
	public BuildMenuBar(Widget parent, int x, int y)
	{
		super(parent, x, y);
	}

	public void addCategory(Image icon, String label, BuildMenu menu) throws SlickException
	{
		add(new MenuBarButton(this, 24, 24, icon), menu);
	}
	
	public void addMode(Image icon, String label, int mode) throws SlickException
	{
		MenuBarButton b = new MenuBarButton(this, 24, 24, icon);
		b.addActionListener(new SelectModeAction(mode));
		add(b, null);
	}
	
	class SelectModeAction implements IActionListener
	{
		int mode;
		public SelectModeAction(int m) {
			mode = m;
		}
		@Override
		public void actionPerformed(Widget sender) {
			if(cityBuilderRef != null)
				cityBuilderRef.setMode(mode);
		}
	}
	
}


