package simciv.builds;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Map;
import simciv.content.Content;

/**
 * Debris are left by destroyed builds.
 * @author Marc
 *
 */
public class Ruins extends Build
{
	private static final long serialVersionUID = 1L;

	private static BuildProperties properties;

	private String infoString;
	
	static
	{
		properties = new BuildProperties("Ruins");
		properties.setCost(0).setSize(1, 1, 0)
			.setUnitsCapacity(0)
			.setCategory(BuildCategory.RUINS)
			.setFlamable(false);
	}
	
	/**
	 * Constructs anonymous ruins.
	 * @param m : parent map
	 * @param burning : if true, the ruins will be in flames.
	 */
	public Ruins(Map m, boolean burning)
	{
		super(m);
		infoString = "Ruins";
		
		if(burning)
			fireLevel = Build.FIRE_BURN;
	}
	
	/**
	 * Sets informations about the build that collapsed
	 * @param b : the build that created the ruins
	 */
	public void setPropertiesFromBuild(Build b)
	{
		infoString = "Ruins of " + b.getProperties().name.toLowerCase();
	}

	@Override
	public BuildProperties getProperties()
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
	protected void renderBuild(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		gfx.drawImage(Content.sprites.buildDebris, 0, 0);
	}

	@Override
	public void tick()
	{
	}

	@Override
	public void onDestruction()
	{
	}

	public boolean isWalkable()
	{
		return !isFireBurning();
	}
	
}



