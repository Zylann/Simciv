package backend.ui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import simciv.content.Content;

/**
 * Classic button with a text.
 * @author Marc
 *
 */
public class PushButton extends Button
{
	private static final int HEIGHT = 16;
	private static final int DEFAULT_WIDTH = 128;

	private String text;

	public PushButton(Widget parent, int x, int y, int w, String text)
	{
		super(parent, x, y, w, HEIGHT);
		this.text = text;
	}

	public PushButton(Widget parent, int x, int y, String text)
	{
		this(parent, x, y, DEFAULT_WIDTH, text);
	}
		
	public PushButton(Widget parent, String text)
	{
		this(parent, 0, 0, text);
	}
	
	public String getText()
	{
		return text;
	}
	
	@Override
	protected void onPress()
	{
		Content.sounds.uiButtonPress.play(1.f, 0.5f);
	}

	@Override
	protected void onRelease()
	{
		if(!isMouseOver())
			return;
		onAction();
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		UIRenderer.getTheme().renderPushButton(gfx, this);
	}

}



