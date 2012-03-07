package simciv;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Main state of the game
 * @author Marc
 *
 */
public class GamePlay extends BasicGameState
{
	int stateID = -1;
	View view;
	World world;
	CityBuilder builder;
	String debugText = "";
	Vector2i pointedCell = new Vector2i();
	boolean closeRequested = false;
	boolean paused = false;
	
	public GamePlay(int stateID)
	{
		this.stateID = stateID;
	}
	
	@Override
	public int getID()
	{
		return stateID;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException
	{		
		world = new World(64, 64);
		builder = new CityBuilder(world);
		view = new View(0, 0, 2);
		
		MapGenerator mapgen = new MapGenerator(131183);
		mapgen.generate(world.map);
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame game) throws SlickException
	{
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException
	{
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta)
			throws SlickException
	{
		if(delta > 100)
			delta = 100;
		
		Input input = gc.getInput();
		
		if(closeRequested)
			gc.exit();
		
		view.update(gc, delta / 1000.f);
		builder.cursorMoved(pointedCell);
		Terrain.updateTerrains(delta);
		
		if(!paused)
		{
			// Pointed cell
			pointedCell = view.convertCoordsToMap(input.getMouseX(), input.getMouseY());

			world.update(delta);
			builder.update(gc);
		}
		
		debugText = "x=" + pointedCell.x + ", y=" + pointedCell.y;
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
			throws SlickException
	{
		//GL11.glEnable(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_DEPTH_TEST);
		
		// World view
		view.look(gfx);
		IntRange2D mapRange = view.getMapRange(gc);
		
		/* World */
		
		world.render(mapRange, gc, gfx);
		
		/* Builder */
		
		builder.render(gfx);
			
		/* HUD */
		
		gfx.resetTransform();
		gfx.setColor(Color.white);
		gfx.drawString(debugText, 10, 50);
		
		if(paused)
			gfx.drawString("PAUSE", 10, 70);
	}
	
	@Override
	public void mousePressed(int button, int x, int y)
	{		
		if(!paused)
			builder.cursorPressed(button, pointedCell);
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy)
	{
		if(!paused)
			builder.cursorMoved(pointedCell);
	}

	@Override
	public void mouseReleased(int button, int x, int y)
	{
		if(!paused)
			builder.cursorReleased();
	}

	@Override
	public void keyReleased(int key, char c)
	{
		if(key == Input.KEY_G)
			world.map.toggleRenderGrid();
		if(key == Input.KEY_ESCAPE)
			closeRequested = true;
		if(key == Input.KEY_P)
			paused = !paused;
		if(key == Input.KEY_B)
			builder.setMode(CityBuilder.MODE_BUILDING);
		if(key == Input.KEY_E)
			builder.setMode(CityBuilder.MODE_ERASE);
		if(key == Input.KEY_R)
			builder.setMode(CityBuilder.MODE_ROAD);
	}
}


