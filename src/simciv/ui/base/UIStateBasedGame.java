package simciv.ui.base;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public abstract class UIStateBasedGame extends StateBasedGame
{
	protected WidgetContainer ui;
	private UIInputForwarder inputForwarder;
	private WidgetContainer dummyUI;
	
	public UIStateBasedGame(String name)
	{
		super(name);
		dummyUI = new RootPane(0, 0);
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
	
	public void onContainerResize(int width, int height)
	{
		if(ui != null)
		{
			int gs = UIRenderer.instance().getGlobalScale();
			ui.onScreenResize(width / gs, height / gs);
		}
	}

	@Override
	protected void postRenderState(GameContainer container, Graphics gfx)
			throws SlickException
	{
		if (ui != null)
		{
			UIRenderer.instance().beginRender(gfx);
			ui.render(container, gfx);
			UIRenderer.instance().endRender(gfx);
		}
	}
	
}
