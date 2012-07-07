package simciv.ui;

import org.newdawn.slick.Input;

public abstract class Button extends Widget
{
	private boolean mouseOver;
	private boolean pressed;
	
	public Button(WidgetContainer parent, int x, int y, int width, int height)
	{
		super(parent, x, y, width, height);
		mouseOver = false;
		pressed = false;
	}
	
	public boolean isPressed()
	{
		return pressed;
	}
	
	public boolean isMouseOver()
	{
		return mouseOver;
	}

	protected abstract void onPress();
	protected abstract void onRelease();
	
	protected void press(boolean p)
	{
		if(p)
		{
			if(!pressed)
				onPress();
			pressed = true;
		}
		else
			pressed = false;
	}

	@Override
	public boolean mouseMoved(int oldX, int oldY, int newX, int newY)
	{
		mouseOver = contains(newX, newY);
		return false;
	}

	@Override
	public boolean mousePressed(int button, int x, int y)
	{
		if(contains(x, y))
		{
			if(button == Input.MOUSE_LEFT_BUTTON)
				press(true);
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseReleased(int button, int x, int y)
	{
		pressed = false;
		if(contains(x, y))
			onRelease();
		return false;
	}
		
	@Override
	public boolean mouseDragged(int oldX, int oldY, int newX, int newY)
	{
		return false;
	}

	@Override
	public boolean mouseClicked(int button, int x, int y, int clickCount)
	{
		return false;
	}

	@Override
	public boolean mouseWheelMoved(int change)
	{
		return false;
	}

	@Override
	public boolean keyPressed(int key, char c)
	{
		return false;
	}

	@Override
	public boolean keyReleased(int key, char c)
	{
		return false;
	}

}
