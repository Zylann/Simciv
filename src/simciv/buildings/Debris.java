package simciv.buildings;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import simciv.World;
import simciv.content.Content;

/**
 * Debris are left by destroyed builds.
 * @author Marc
 *
 */
public class Debris extends Building
{
	private static BuildingProperties properties;

	private String infoString;
	
	static
	{
		properties = new BuildingProperties("Ruins");
		properties.setCost(0).setSize(1, 1, 0).setUnitsCapacity(0);
	}
	
	public Debris(World w)
	{
		super(w);
		infoString = "Ruins";
	}
	
	public void setPropertiesFromBuild(Building b)
	{
		infoString = "Ruins of " + b.getProperties().name.toLowerCase();
	}

	@Override
	public BuildingProperties getProperties()
	{
		return properties;
	}

	@Override
	public boolean onMaintenance()
	{
		return false;
	}

	@Override
	protected void tickSolidness()
	{
		// No solidness
	}

	@Override
	public String getInfoString()
	{
		return infoString;
	}

	@Override
	protected void renderBuilding(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		gfx.drawImage(Content.images.buildDebris, 0, 0);
	}

	@Override
	protected void tick()
	{
	}

	@Override
	public void onDestruction()
	{
	}

}



