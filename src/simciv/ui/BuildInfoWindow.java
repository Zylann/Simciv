package simciv.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Input;

import backend.ui.IActionListener;
import backend.ui.Label;
import backend.ui.PushButton;
import backend.ui.Widget;
import backend.ui.WidgetContainer;
import backend.ui.Window;

public class BuildInfoWindow extends Window
{
	private Label infoText;
	private PushButton okButton;
	
	public BuildInfoWindow(WidgetContainer parent, String title)
	{
		super(parent, 0, 0, 250, 150, title);
		alignToCenter();
		
		// FIXME the text is displayed as one word per line
		infoText = new Label(this, 4, 4, getWidth() - 8, getHeight() - 8, "[Build info]");
		infoText.setTextWrap(true);
		infoText.setTextColor(Color.black);
		add(infoText);
		
		okButton = new PushButton(this, 0, 0, "OK");
		okButton.setAlignX(ALIGN_CENTER);
		okButton.setY(getHeight() - 36);
		okButton.addActionListener(new CloseAction());
		add(okButton);
	}
	
	public void setInfoText(String text)
	{
		infoText.setText(text);
	}

	@Override
	public boolean mousePressed(int button, int x, int y)
	{
		// The window closes on left click
		if(button == Input.MOUSE_RIGHT_BUTTON)
		{
			close();
			return true;
		}
		return super.mousePressed(button, x, y);
	}
	
	class CloseAction implements IActionListener
	{
		@Override
		public void actionPerformed(Widget sender) {
			close();
		}		
	}
	
}


