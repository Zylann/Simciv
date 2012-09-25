package backend.pathfinding;

import java.util.LinkedList;

import backend.ByteArray2D;
import backend.Direction2D;
import backend.geom.Vector2i;

/**
 * Intuitive path finder based on searching a path to a known position.
 * It will find one valid path, but not always the best one.
 * It is called "mono seed" because of its intuitive behavior :
 * There is only one mover that explores the map, goes back if it is blocked and tries other directions. 
 * The seed chooses its preferred direction by calculating bird-fly distances from the target.<br/>
 * It may work fine in orthogonal labyrinths of tracks.<br/><br/>
 * 
 * Drawback : if a path doesn't exist, this pathfinder may cost a lot of performance,
 * because all available cells will be explored until the seed goes back to its initial position.
 * @author Marc
 *
 */
public class MonoSeedPathFinder
{
	/** Default value of maxDistance **/
	private static final int DEFAULT_MAX_DISTANCE = 64;
	
	/** Initial seed position **/
	private Vector2i initialPos;
	
	/** Current seed position **/
	private Vector2i pos;
	
	/** Target position **/
	private Vector2i dst;
	
	/** Object used to know where we can move **/
	private IMapSpec mapSpec;
	
	/** Data grid the size of the map **/
	private ByteArray2D visited;
	
	/** Distance limit from the initial position **/
	private int maxDistance;
	
	public MonoSeedPathFinder(int mapWidth, int mapHeight)
	{
		maxDistance = DEFAULT_MAX_DISTANCE;
		visited = new ByteArray2D(mapWidth, mapHeight);
	}
	
	protected boolean canPass(Vector2i p)
	{
		if(!visited.contains(p.x, p.y))
			return false;
		return mapSpec.canPass(p.x, p.y);
	}
	
	/**
	 * Method defining how distances are computed.
	 * Returns the distance from a to b.
	 * @param a : first point
	 * @param b : second point
	 * @return distance between the two points
	 */
	protected float getDistance(Vector2i a, Vector2i b)
	{
		//return a.manhattanDistanceFrom(b); // Causes weird behavior
		return a.distanceFrom(b);
	}
	
	public void setMaxDistance(int m)
	{
		maxDistance = m;
	}
		
	public LinkedList<Vector2i> findPath(
			int srcX, int srcY, 
			int dstX, int dstY, 
			IMapSpec mapSpec)
	{
		if(!visited.contains(srcX, srcY))
			return null;
		
		LinkedList<Vector2i> path = new LinkedList<Vector2i>();
		initialPos = new Vector2i(srcX, srcY);
		pos = new Vector2i(initialPos);
		dst = new Vector2i(dstX, dstY);
		
		if(pos.equals(dst))
		{
			path.add(dst);
			return path;
		}
		
		this.mapSpec = mapSpec;
		visited.fill((byte) 0);

		while(!pos.equals(dst))
		{
			Vector2i nextPos = getBetterNextPos(pos);
			
			if(nextPos != null)
			{
				visited.set(nextPos.x, nextPos.y, (byte) 1);
				path.addLast(pos);
				pos = nextPos;
			}
			else
			{
				if(path.isEmpty())
					return null;
				else
					pos = path.removeLast();
			}
		}
		
		if(pos.equals(dst))
			return path;
		else
			return null;
	}
	
	private Vector2i getBetterNextPos(Vector2i from)
	{
		float minDistance = Integer.MAX_VALUE;
		Vector2i electedNextPos = null;
		
		for(byte dir = 0; dir < 4; dir++)
		{
			Vector2i dirVec = Direction2D.vectors[dir];
			Vector2i nextPos = new Vector2i(from.x + dirVec.x, from.y + dirVec.y);
			
			if(canPass(nextPos) && getDistance(initialPos, nextPos) < maxDistance)
			{
				if(visited.get(nextPos.x, nextPos.y) == 0)
				{
					float d = getDistance(nextPos, dst);
					if(d < minDistance)
					{
						minDistance = d;
						electedNextPos = nextPos;
					}
				}
			}
		}
		
		return electedNextPos;
	}
		
}




