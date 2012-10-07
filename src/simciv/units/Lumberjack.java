package simciv.units;

import org.newdawn.slick.Graphics;

import backend.Direction2D;
import backend.geom.Vector2i;
import backend.pathfinding.IMapSpec;
import backend.pathfinding.IMapTarget;

import simciv.Map;
import simciv.MapCell;
import simciv.Nature;
import simciv.Resource;
import simciv.builds.Loggers;
import simciv.builds.Workplace;
import simciv.content.Content;

public class Lumberjack extends Citizen
{
	private static final long serialVersionUID = 1L;
	
	/** Time to cut down a tree, in seconds **/
	private static final int TREE_CHOP_TIME = 16;
	
	// States
	private static final byte FIND_TREE = 0;
	private static final byte CHOP_TREE = 1;
	private static final byte BACK_TO_WORKPLACE = 2;
	
	private int ticksChoppingTree;
	private boolean hasLogs;

	public Lumberjack(Map m, Workplace w)
	{
		super(m, w);
		setState(FIND_TREE);
	}
	
	@Override
	public void onDispose()
	{
		super.onDispose();
		stopChopTree(); // Unmark chopped tree
	}

	@Override
	public void tick()
	{
//		byte stateTemp = getState();
		
		switch(getState())
		{
		case FIND_TREE : tickFindTree(); break;
		case CHOP_TREE : tickChopTree(); break;
		case BACK_TO_WORKPLACE : tickBackToWorkplace(); break;
		}
		
//		lastState = stateTemp;
	}

	private void tickFindTree()
	{
		if(!isMovement() || isMovementBlocked() || isMovementFinished())
		{
			if(!startChopTree())
			{
				if(!findAndGoTo(new WalkableFloor(), new TreeTarget(), -1))
					setState(BACK_TO_WORKPLACE);
			}
		}	
	}

	private void tickChopTree()
	{
		if(isMovement())
			setMovement(null);
		
		// TODO add tree chop sound
		ticksChoppingTree--;
		if(ticksChoppingTree == 0)
		{
			stopChopTree();
			
			getFrontCell().nature = Nature.NONE;
			
			// TODO tree growth
			// TODO treefall sound
			
			hasLogs = true;
			setState(BACK_TO_WORKPLACE);
		}
	}

	private void tickBackToWorkplace()
	{
		if(!isMovement() || isMovementBlocked())
			findAndGoTo(new WalkableFloor(), new WorkplaceTarget(), -1);
		
		if(!isMovement() || isMovementFinished())
		{
			if(isMyWorkplaceNearby())
			{
				Loggers w = (Loggers) getWorkplace();
				w.onLumberjackReturn(hasLogs);
				dispose();
			}
		}
	}
	
	private boolean startChopTree()
	{
		byte treeDir = Direction2D.NONE;
		Vector2i dv = null;
		
		for(byte d = 0; d < 4; d++)
		{
			dv = Direction2D.vectors[d];
			if(isCompliantTree(getX() + dv.x, getY() + dv.y, mapRef)) {
				treeDir = d;
				break;
			}
		}
		
		if(treeDir != Direction2D.NONE)
		{
			setMovement(null);
			setDirection(treeDir);

			// Mark the cell
			getFrontCell().setUnitInfo(this.getID());
			
			ticksChoppingTree = secondsToTicks(TREE_CHOP_TIME);
			
			setState(CHOP_TREE);
			return true;
		}
		else
			return false;
	}
	
	private void stopChopTree()
	{
		if(getDirection() == Direction2D.NONE)
			return;
		getFrontCell().eraseUnitInfo();
	}

	@Override
	protected void renderUnit(Graphics gfx)
	{
		if(getState() == FIND_TREE)
			renderDefault(gfx, Content.sprites.unitLumberjack);
		else if(getState() == CHOP_TREE)
			renderDefault(gfx, Content.sprites.unitLumberjack, 4);
		else if(getState() == BACK_TO_WORKPLACE)
		{
			if(hasLogs)
			{
				// TODO render carried logs
				if(getDirection() == Direction2D.NORTH)
				{
					renderDefault(gfx, Content.sprites.unitLumberjack, 8);
					Resource.get(Resource.LOGS).renderCarriage(gfx, 0, 0, 100, getDirection());
				}
				else
				{
					Resource.get(Resource.LOGS).renderCarriage(gfx, 0, 0, 100, getDirection());
					renderDefault(gfx, Content.sprites.unitLumberjack, 8);
				}
			}
			else
				renderDefault(gfx, Content.sprites.unitLumberjack);
		}
	}

	@Override
	public String getDisplayableName()
	{
		return "Lumberjack";
	}
	
	@Override
	public String getInfoLine()
	{
		String line = super.getInfoLine();
		if(getState() == CHOP_TREE)
			line += " Chop chop chop !";
		else if(getState() == FIND_TREE)
			line += " Searching for a tree";
		return line;
	}

	public static boolean isCompliantTree(int x, int y, Map m)
	{
		if(!m.grid.contains(x, y))
			return false;
		MapCell cell = m.grid.getCellExisting(x, y);
		// Is tree and not chopped by another lumberjack
		return cell.nature == Nature.TREE && cell.getUnitID() == 0;
	}
	
	private class WalkableFloor implements IMapSpec
	{
		@Override
		public boolean canPass(int x, int y) {
			return mapRef.isWalkable(x, y);
		}
	}
	
	private class TreeTarget implements IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y) {
			return isCompliantTree(x, y, mapRef);
		}		
	}
	
	private class WorkplaceTarget implements IMapTarget
	{
		@Override
		public boolean isTarget(int x, int y) {
			return mapRef.grid.getBuildID(x, y) == getWorkplaceID();
		}
	}
	
}

