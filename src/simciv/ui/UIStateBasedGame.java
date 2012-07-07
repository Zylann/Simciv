package simciv.ui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public abstract class UIStateBasedGame extends StateBasedGame
{
	protected WidgetContainer ui;
	private UIInputForwarder inputForwarder;
	private WidgetContainer dummyUI = new WidgetContainer(null, 0, 0, 0, 0);
	
	public UIStateBasedGame(String name)
	{
		super(name);
	}
	
	public void setUI(WidgetContainer ui)
	{
		if(ui == null)
			this.ui = dummyUI;
		else
			this.ui = ui;
		
		if(inputForwarder == null)
		{
			Input input = getContainer().getInput();
			inputForwarder = new UIInputForwarder(input);
			input.addPrimaryListener(inputForwarder);
		}
		
		inputForwarder.setUI(ui);
	}

	@Override
	protected void postRenderState(GameContainer container, Graphics gfx)
			throws SlickException
	{
		if (ui != null)
		{
			UIRenderer.instance().beginRender(gfx);
			ui.render(gfx);
			UIRenderer.instance().endRender(gfx);
		}
	}
	
}