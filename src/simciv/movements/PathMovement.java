package simciv.movements;

import java.util.LinkedList;

import backend.Direction2D;
import backend.Vector2i;

import simciv.maptargets.IMapTarget;
import simciv.units.Unit;

/**
 * Moves a unit following a pre-computed path
 * @author Marc
 *
 */
public class PathMovement implements IMovement
{
	private static final long serialVersionUID = 1L;
	
	private LinkedList<Vector2i> path; // TODO create a Path class storing directions (lighter)
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
		
		u.setDirection(Direction2D.toDirection(pos, nextPos));
		if(!u.moveIfPossible())
		{
			blocked = true;
		}				
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


