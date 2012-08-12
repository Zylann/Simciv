package simciv.ui;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import simciv.CityBuilder;
import simciv.World;
import simciv.buildings.Building;
import simciv.buildings.BuildingFactory;
import simciv.ui.base.Menu;
import simciv.ui.base.MenuItem;
import simciv.ui.base.UIRenderer;

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
			updateInfos(parentBuildMenuBar.cityBuilderRef.getWorld());
	}

	public void updateInfos(World worldRef)
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
					.setBuildingString(buildString);
			}
		}

		public void updateInfos(World worldRef)
		{
			// Name
			Building b = BuildingFactory.createFromName(buildString, worldRef);
			setText(b.getProperties().name);
			
			// Cost
			int cost = b.getProperties().cost;
			if(worldRef.playerCity.getMoney() < cost)
				costColor = Color.red;
			else
				costColor = Color.black;
			costString = "" + cost;
			costStringWidth = UIRenderer.instance().getFont().getWidth(costString);
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


