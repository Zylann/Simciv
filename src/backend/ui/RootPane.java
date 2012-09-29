package backend.ui;

//import org.newdawn.slick.Color;
//import org.newdawn.slick.GameContainer;
//import org.newdawn.slick.Graphics;

public class RootPane extends WidgetContainer
{
	public RootPane(int width, int height)
	{
		super(null, 0, 0, width, height);
	}
	
	public void onScreenResize(int width, int height)
	{
		setSize(width, height);
	}

	// For debug
//	@Override
//	public void render(GameContainer gc, Graphics gfx)
//	{
//		super.render(gc, gfx);
//		gfx.setColor(Color.red);
//		gfx.drawRect(posX, posY, width-2, height-2);
//	}

}


