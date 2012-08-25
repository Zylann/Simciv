package simciv.units;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

import simciv.Game;
import simciv.Map;
import simciv.content.Content;
import simciv.movements.RandomMovement;

public class Nomad extends Unit
{
	private static SpriteSheet sprites;
	
	public Nomad(Map m)
	{
		super(m);
		setMovement(new RandomMovement());
		
		if(sprites == null)
			sprites = new SpriteSheet(Content.images.unitNomad, Game.tilesSize, Game.tilesSize);
	}
	
	@Override
	public void renderUnit(Graphics gfx)
	{
		defaultRender(gfx, sprites);
	}

	@Override
	public void tick()
	{
		// Nomads can disappear after 2min
		if(getLifeTime() > 120000)
		{
			if(Math.random() < 0.1)
				dispose();
		}
	}

	@Override
	public void onDestruction()
	{
	}

	@Override
	protected int getTickTime()
	{
		return 700;
	}
	
}


