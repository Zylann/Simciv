package simciv.ui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import simciv.content.Content;

public class PushButton extends Button
{
	private static final int HEIGHT = 16;

	private IActionListener actionListener;
	private String text;

	public PushButton(WidgetContainer parent, int x, int y, String text)
	{
		// TODO allow various button widths
		super(parent, x, y, 128, HEIGHT);
		this.text = text;
	}

	public void setAction(IActionListener listener)
	{
		actionListener = listener;
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
		if(actionListener != null)
			actionListener.actionPerformed();
	}

	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		UIRenderer.instance().renderPushButton(gfx, this);
	}

}



