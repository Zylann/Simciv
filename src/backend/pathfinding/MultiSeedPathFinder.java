package backend.pathfinding;

import java.util.ArrayList;
import java.util.LinkedList;

import org.newdawn.slick.Graphics;
import backend.ByteArray2D;
import backend.Direction2D;
import backend.geom.Vector2i;

/**
 * Intuitive pathfinder for searching a path from N positions to N unknown target locations.
 * This model assumes that each cell can potentially be a target.
 * Each of the computed paths are one of the best ones.
 * This is an useful way to link 2 groups of numerous positions, by using 1 data grid at a time.
 * The connexity used is 4.<br/><br/>
 * 
 * Example : on a map, there is trees, firemen, and burning trees.
 * Using this pathfinder with firemen and burning trees will find the N best paths linking them.<br/><br/>
 * 
 * @author Marc
 *
 */
public class MultiSeedPathFinder
{
	/** Default value for maxDistance **/
	private static final int DEFAULT_MAX_DISTANCE = 32;
	
	/** Default value for maxPaths **/
	private static final int DEFAULT_MAX_PATHS = 1;
	
	/**
	 * Constant used to mark a cell as an initial position.
	 * Its value has been choosen in the way that it is different
	 * from all Direction2D constants (see visited attribute).
	 */
	private static final byte INITIAL_POS = 16;
	
	/** Object used to know where we can move **/
	private IMapSpec mapSpec;
	
	/** Object used to know which cells are targets **/
	private IMapTarget mapTarget;
	
	/**
	 * Data matrix for visited nodes and path retrieving.
	 * It contains 3 sorts of information :<br/>
	 * Direction.NONE => not visited,<br/>
	 * One of the 4 directions => visited, direction choosen by a seed that moved on the cell,<br/>
	 * INITIAL_POS => an initial seed position<br/>
	 */
	private ByteArray2D visited;
	
	/** Distance limit applied to the seeds **/
	private int maxDistance;
	
	/** Max number of paths to find **/
	private int maxPaths;
	
	/**
	 * Find target positions even if we cannot move on (canPass(x,y) == false). 
	 * Only works if the target is connected to an available cell.
	 **/
	private boolean findBlockedTargets;
	
	/** Crossed distance on last execution **/
	private int distanceCrossed;
		
	/**
	 * Constructs the path finder.
	 * @param m : map reference for predicates evaluation
	 */
	public MultiSeedPathFinder(int mapWidth, int mapHeight)
	{
		maxDistance = DEFAULT_MAX_DISTANCE;
		maxPaths = DEFAULT_MAX_PATHS;
		visited = new ByteArray2D(mapWidth, mapHeight);
		visited.fill(Direction2D.NONE);
	}
	
	/**
	 * Set if the pathfinder have to find targets even if they are on a uncrossable cell.
	 * (only if they are connected to a crossable cell).
	 * @param f
	 */
	public void setFindBlockedTargets(boolean f)
	{
		findBlockedTargets = f;
	}
	
	/**
	 * Sets the distance limit applied to the seeds
	 * @param m : positive value
	 */
	public void setMaxDistance(int m)
	{
		maxDistance = m > 0 ? m : 1;
	}
	
	/**
	 * Sets the max number of paths to find
	 * @param m : positive value
	 */
	public void setMaxPaths(int m)
	{
		maxPaths = m > 0 ? m : 1;
	}
	
	/**
	 * Returns true if we can move to the given cell position
	 * @param p
	 * @return
	 */
	private boolean canPass(Vector2i p)
	{
		return mapSpec.canPass(p.x, p.y);
	}
	
	/**
	 * Returns true if the cell at the given position is a target
	 * @param p
	 * @return
	 */
	private boolean isTarget(Vector2i p)
	{
		return mapTarget.isTarget(p.x, p.y);
	}
	
	/**
	 * Method defining how distances are computed.
	 * Returns the distance from a to b.
	 * @param a : first point
	 * @param b : second point
	 * @return distance between the two points
	 */
	protected int getDistance(Vector2i a, Vector2i b)
	{
		return a.manhattanDistanceFrom(b);
	}
	
	public int getCrossedDistance()
	{
		return distanceCrossed;
	}
	
	/**
	 * Finds the best path from one given position to the closest compliant cell
	 * @param srcX : start pos X
	 * @param srcY : start pos Y
	 * @param mapSpec : map specs (where can we move and which cells are targets)
	 * @param reversePath : reverses computed path
	 * @return the path, null if not found
	 */
	public LinkedList<Vector2i> findPath(
			int srcX, int srcY,
			IMapSpec mapSpec, IMapTarget target,
			boolean reversePath)
	{
		ArrayList<Vector2i> seeds = new ArrayList<Vector2i>();
		seeds.add(new Vector2i(srcX, srcY));
		
		setMaxPaths(1); // We only search the first path
		
		ArrayList<LinkedList<Vector2i>> paths =
			findPaths(seeds, mapSpec, target, reversePath);
		
		if(paths != null && !paths.isEmpty())
			return paths.get(0);
		return null;
	}
	
	public LinkedList<Vector2i> findPath(
			int srcX, int srcY,
			IMapSpec mapSpec, IMapTarget target)
	{
		return findPath(srcX, srcY, mapSpec, target, false);
	}
	
	/**
	 * Finds the best path from the given positions to the closest compliant cells.
	 * @param seeds
	 * @param mapSpec
	 * @param reversePaths : reverses computed paths
	 * @return
	 */
	public ArrayList<LinkedList<Vector2i>> findPaths(
			ArrayList<Vector2i> seeds,
			IMapSpec mapSpec, IMapTarget target,
			boolean reversePaths)
	{		
		if(seeds.isEmpty())
			return null;
		
		// Init
		this.mapSpec = mapSpec;
		this.mapTarget = target;
		this.distanceCrossed = 0;		
		ArrayList<Vector2i> nextSeeds = new ArrayList<Vector2i>();
		visited.fill(Direction2D.NONE);
		ArrayList<LinkedList<Vector2i>> paths = new ArrayList<LinkedList<Vector2i>>();
		
		// Mark initial positions as visited, and check if we are already on targets
		for(Vector2i s : seeds)
		{
			if(visited.contains(s.x, s.y))
			{
				visited.set(s.x, s.y, INITIAL_POS);
				
				if(isTarget(s))
				{
					LinkedList<Vector2i> path = new LinkedList<Vector2i>();
					path.add(s);
					paths.add(path);
					
					if(paths.size() == maxPaths)
						return paths;
				}
			}
		}
		
		// This variable will be used in the main loop
		Vector2i nextPos = new Vector2i();

		// Main loop
		// Note : using the manhattan distance,
		// the distance crossed equals the number of steps.
		while(distanceCrossed < maxDistance)
		{
			// For each seed
			for(Vector2i s : seeds)
			{
				// Look at the 4 directions
				for(byte dir = 0; dir < 4; dir++)
				{
					// Get next pos from its direction
					Vector2i dirVec = Direction2D.vectors[dir];
					nextPos.set(s.x + dirVec.x, s.y + dirVec.y);
										
					// If the direction is available and if we can pass here
					if(visited.contains(nextPos.x, nextPos.y) &&
						visited.get(nextPos.x, nextPos.y) == Direction2D.NONE)
					{
						if(canPass(nextPos))
						{
							// Create a new seed
							nextSeeds.add(new Vector2i(nextPos));
							visited.set(nextPos.x, nextPos.y, dir); // Mark visited
							
							// If the new seed is the target
							if(isTarget(nextPos))
							{
								// Target reached ! Retrieve its path and add it to the list.
								// Note : the target will not be found twice as it is now marked as visited.
								paths.add(retrievePath(nextPos, reversePaths));
								
								// End the main loop if we reached the max number of expected paths
								if(paths.size() >= maxPaths)
									return paths;
							}
						}
						else if(findBlockedTargets && isTarget(nextPos))
						{
							// Target reached ! Retrieve its path and add it to the list.
							// Note : the target will not be found twice as it is now marked as visited.
							visited.set(nextPos.x, nextPos.y, dir); // Mark visited
							paths.add(retrievePath(nextPos, reversePaths));
							
							// End the main loop if we reached the max number of expected paths
							if(paths.size() >= maxPaths)
								return paths;
						}
					}
				}
			}
			
			// Swap seeds containers
			ArrayList<Vector2i> temp = nextSeeds;
			nextSeeds = seeds;
			nextSeeds.clear();
			seeds = temp;
			
			if(seeds.isEmpty()) // If no seeds for the next update
				return paths; // No more paths to find
			
			distanceCrossed++;
		}
		
		return paths;
	}

	/**
	 * Recomputes the path linking an initial position and the given position
	 * using the data stored in the matrix.
	 * @param p 
	 * @param reverse : if true, will reverse the computed path
	 * @return points of the path
	 */
	private LinkedList<Vector2i> retrievePath(Vector2i p, boolean reverse)
	{
		int x = p.x;
		int y = p.y;
		
		LinkedList<Vector2i> path = new LinkedList<Vector2i>();
		path.add(new Vector2i(x, y));
		
		while(visited.get(x, y) != INITIAL_POS)
		{			
			byte d = visited.get(x, y);
			
			x -= Direction2D.vectors[d].x;
			y -= Direction2D.vectors[d].y;
			
			if(reverse)
				path.addLast(new Vector2i(x, y));
			else
				path.addFirst(new Vector2i(x, y));
		}
		
		return path;
	}
	
	/**
	 * Draws a visual version of the state of the matrix (for debug).
	 * Each cell is drawn as using an 1:1 scale.
	 * The current transformation matrix will not be altered.
	 * @param gfx
	 */
	public void renderMatrix(Graphics gfx)
	{
		for(int y = 0; y < visited.getHeight(); y++)
		{
			for(int x = 0; x < visited.getWidth(); x++)
			{
				byte d = visited.get(x, y);
				if(d == INITIAL_POS)
					gfx.fillOval(x + 0.25f, y + 0.25f, 0.5f, 0.5f);
				else
					Direction2D.drawArrow(gfx, x, y, d);
			}
		}
	}

}


