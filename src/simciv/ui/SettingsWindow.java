package simciv.ui;

import org.newdawn.slick.util.Log;

import simciv.Game;

import backend.ui.Checkbox;
import backend.ui.IActionListener;
import backend.ui.Label;
import backend.ui.PushButton;
import backend.ui.Widget;
import backend.ui.WidgetContainer;
import backend.ui.Window;

public class SettingsWindow extends Window
{
	private Checkbox fancyMovementsCB;
	private Checkbox vsyncCB;
	private Checkbox smoothDeltasCB;
	
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
		applyButton.addActionListener(new ApplyAction());
		content.add(applyButton);
		
		fancyMovementsCB = new Checkbox(content, 4, 4);
		Label fancyMovementsLB = new Label(
				content, fancyMovementsCB.getWidth() + 4, 6, "Fancy units movements");
		content.add(fancyMovementsCB);
		content.add(fancyMovementsLB);
		
		vsyncCB = new Checkbox(content, 4, 20);
		Label vsyncLB = new Label(
				content, vsyncCB.getWidth() + 4, 22, "Use vertical sync");
		content.add(vsyncCB);
		content.add(vsyncLB);

		smoothDeltasCB = new Checkbox(content, 4, 36);
		Label smoothDeltasLB = new Label(
				content, smoothDeltasCB.getWidth() + 4, 38, "Smooth framerate");
		content.add(smoothDeltasCB);
		content.add(smoothDeltasLB);

		this.alignToCenter();
		this.addOnOpenAction(new UpdateControlsAction());

		// TODO UI Slider
	}
	
	private void updateControls()
	{
		fancyMovementsCB.setChecked(Game.settings.isRenderFancyUnitMovements());
		vsyncCB.setChecked(Game.settings.isUseVSync());
		smoothDeltasCB.setChecked(Game.settings.isSmoothDeltasEnabled());
	}
	
	private void applyChanges()
	{
		Log.debug("Applying setting changes");
		
		Game.settings.setRenderFancyUnitMovements(fancyMovementsCB.isChecked());
		Game.settings.setUseVSync(vsyncCB.isChecked());
		Game.settings.setSmoothDeltasEnabled(smoothDeltasCB.isChecked());
		
		Game.applySettings();
	}
	
	private class UpdateControlsAction implements IActionListener
	{
		@Override
		public void actionPerformed(Widget sender) {
			updateControls();
		}	
	}
	
	private class ApplyAction implements IActionListener
	{
		@Override
		public void actionPerformed(Widget sender) {
			applyChanges();
			close();
		}	
	}

}

