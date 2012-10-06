package simciv.units;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.Log;

import backend.MathHelper;

import simciv.Map;
import simciv.content.Content;
import simciv.movement.RandomMovement;

public class Duck extends Animal
{
	private static final long serialVersionUID = 1L;
	private static final int SPACE_NEEDED_TO_LIVE = 16;
	
	public Duck(Map m)
	{
		super(m);
		setMovement(new RandomMovement());		
	}

	@Override
	protected void renderUnit(Graphics gfx)
	{
		renderDefault(gfx, Content.sprites.unitDuck);
	}

	@Override
	public void tick()
	{
		if(getTicks() % 16 == 0)
		{
			int d = evaluateWalkableDistance(SPACE_NEEDED_TO_LIVE);
			if(d < SPACE_NEEDED_TO_LIVE)
			{
				Log.debug(this + " can't live in spawn space (" + d + ")");
				dispose();
			}
		}
		
		if(mapRef.getFaunaCount() < 100 && Math.random() < 0.001f)
		{
			Log.debug(this + " breeds");
			breed();
		}
	}
	
	@Override
	protected void playDeathSound()
	{
		Content.sounds.unitDuckDeath.play(1.f + MathHelper.randS(0.1f), 0.2f);
	}

	@Override
	public void onDestruction()
	{
	}

	@Override
	public String getDisplayableName()
	{
		return "Duck";
	}

	@Override
	protected void breed()
	{
		mapRef.spawnUnit(new Duck(mapRef), getX(), getY());
	}

}


