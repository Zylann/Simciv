package simciv.effects;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;


/**
 * Displays an image rising and fading
 * @author Marc
 *
 */
public class RisingIcon extends VisualEffect
{
	protected Color color;
	protected Image sprite;

	public RisingIcon(int x, int y, Image sprite)
	{
		super(x, y);
		color = new Color(255, 255, 255, 1);
		duration = 800;
		this.sprite = sprite;
	}

	protected void updateEffect(int delta)
	{
		float k = getK();
		posY -= (1.f - k) * 64.f * (float)delta / 1000.f;
		time += delta;
		color.a = 1.f - k;
	}
	
	public void render(Graphics gfx)
	{
		gfx.drawImage(sprite, posX, posY, color);
	}
}




