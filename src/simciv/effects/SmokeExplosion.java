package simciv.effects;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import simciv.Game;
import simciv.content.Content;

/**
 * Displays a smoke explosion
 * @author Marc
 *
 */
public class SmokeExplosion extends VisualEffect
{
	private static SpriteSheet sprites;
	
	private SmokeParticle particles[];
	private boolean finished;
	
	/**
	 * Creates a smoke explosion at (x,y).
	 * @param x : x position in cells
	 * @param y : y position in cells
	 * @param nbParticles : amount of particles to produce
	 * @param meanDuration : mean duration of particles
	 */
	public SmokeExplosion(int x0, int y0, int nbParticles, float meanDuration, int excentering)
	{
		super(x0, y0);
		particles = new SmokeParticle[nbParticles];
		finished = false;
		
		if(sprites == null)
			sprites = new SpriteSheet(Content.images.effectSmoke, Game.tilesSize/2, Game.tilesSize/2);

		// Generate particles
		float durationVariation = meanDuration / 4;
		for(int i = 0; i < particles.length; i++)
		{
			float lifeTime = (int) (meanDuration + durationVariation * (2.0 * Math.random() - 0.5));
			int x = (int) ((2.0 * Math.random() - 0.5) * excentering);
			int y = (int) ((2.0 * Math.random() - 0.5) * excentering);
			particles[i] = new SmokeParticle(x, y, lifeTime);
		}
	}
	
	public void update(int deltaMs)
	{
		float delta = deltaMs / 1000.f;
		finished = true;
		for(int i = 0; i < particles.length; i++)
		{
			if(particles[i] != null)
			{
				finished = false;
				particles[i].update(delta);
				if(particles[i].isDead())
					particles[i] = null;
			}
		}
	}

	public void renderEffect(Graphics gfx)
	{
		for(int i = 0; i < particles.length; i++)
		{
			if(particles[i] != null)
				particles[i].render(gfx);
		}
	}
	
	@Override
	public boolean isFinished()
	{
		return finished;
	}
		
	class SmokeParticle extends Particle
	{
		private Color color;
		private int sprite;
		
		public SmokeParticle(float x, float y, float lifeTime)
		{
			super(x, y, lifeTime);
			velX = 32.f * (float) (2.0 * Math.random() - 1.0);
			velY = -32.f * (float) (Math.random());
			color = new Color(224, 224, 224, 255);
			sprite = (int) ((float)(sprites.getHorizontalCount()) * Math.random());
		}
		
		public void render(Graphics gfx)
		{
			color.a = getK();
			gfx.drawImage(sprites.getSprite(sprite, 0), posX, posY, color);
		}
	}
	
}




