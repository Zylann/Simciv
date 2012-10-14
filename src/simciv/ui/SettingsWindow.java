package simciv.ui;

import backend.ui.IActionListener;
import backend.ui.PushButton;
import backend.ui.Widget;
import backend.ui.WidgetContainer;
import backend.ui.Window;

public class SettingsWindow extends Window
{
	public SettingsWindow(Widget parent)
	{
		super(parent, 200, 100, "Settings");
		
		WidgetContainer content = this.getContent();
		
		PushButton cancelButton = new PushButton(content, 0, 0, 64, "Cancel");
		cancelButton.setAlign(ALIGN_LEFT, ALIGN_BOTTOM, 8, 8);
		cancelButton.addActionListener(this.new CloseAction());
		content.add(cancelButton);
		
		PushButton applyButton = new PushButton(content, 0, 0, 64, "Apply");
		applyButton.setAlign(ALIGN_RIGHT, ALIGN_BOTTOM, 8, 8);
		applyButton.addActionListener(this.new ApplyAction());
		content.add(applyButton);

		// TODO UI CheckBox
		// TODO UI Slider
	}
	
	private class ApplyAction implements IActionListener
	{
		@Override
		public void actionPerformed(Widget sender) {
			close();
		}	
	}

}

