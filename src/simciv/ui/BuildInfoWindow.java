package simciv.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import simciv.ui.base.Label;
import simciv.ui.base.WidgetContainer;
import simciv.ui.base.Window;

public class BuildInfoWindow extends Window
{
	private Label infoText;
	
	public BuildInfoWindow(WidgetContainer parent, String title)
	{
		super(parent, 0, 0, 250, 200, title);
		alignToCenter();
		
		try {
			infoText = new Label(this, 4, 4, getWidth() - 8, getHeight() - 8, "[Build info]");
			infoText.setTextWrap(true);
			infoText.setTextColor(Color.black);
			this.add(infoText);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public void setInfoText(String text)
	{
		infoText.setText(text);
	}

	@Override
	public boolean mousePressed(int button, int x, int y)
	{
		if(button == Input.MOUSE_RIGHT_BUTTON)
		{
			close();
			return true;
		}
		return super.mousePressed(button, x, y);
	}
	
}
