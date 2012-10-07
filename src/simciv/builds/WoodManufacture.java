package simciv.builds;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Game;
import simciv.Map;
import simciv.content.Content;

public class WoodManufacture extends Workplace
{
	private static final long serialVersionUID = 1L;
	
	private static BuildProperties properties;
	
	// States
	private static final byte FIND_LOGS = 0;
	private static final byte PRODUCE = 0;
	
	static
	{
		properties = new BuildProperties("Wood manufacture");
		properties.setCategory(BuildCategory.INDUSTRY).setCost(250)
			.setSize(3, 3, 1).setUnitsCapacity(12);
	}

	public WoodManufacture(Map m)
	{
		super(m);
	}

	@Override
	public int getProductionProgress()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void onActivityStart()
	{
		setState(FIND_LOGS);
	}

	@Override
	protected void onActivityStop()
	{
		removeAllUnits();
	}

	@Override
	protected void tickActivity()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public BuildProperties getProperties()
	{
		return properties;
	}

	@Override
	protected void renderBuild(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		SpriteSheet sprites = Content.sprites.buildWoodManufacture;
		if(isActive())
		{
			if(getState() == PRODUCE)
				gfx.drawImage(sprites.getSprite(2, 0), 0, -Game.tilesSize);
			else
				gfx.drawImage(sprites.getSprite(1, 0), 0, -Game.tilesSize);
		}
		else
			gfx.drawImage(sprites.getSprite(0, 0), 0, -Game.tilesSize);
	}

}
