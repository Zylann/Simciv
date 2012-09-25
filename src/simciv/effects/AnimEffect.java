package simciv.effects;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import simciv.content.Content;

import backend.IntRange2D;

public class AnimEffect extends VisualEffect
{
	private static final long serialVersionUID = 1L;
	
	private Animation anim;

	public AnimEffect(int x, int y, SpriteSheet sprites, int frametime)
	{
		super(x, y);
		anim = new Animation(sprites, frametime);
		anim.setAutoUpdate(false);
	}

	@Override
	public void getRenderBounds(IntRange2D range)
	{
		// TODO world clip for effects (they are currently all rendered)
	}

	@Override
	protected void renderEffect(Graphics gfx)
	{
		gfx.drawAnimation(anim, 0, 0);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta)
	{
		int lastFrame = anim.getFrame();
		
		anim.update(delta);
		
		if(lastFrame > 0 && anim.getFrame() == 0)
			dispose();
	}
	
	public static class Splash extends AnimEffect
	{
		private static final long serialVersionUID = 1L;

		public Splash(int x, int y)
		{
			super(x, y, Content.sprites.effectSplash, 75);
		}		
	}
	
}




