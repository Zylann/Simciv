package simciv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * Path finder for 2D matrix allowing step-by-step execution.
 * This one is inspired on the Dijkstra algorithm, and will
 * find the better path from startPos to targetPos.
 * @author Marc
 *
 */
public class PathFinder
{
	// States
	public static final byte INIT = 0;
	public static final byte RUNNING = 1;
	public static final byte FOUND = 2;
	public static final byte NOT_FOUND = 3;
	
	private Vector2i startPos;
	private Vector2i targetPos;
	private int targetBuildingID;
	private byte state;
	private int step;
	private int maxSteps;
	private HashMap<Vector2i, Byte> visited; // at, fromDirection
	private ArrayList<Vector2i> leaves;
	private transient Map mapRef;
	
	/**
	 * Constructs and initializes the path finder.
	 * @param map
	 * @param startX
	 * @param startY
	 * @param targetX
	 * @param targetY
	 */
	public PathFinder(Map map, int startX, int startY, int targetX, int targetY)
	{
		init(startX, startY, targetX, targetY);
		mapRef = map;
		maxSteps = 100;
	}
	
	public PathFinder(Map map, int startX, int startY, int targetBuildingID)
	{
		init(startX, startY, targetBuildingID);
		mapRef = map;
		maxSteps = 100;
	}
	
	public void setMaxSteps(int maxSteps)
	{
		this.maxSteps = maxSteps;
	}
	
	public void init(int startX, int startY, int targetX, int targetY)
	{
		startPos = new Vector2i(startX, startY);
		targetPos = new Vector2i(targetX, targetY);
		targetBuildingID = -1;
		state = INIT;
		step = 0;
		visited = new HashMap<Vector2i, Byte>();
		leaves = new ArrayList<Vector2i>();
		leaves.add(new Vector2i(startPos.x, startPos.y));
		visited.put(new Vector2i(startPos.x, startPos.y), (byte) -1);
	}
	
	public void init(int startX, int startY, int targetBuildingID)
	{
		init(startX, startY, 0, 0);
		targetPos = null;
		this.targetBuildingID = targetBuildingID;
	}
	
	public byte getState()
	{
		return state;
	}
	
	public Vector2i getTargetPos()
	{
		return targetPos;
	}
	
	/**
	 * Returns true if the algorithm has finished.
	 * @return
	 */
	public boolean isFinished()
	{
		return state == FOUND || state == NOT_FOUND;
	}
	
	protected boolean isTargetPos(Vector2i pos)
	{
		if(targetBuildingID == -1)
			return pos.equals(targetPos);
		else
			return mapRef.isBuildingAroundWithID(targetBuildingID, pos.x, pos.y);
	}
	
	/**
	 * Executes one step of the algorithm
	 * @return
	 */
	public boolean step()
	{
		if(isFinished())
			return true;

		if(state == INIT)
			state = RUNNING;

		if(maxSteps > 0 && step >= maxSteps)
		{
			state = NOT_FOUND;
			return isFinished();
		}
		
		if(leaves.isEmpty())
		{
			state = NOT_FOUND;
			return isFinished();
		}
		
		ArrayList<Vector2i> blockedLeaves = new ArrayList<Vector2i>();
		ArrayList<Vector2i> newLeaves = new ArrayList<Vector2i>();
				
		for(Vector2i leafPos : leaves)
		{
			if(!canPass(leafPos.x, leafPos.y))
			{
				blockedLeaves.add(leafPos);
				continue;
			}
						
			if(isTargetPos(leafPos))
			{
				state = FOUND;
				targetPos = leafPos;
				return isFinished();
			}
			
			for(byte dir = 0; dir < 4; dir++)
			{
				Vector2i dirVec = Direction2D.vectors[dir];
				Vector2i nextPos = new Vector2i(leafPos.x + dirVec.x, leafPos.y + dirVec.y);
				
				if(canPass(nextPos.x, nextPos.y))
				{
					if(!visited.containsKey(nextPos))
					{
						visited.put(new Vector2i(nextPos.x, nextPos.y), dir);
						newLeaves.add(nextPos);
					}
				}
			}
			
			blockedLeaves.add(leafPos);
		}
		
		leaves.removeAll(blockedLeaves);
		leaves.addAll(newLeaves);
		
		step++;
		
		return isFinished();
	}
	
	/**
	 * Retrieves the resulting path from algorithm's data and returns it.
	 * Returns null if none path was found.
	 * Start pos and target pos are included in the path.
	 * @return
	 */
	public LinkedList<Vector2i> retrievePath()
	{
		if(state != FOUND)
			return null;
		
		LinkedList<Vector2i> path = new LinkedList<Vector2i>();
		Vector2i pos = targetPos;
		path.add(new Vector2i(pos.x, pos.y));
		
		// From target to start pos
		while(!pos.equals(startPos))
		{
			Byte fromDir = visited.get(pos);
			if(fromDir == null)
			{
				System.out.println("WARN: unable to retrieve the path");
				return null;
			}
			Vector2i dirVec = Direction2D.vectors[fromDir];
			pos.x -= dirVec.x;
			pos.y -= dirVec.y;
			path.addFirst(new Vector2i(pos.x, pos.y));
		}
		
		return path;
	}
	
	/**
	 * Calls nbSteps times the step() method.
	 * @param nbSteps
	 * @return true if the algorithm finished.
	 */
	public boolean step(int nbSteps)
	{
		for(int i = 0; i < nbSteps && !isFinished(); i++)
			step();
		return isFinished();
	}
	
	/**
	 * Calls step() until the algorithm finishes.
	 * @return state of the path finder at the end of the algorithm (FOUND or NOT_FOUND)
	 */
	public byte stepAll()
	{
		while(!isFinished())
			step();
		return state;
	}
	
	/**
	 * Defines if we can go through the cell at (x,y).
	 * @param x
	 * @param y
	 * @return true if we can go, false if not.
	 */
	protected boolean canPass(int x, int y)
	{
		return mapRef.isCrossable(x, y) && mapRef.isRoad(x, y);
	}
	
	// Debug
	
	/**
	 * Draws a visual version of the state of the algorithm.
	 * This is just for debug purposes, this method shouldn't stay here in releases.
	 * @param gfx
	 */
	public void render(Graphics gfx)
	{
		gfx.pushTransform();
		
		gfx.scale(Game.tilesSize, Game.tilesSize);
		
		gfx.setColor(new Color(255, 0, 128));
		gfx.fillRect(startPos.x, startPos.y, 1, 1);
		
		gfx.setColor(new Color(0, 255, 255, 128));
		gfx.fillRect(targetPos.x, targetPos.y, 1, 1);
		
		gfx.setColor(new Color(0, 255, 0, 128));
		for(Vector2i pos : visited.keySet())
			gfx.fillRect(pos.x, pos.y, 1, 1);
		
		gfx.setColor(new Color(255, 0, 0));
		gfx.setLineWidth(2);
		for(Vector2i pos : leaves)
			gfx.drawRect(pos.x, pos.y, 1, 1);
		
		gfx.popTransform();
	}
	
	/**
	 * Draws a set of squares at all positions of the given path.
	 * For debug purpose, shouldn't appear in releases.
	 * @param path
	 */
	public void renderPath(Graphics gfx, List<Vector2i> path)
	{
		gfx.pushTransform();
		
		gfx.setColor(new Color(255, 255, 0, 128));
		gfx.scale(Game.tilesSize, Game.tilesSize);
		
		for(Vector2i pos : path)
			gfx.fillRect(pos.x, pos.y, 1, 1);
				
		gfx.popTransform();
	}
	
}



