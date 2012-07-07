package simciv.ui;

import org.newdawn.slick.Input;
import org.newdawn.slick.util.InputAdapter;

/**
 * Sends events to UI elements. If they are not intercepted,
 * they are forwarded to the game.
 * @author Marc
 *
 */
public class UIInputForwarder extends InputAdapter
{
	private final Input input;
	private WidgetContainer ui;

	public UIInputForwarder(Input input)
	{
		if (input == null)
		{
			throw new NullPointerException("input");
		}

		this.input = input;
	}
	
	public void setUI(WidgetContainer ui)
	{
		this.ui = ui;
	}
	
	private int getGScale()
	{
		return UIRenderer.instance().getGlobalScale();
	}

	@Override
	public void mouseWheelMoved(int change)
	{
		if(ui == null)
			return;
		if(ui.mouseWheelMoved(change))
			input.consumeEvent();
	}

	@Override
	public void mousePressed(int button, int x, int y)
	{
		if(ui == null)
			return;
		if(ui.mousePressed(button,
				x / getGScale(),
				y / getGScale()))
			input.consumeEvent();
	}

	@Override
	public void mouseReleased(int button, int x, int y)
	{
		if(ui == null)
			return;
		if(ui.mouseReleased(button,
				x / getGScale(),
				y / getGScale()))
			input.consumeEvent();
	}

	@Override
	public void mouseMoved(int oldX, int oldY, int newX, int newY)
	{
		if(ui == null)
			return;
		if(ui.mouseMoved(
				oldX / getGScale(),
				oldY / getGScale(),
				newX / getGScale(),
				newY / getGScale()))
			input.consumeEvent();
	}

	@Override
	public void mouseDragged(int oldX, int oldY, int newX, int newY)
	{
		if(ui == null)
			return;
		if(ui.mouseDragged(
				oldX / getGScale(),
				oldY / getGScale(),
				newX / getGScale(),
				newY / getGScale()))
			input.consumeEvent();
	}

	@Override
	public void keyPressed(int key, char c)
	{
		if(ui == null)
			return;
		if(ui.keyPressed(key, c))
			input.consumeEvent();
	}

	@Override
	public void keyReleased(int key, char c)
	{
		if(ui == null)
			return;
		if(ui.keyReleased(key, c))
			input.consumeEvent();
	}

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount)
	{
		if(ui == null)
			return;
		if(ui.mouseClicked(button, x, y, clickCount))
			input.consumeEvent();
	}

	@Override
	public void inputStarted()
	{
		super.inputStarted();
	}

	@Override
	public void inputEnded()
	{
		super.inputEnded();
	}
}
