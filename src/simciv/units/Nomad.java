package simciv.units;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

import simciv.Game;
import simciv.World;
import simciv.content.Content;
import simciv.movements.RandomMovement;

public class Nomad extends Unit
{
	private static SpriteSheet sprites;
	
	public Nomad(World w)
	{
		super(w);
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


