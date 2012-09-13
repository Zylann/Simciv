package simciv.effects;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import backend.IntRange2D;
import backend.MathHelper;
import simciv.content.Content;

/**
 * Displays a smoke explosion
 * @author Marc
 *
 */
public class SmokeExplosion extends VisualEffect
{
	private static final long serialVersionUID = 1L;
	
	private SmokeParticle particles[];
	
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
		
		// Generate particles
		float durationVariation = meanDuration / 4;
		for(int i = 0; i < particles.length; i++)
		{
			float lifeTime = (int) (meanDuration + MathHelper.randS(durationVariation));
			int x = (int) ((2.0 * Math.random() - 0.5) * excentering);
			int y = (int) ((2.0 * Math.random() - 0.5) * excentering);
			particles[i] = new SmokeParticle(x, y, lifeTime);
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
	public void getRenderBounds(IntRange2D range)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta)
	{
		float deltaS = delta / 1000.f;
		boolean finished = true;
		
		for(int i = 0; i < particles.length; i++)
		{
			if(particles[i] != null)
			{
				finished = false;
				particles[i].update(deltaS);
				if(particles[i].isDead())
					particles[i] = null;
			}
		}
		
		if(finished)
			dispose();
	}
		
	class SmokeParticle extends Particle
	{
		private Color color;
		private int sprite;
		
		public SmokeParticle(float x, float y, float lifeTime)
		{
			super(x, y, lifeTime);
			velX = MathHelper.randS(32.f);
			velY = -32.f * (float) (Math.random());
			color = new Color(224, 224, 224, 255);
			sprite = MathHelper.randInt(0, Content.sprites.effectSmoke.getHorizontalCount());
		}
		
		public void render(Graphics gfx)
		{
			color.a = getK();
			gfx.drawImage(Content.sprites.effectSmoke.getSprite(sprite, 0), posX, posY, color);
		}
	}
	
}




