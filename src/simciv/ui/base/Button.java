package simciv.ui.base;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Input;

/**
 * Every widget that can be pressed in order to do something
 * @author Marc
 *
 */
public abstract class Button extends Widget
{
	private boolean mouseOver;
	private boolean pressed;
	private boolean enabled;
	private List<IActionListener> actionListeners;
	
	public Button(Widget parent, int x, int y, int width, int height)
	{
		super(parent, x, y, width, height);
		mouseOver = false;
		pressed = false;
		enabled = true;
		actionListeners = new ArrayList<IActionListener>();
	}
	
	public Button(Widget parent, int w, int h)
	{
		this(parent, 0, 0, w, h);
	}
	
	/**
	 * Adds a listener which be notified when the button is activated
	 * (defines the action associated to the button).
	 * @param l
	 */
	public void addActionListener(IActionListener l)
	{
		if(l != null)
			actionListeners.add(l);
	}
	
	/**
	 * Notifies all action listeners of the button.
	 * This method must be called when we consider that the button has been activated.
	 */
	protected void onAction()
	{
		for(IActionListener l : actionListeners)
			l.actionPerformed(this);
	}
	
	public boolean isPressed()
	{
		return pressed;
	}
	
	/**
	 * Returns true if the mouse cursor is over the button
	 * @return
	 */
	public boolean isMouseOver()
	{
		return mouseOver;
	}
	
	/**
	 * Returns true if the button is enabled.
	 * @return
	 */
	public boolean isEnabled()
	{
		return enabled;
	}
	
	/**
	 * Sets the button's enabled state.
	 * @param e : enables the button if true, disables if false
	 * @return the button itself for chaining
	 */
	public Button setEnabled(boolean e)
	{
		enabled = e;
		return this;
	}

	protected abstract void onPress();
	protected abstract void onRelease();
	
	/**
	 * Sets the press state of the button, as if done with the mouse
	 * @param p
	 */
	protected void press(boolean p)
	{
		if(p)
		{
			if(!pressed)
				onPress();
			pressed = true;
		}
		else
		{
			if(pressed)
				onRelease();
			pressed = false;
		}
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
			if(enabled && button == Input.MOUSE_LEFT_BUTTON)
				press(true);
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseReleased(int button, int x, int y)
	{
		if(pressed && contains(x, y))
		{
			onRelease();
		}
		pressed = false;
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


