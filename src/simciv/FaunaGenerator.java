package simciv;

import java.util.ArrayList;

import simciv.units.Duck;

import backend.Noise;
import backend.geom.Vector2i;

/**
 * Spawns groups of animals on the map.
 * The result totally depends on the map and the given seed.
 * This generator splits the map into sectors to determine complying areas for animals.
 * Some of these sectors are then used to spawn animals (depends on the seed which ones are choosen).
 * Sector sizes also defines the scattering wideness of each group.
 * @author Marc
 *
 */
public class FaunaGenerator
{
	private int sectorSize;
	private float spawnFrequency;
	
	/**
	 * Constructs a fauna generator.
	 * @param sectorSize : sector size, usually 8.
	 * @param spawnFrequency : probability to spawn animals on a complying sector
	 * (the pseudo-random is computed using the seed).
	 */
	public FaunaGenerator(int sectorSize, float spawnFrequency)
	{
		this.sectorSize = sectorSize;
		this.spawnFrequency = spawnFrequency;
	}
	
	/**
	 * Executes the generator.
	 * @param map : map where the animals will be spawned
	 * @param seed
	 */
	public void generateFauna(Map map, int seed)
	{
		ArrayList<Vector2i> sectors = findCompliantSectors(map);

		if(sectors.isEmpty())
			return;
		
		int i = 0;
		for(Vector2i sectorPos : sectors)
		{
			if(Noise.getf(i, i, seed) < spawnFrequency)
			{
				// How many units to spawn
				int nbUnits = 2 + (int)(10.f * Noise.getf(i, i, seed + 1));

				// Spawn units if possible
				for(int j = 0; j < nbUnits; j++)
				{
					int x = sectorPos.x + (int)(sectorSize * Noise.getf(i, i, seed + j));
					int y = sectorPos.y + (int)(sectorSize * Noise.getf(i, i, seed + j));
					
					if(map.isWalkable(x, y))
						map.spawnUnit(new Duck(map), x, y);
				}
			}
			i++;
		}
	}
	
	private ArrayList<Vector2i> findCompliantSectors(Map map)
	{
		ArrayList<Vector2i> compliantSectors = new ArrayList<Vector2i>();
		
		for(int y = 0; y < map.grid.getHeight(); y += sectorSize) {
			for(int x = 0; x < map.grid.getWidth(); x += sectorSize) {
				if(evaluateSector(map, x, y)) {
					Vector2i s = new Vector2i(x, y);
					compliantSectors.add(s);
				}
			}
		}
		
		return compliantSectors;
	}
	
	private boolean evaluateSector(Map map, int sectorX, int sectorY)
	{
		int compliantCells = 0;
		
		for(int y = sectorY; y < sectorY + sectorSize; y++) {
			for(int x = sectorX; x < sectorX + sectorSize; x++) {
				if(map.isWalkable(x, y)) {
					if(map.grid.getTerrain(x, y).getID() == Terrain.GRASS)
						compliantCells++;
				}
			}
		}
		
		return compliantCells >= 3 * (sectorSize * sectorSize) / 4;
	}

}




