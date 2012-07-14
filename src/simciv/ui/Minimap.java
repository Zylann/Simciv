package simciv.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import simciv.Game;
import simciv.View;

public class Minimap extends Widget
{
	private Image vizRef;
	private View viewRef;
	
	public Minimap(WidgetContainer parent, int x, int y, int width, int height)
	{
		super(parent, x, y, width, height);
	}
	
	public void setView(View view)
	{
		viewRef = view;
	}
	
	public void setViz(Image viz)
	{
		vizRef = viz;
		setSize(viz.getWidth(), viz.getHeight());
	}
	
	public void update(GameContainer gc, int delta)
	{
		
	}
	
	@Override
	public void render(GameContainer gc, Graphics gfx)
	{
		int x = getAbsoluteX();
		int y = getAbsoluteY();
		int viewX = viewRef.getMapX();
		int viewY = viewRef.getMapY();
		int viewWidth = gc.getWidth() / viewRef.getScale() / Game.tilesSize;
		int viewHeight = gc.getHeight() / viewRef.getScale() / Game.tilesSize;
		
		gfx.setColor(Color.red);
		gfx.setLineWidth(2);
		gfx.drawImage(vizRef, x, y);
		gfx.drawRect(x + viewX, y + viewY, viewWidth, viewHeight);
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
		return false;
	}

	@Override
	public boolean mouseReleased(int button, int x, int y)
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






