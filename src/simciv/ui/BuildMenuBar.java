package simciv.ui;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import backend.ui.IActionListener;
import backend.ui.MenuBar;
import backend.ui.MenuBarButton;
import backend.ui.Widget;

import simciv.CityBuilder;

/**
 * The game BuildMenuBar can hold mode setters, builds setters or categories with more builds in it.
 * @author Marc
 *
 */
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
	
	public void addBuild(Image icon, String label, String buildStr) throws SlickException
	{
		MenuBarButton b = new MenuBarButton(this, 24, 24, icon);
		b.addActionListener(new SelectBuildAction(buildStr));
		add(b, null);
	}
	
	class SelectBuildAction implements IActionListener
	{
		String buildString;
		public SelectBuildAction(String buildStr) {
			this.buildString = buildStr;
		}
		@Override
		public void actionPerformed(Widget sender) {
			if(cityBuilderRef != null) {
				cityBuilderRef.setMode(CityBuilder.MODE_BUILDS);
				cityBuilderRef.setBuildString(buildString);
			}
		}		
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


