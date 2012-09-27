package simciv.gamestates;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Game;

import backend.ByteArray2D;
import backend.Direction2D;
import backend.MathHelper;
import backend.geom.Vector2i;
import backend.pathfinding.IMapSpec;
import backend.pathfinding.IMapTarget;
import backend.pathfinding.MultiSeedPathFinder;

/**
 * Testing game state for pathfinding tests
 * @author Marc
 *
 */
public class TestPathFinder extends BasicGameState
{
	private int stateID;
	private ByteArray2D map;
	private Vector2i mousePos;
	private int mapScale;
	private ArrayList<Vector2i> start;
	private List<LinkedList<Vector2i>> paths;
	private MultiSeedPathFinder pathFinder;
	private boolean autoPathFinding;
	
	public TestPathFinder(int stateID)
	{
		this.stateID = stateID;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException
	{
		map = new ByteArray2D(32, 32);
		mapScale = Game.tilesSize;
		start = new ArrayList<Vector2i>();
//		end = new Vector2i(map.getWidth() - 1, map.getHeight() - 1);
		mousePos = new Vector2i();
		pathFinder = new MultiSeedPathFinder(map.getWidth(), map.getHeight());
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics gfx)
			throws SlickException
	{
		gfx.pushTransform();
		gfx.scale(mapScale, mapScale);
		
		gfx.setLineWidth(0.05f);
		
		for(int y = 0; y < map.getHeight(); y++)
		{
			for(int x = 0; x < map.getWidth(); x++)
			{
				byte c = map.get(x, y);
				
				switch(c)
				{
				case 0 : gfx.setColor(Color.white); break;
				case 1 : gfx.setColor(Color.darkGray); break;
				case 2 : gfx.setColor(Color.blue); break;
				default : gfx.setColor(Color.white); break;
				}
				
				gfx.fillRect(x, y, 1, 1);
				gfx.setColor(Color.darkGray);
				gfx.drawRect(x, y, 1, 1);
			}
		}
		
		if(paths != null)
		{
			for(List<Vector2i> path : paths)
			{
				Color clr = new Color(0, 255, 0, 128);
				float k = clr.g / (float)(path.size());
				
				for(Vector2i p : path)
				{
					gfx.setColor(clr);
					gfx.fillRect(p.x, p.y, 1, 1);
					clr.g -= k;
					clr.r += k;
				}
			}
		}
		
		gfx.setLineWidth(0.1f);
		
		gfx.setColor(new Color(0, 0, 0, 128));
		pathFinder.renderMatrix(gfx);
		
		for(Vector2i s : start)
		{
			gfx.setColor(Color.green);
			gfx.fillOval(s.x, s.y, 0.8f, 0.8f);
			gfx.setColor(Color.black);
			gfx.drawOval(s.x, s.y, 0.8f, 0.8f);
		}
//		gfx.setColor(Color.red);
//		gfx.fillOval(end.x, end.y, 0.8f, 0.8f);
//		gfx.setColor(Color.black);
//		gfx.drawOval(end.x, end.y, 0.8f, 0.8f);

		gfx.popTransform();
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException
	{
		Input input = container.getInput();
		mousePos.set(input.getMouseX(), input.getMouseY());
		
		Vector2i mapPos = new Vector2i(mousePos);
		mapPos.divide(mapScale);
		
		if(map.contains(mapPos))
		{
			if(input.isKeyDown(Input.KEY_0) || input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON))
				map.set(mapPos, (byte) 0);
			else if(input.isKeyDown(Input.KEY_1) || input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON))
				map.set(mapPos, (byte) 1);
			else if(input.isKeyDown(Input.KEY_2))
				map.set(mapPos, (byte) 2);
			else if(input.isKeyDown(Input.KEY_A))
				start.add(new Vector2i(mapPos));
			else if(input.isKeyDown(Input.KEY_B))
				map.set(mapPos, (byte) 2);
		}
		
		if(autoPathFinding)
			doPathFinding();
	}
	
	public void doPathFinding()
	{
		long timeBefore = System.currentTimeMillis();
		System.out.println("Start pathfinding...");
		
		ArrayList<Vector2i> seeds = new ArrayList<Vector2i>();
		for(Vector2i s : start)
			seeds.add(new Vector2i(s.x, s.y));
		
		pathFinder.setMaxPaths(4);
		pathFinder.setFindBlockedTargets(true);
		MapSpec specs = new MapSpec();
		paths = pathFinder.findPaths(seeds, specs, specs, false);
		
		System.out.println("Pathfinding done.");
		System.out.println("Elapsed : " + (System.currentTimeMillis() - timeBefore));
	}

	@Override
	public void keyPressed(int key, char c)
	{
		if(key == Input.KEY_SPACE)
		{
			doPathFinding();
		}
		else if(key == Input.KEY_ENTER)
		{
			autoPathFinding = !autoPathFinding;
			System.out.println("Auto-pathfinding " + (autoPathFinding ? "ON" : "OFF"));
		}
		else if(key == Input.KEY_X)
		{
			map.fill((byte) 0);
			paths = null;
			start.clear();
		}
		else if(key == Input.KEY_F)
		{
			map.fill((byte) 1);
		}
		else if(key == Input.KEY_G)
		{
			generateMap();
		}
	}
	
	private void generateMap()
	{
		map.fill((byte) 1);
		int n = 16;
		
		for(int t = 0; t < n; t++)
		{
			int m = MathHelper.randInt(15, 35);
			int x = MathHelper.randInt(0, map.getWidth() - 1);
			int y = MathHelper.randInt(0, map.getHeight() - 1);
			
			if(Math.random() < 0.8f)
			{
				// Corridors
				map.set(x, y, (byte) 0);
				
				byte d = Direction2D.random();

				for(; m > 0; m--)
				{
					if(Math.random() < 0.15f)
						d = Direction2D.random();

					x += Direction2D.vectors[d].x;
					y += Direction2D.vectors[d].y;
					
					if(x < 0)
						x = map.getWidth() - 1;
					if(y < 0)
						y = map.getHeight() - 1;
					if(x >= map.getWidth())
						x = 0;
					if(y >= map.getHeight())
						y = 0;					
					
					map.set(x, y, (byte) 0);
				}
			}
			else
			{
				// Rooms
				int w = MathHelper.randInt(3, 10);
				int h = MathHelper.randInt(3, 10);
				for(int j = 0; j < h; j++)
				{
					for(int i = 0; i < w; i++)
					{
						int xi = x + i;
						int yi = y + j;
						
						if(xi < 0)
							xi = map.getWidth() - 1;
						if(yi < 0)
							yi = map.getHeight() - 1;
						if(xi >= map.getWidth())
							xi = 0;
						if(yi >= map.getHeight())
							yi = 0;
						
						map.set(xi, yi, (byte) 0);
					}
				}
			}
		}
	}

	@Override
	public int getID()
	{
		return stateID;
	}
		
	class MapSpec implements IMapSpec, IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y) {
			return map.get(x, y) == 2;
		}
		
		@Override
		public boolean canPass(int x, int y) {
			return map.get(x, y) == 0;
		}	
	}

}




