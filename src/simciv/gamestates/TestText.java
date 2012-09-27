package simciv.gamestates;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import backend.ui.Text;

public class TestText extends BasicGameState
{
	private int stateID;
	private Text text;
	private int textWidth;
	
	public TestText(int stateID)
	{
		this.stateID = stateID;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException
	{
		text = new Text();
		text.setWrapEnabled(true);
		textWidth = 300;
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
			throws SlickException
	{		
		gfx.setColor(Color.white);
		gfx.setLineWidth(1);
		gfx.drawLine(textWidth, 0, textWidth, gc.getHeight());
		
		text.render(gfx, 0, 0);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta)
			throws SlickException
	{
		Input input = gc.getInput();
		if(input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON))
		{
			textWidth = input.getMouseX();
			text.setMaxLineWidth(textWidth);
		}
	}
	
	@Override
	public void mousePressed(int button, int x, int y)
	{
	}

	@Override
	public void keyPressed(int key, char c)
	{
		String str = 
			"Hello my name is Zylann and I am testing my brand" +
			" new text system that provides line wrapping. I'd" +
			" like it to work soon so that I will add it to my game !";
		text.setFromString(str);
	}

	@Override
	public int getID()
	{
		return stateID;
	}

}


