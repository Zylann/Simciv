package simciv;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import simciv.units.Fireman;
import simciv.units.Unit;

import backend.geom.Vector2i;
import backend.pathfinding.IMapSpec;
import backend.pathfinding.IMapTarget;

/**
 * Fire alerts manager
 * @author Marc
 *
 */
public class FireAlerts
{
	private static final int TIME_PER_ENV_ALERT = 500;
	private static final int ALERT_RANGE = 32;
	
	/** All of the referenced fires on the map **/
	private HashSet<Vector2i> fires;
		
	/** Time before next tick **/
	private int timeBeforeNextEnvAlert;
	
	public FireAlerts()
	{
		fires = new HashSet<Vector2i>();
	}
	
	/**
	 * Registers a fire.
	 * @param x : fire pos X in cells
	 * @param y : fire pos Y in cells
	 */
	public void registerFire(int x, int y)
	{
		fires.add(new Vector2i(x, y));
	}
	
	/**
	 * Unegisters a fire (if extinguished for ex.).
	 * @param x : fire pos X in cells
	 * @param y : fire pos Y in cells
	 */
	public void unregisterFire(int x, int y)
	{
		Vector2i p = new Vector2i(x, y);
		fires.remove(p);
	}
	
	/**
	 * Get the total count of fires on the map
	 * @return
	 */
	public int getNbFires()
	{
		return fires.size();
	}
	
	public void update(Map map, int delta)
	{
		timeBeforeNextEnvAlert -= delta;
		if(timeBeforeNextEnvAlert < 0)
		{
			timeBeforeNextEnvAlert = TIME_PER_ENV_ALERT;
			alertEnvironnment(map);
		}
	}
	
	/**
	 * Searches firemen and paths linking them to fires.
	 * Then tells them to fight these fires.
	 * @param map
	 */
	public void alertEnvironnment(Map map)
	{	
		if(getNbFires() == 0)
			return;
		
		// TODO count firemen
		
		// Prepare start positions for pathfinding
		ArrayList<Vector2i> seeds = new ArrayList<Vector2i>();		
		for(Vector2i p : fires)
			seeds.add(new Vector2i(p));
		
		// Configure pathfinding
		map.multiPathFinder.setMaxDistance(ALERT_RANGE);
		map.multiPathFinder.setMaxPaths(getNbFires() / Fireman.WATER_CHARGE_MAX + 1);
		
		// Find paths
		// Note : this is fires=>firemen because fires are grouped and firemen dispersed.
		// Starting from firemen would be more expensive.
		// Note 2 : true is for reversing paths.
		MapSpec specs = new MapSpec(map);
		List<LinkedList<Vector2i>> paths =
			map.multiPathFinder.findPaths(seeds, specs, specs, true);
		
		// Tell firemen to fight fires
		if(paths != null)
		{
			// For each path linking a fire and a fireman
			for(LinkedList<Vector2i> path : paths)
			{
				// Get fireman pos
				Vector2i firemanPos = path.getFirst();
				// According to the internal MapSpec, it MUST be a fireman
				Fireman f = (Fireman)(map.getUnit(firemanPos.x, firemanPos.y));								
				// Send the alert with the path to the fireman
				f.onFireAlert(path);
			}
		}
	}
	
	/**
	 * Pathfinding specifications for fire alerts
	 * @author Marc
	 *
	 */
	private class MapSpec implements IMapSpec, IMapTarget
	{
		Map mapRef;
		
		public MapSpec(Map m) {
			mapRef = m;
		}

		@Override
		public boolean canPass(int x, int y) {
			// Firemen can walk out of roads when on mission
			return mapRef.isWalkable(x, y);
			//return mapRef.grid.isRoad(x, y);
		}

		@Override
		public boolean isTarget(int x, int y)
		{
			Unit u = mapRef.getUnit(x, y);
			if(Fireman.class.isInstance(u))
				return ((Fireman)u).isReadyForMission();
			return false;
		}
		
	}

}










