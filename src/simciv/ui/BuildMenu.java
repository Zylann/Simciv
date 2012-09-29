package simciv.ui;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

import backend.ui.Menu;
import backend.ui.MenuItem;
import backend.ui.UIRenderer;

import simciv.CityBuilder;
import simciv.Map;
import simciv.builds.Build;
import simciv.builds.BuildFactory;

/**
 * List of builds.
 * Each item displays the name of the build, its cost,
 * and changes the current build of the cityBuilder on click (if allowed).
 * @author Marc
 *
 */
public class BuildMenu extends Menu
{
	private List<BuildMenuItem> items;
	private BuildMenuBar parentBuildMenuBar;
	
	public BuildMenu(BuildMenuBar parent, int x, int y, int width)
	{
		super(parent, x, y, width);
		items = new ArrayList<BuildMenuItem>();
		parentBuildMenuBar = parent;
	}
	
	public void addBuild(String buildString) throws SlickException
	{
		BuildMenuItem item = new BuildMenuItem(this, buildString);
		this.add(item, null);
		items.add(item);
	}
	
	@Override
	protected void onShow()
	{
		if(parentBuildMenuBar.cityBuilderRef != null)
		{
			try {
				updateInfos(parentBuildMenuBar.cityBuilderRef.getMap());
			} catch (SlickException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateInfos(Map worldRef) throws SlickException
	{
		for(BuildMenuItem item : items)
			item.updateInfos(worldRef);
	}
	
	class BuildMenuItem extends MenuItem
	{
		private String buildString;
		private String costString;
		private Color costColor;
		private int costStringWidth;
		
		public BuildMenuItem(BuildMenu parent, String buildString)
				throws SlickException
		{
			super(parent, "<" + buildString + ">");
			costString = "---";
			this.buildString = buildString;
			costColor = Color.black;
		}
		
		@Override
		protected void onAction()
		{
			super.onAction();
			if(parentBuildMenuBar.cityBuilderRef != null)
			{
				parentBuildMenuBar.cityBuilderRef
					.setMode(CityBuilder.MODE_BUILDS)
					.setBuildString(buildString);
			}
		}

		public void updateInfos(Map worldRef) throws SlickException
		{
			// Name
			Build b = BuildFactory.createFromName(buildString, worldRef);
			setText(b.getProperties().name);
			
			// Cost
			int cost = b.getProperties().cost;
			if(worldRef.playerCity.getMoney() < cost)
				costColor = Color.red;
			else
				costColor = Color.black;
			costString = "" + cost;
			
			Font font = UIRenderer.getFont();
			if(font != null)
				costStringWidth = UIRenderer.getFont().getWidth(costString);
			else
				Log.error("BuildMenu:updateInfos(): no font defined");
		}

		@Override
		public void render(GameContainer gc, Graphics gfx)
		{
			super.render(gc, gfx);
			gfx.setColor(costColor);
			int y = getAbsoluteY() + 2;
			if(isPressed())
				y += 1;
			gfx.drawString(costString, getWidth() - costStringWidth, y);
		}
	}
	
}


