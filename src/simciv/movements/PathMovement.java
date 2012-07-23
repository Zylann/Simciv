package simciv.movements;

import java.util.LinkedList;

import simciv.Direction2D;
import simciv.Vector2i;
import simciv.maptargets.IMapTarget;
import simciv.units.Unit;

/**
 * Moves a unit following a pre-computed path
 * @author Marc
 *
 */
public class PathMovement implements IMovement
{
	private LinkedList<Vector2i> path;
	private boolean blocked;
	public IMapTarget target; // Target that was used to build the path
	
	public PathMovement(LinkedList<Vector2i> path, IMapTarget target)
	{
		this.path = path;
		this.target = target;
		blocked = false;
	}
	
	@Override
	public void tick(Unit u)
	{
		if(isFinished())
			return;
		
		Vector2i pos = new Vector2i(u.getX(), u.getY());
		Vector2i nextPos = path.pop();
//		System.out.println("Following path (next is " + nextPos + ")"); // debug
		
		u.setDirection(Direction2D.toDirection(pos, nextPos));
		if(!u.moveIfPossible())
		{
//			System.out.println("Can't move (d=" + u.getDirection() + ")"); // debug
			blocked = true;
		}
				
		// Debug
//		if(path.isEmpty())
//			System.out.println("End of path");
	}

	@Override
	public boolean isBlocked()
	{
		return blocked;
	}

	@Override
	public boolean isFinished()
	{
		return path.isEmpty() || path == null;
	}

	@Override
	public IMapTarget getTarget()
	{
		return target;
	}

}


