package simciv.ui.base;

/**
 * Widget class with event methods already defined by default
 * @author Marc
 *
 */
public abstract class BasicWidget extends Widget
{
	public BasicWidget(Widget parent, int x, int y, int width, int height)
	{
		super(parent, x, y, width, height);
	}
	
	public BasicWidget(Widget parent, int width, int height)
	{
		this(parent, 0, 0, width, height);
	}

	@Override
	public boolean mouseMoved(int oldX, int oldY, int newX, int newY)
	{
		return false;
	}

	@Override
	public boolean mouseDragged(int oldX, int oldY, int newX, int newY)
	{
		return false;
	}

	@Override
	public boolean mousePressed(int button, int x, int y)
	{
		return contains(x, y);
	}

	@Override
	public boolean mouseReleased(int button, int x, int y)
	{
		return false;
	}

	@Override
	public boolean mouseClicked(int button, int x, int y, int clickCount)
	{
		return contains(x, y);
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
