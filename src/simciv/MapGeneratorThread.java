package simciv;

public class MapGeneratorThread extends Thread
{
	private int seed;
	private Map mapRef;
	private int progress;
	private boolean finished;
	
	public MapGeneratorThread(int seed, Map map)
	{
		this.seed = seed;
		finished = false;
		mapRef = map;
	}
	
	public boolean isFinished()
	{
		// Note : not synchronized because a boolean is atomic
		return finished;
	}
	
	public int getProgress()
	{
		// Note : should be synchronized, but progress is read-only and use locks would slow the process a bit
		return progress;
	}
	
    @Override
	public void run() // Note : to execute the thread, use start().
	{
    	generateTerrain(mapRef.grid);
	}

	public void generateTerrain(MapGrid grid)
    {
    	int x, y;
    	double heightNoise, forestNoise, localNoise, bushNoise;
		MapCell c = new MapCell();
		
    	for(y = 0; y < grid.getHeight(); y++)
    	{
    		for(x = 0; x < grid.getWidth(); x++)
    		{
    			heightNoise = Noise.getPerlin((float)x, (float)y, seed, 4, 0.5f, 64.f);
    			localNoise = Noise.getf(x, y, seed+1);
    			
				c.nature = 0;
    			c.noise = (byte)(255.f*localNoise);
				
    			if(heightNoise > 0.5)
    			{
    				// Above sea-level
        			forestNoise = Noise.getPerlin((float)x, (float)y, seed+1, 5, 0.5f, 24.f);
        			
        			if(heightNoise < 0.6 && forestNoise > 0.5)
        			{
        				// Beach
        				c.terrainID = Terrain.DUST;
        			}
        			else
        			{
        				// Land
        				c.terrainID = Terrain.GRASS;
        				
        				if(forestNoise < 0.4)
        				{
        					// Forest
        					if(localNoise < 0.7)
            					c.nature = Nature.TREE;
        					else if(localNoise < 0.85)
        						c.nature = Nature.BUSH;
        				}
        				else if(forestNoise < 0.45)
        				{
        					// Forest edge
        					if(localNoise < 0.1)
        						c.nature = Nature.TREE;
        					else if(localNoise < 0.2)
        						c.nature = Nature.BUSH;
        				}
        				
    					// Bush group
    					bushNoise = Noise.getPerlin(x, y, seed+2, 5, 0.5f, 24.f);
    					if(bushNoise < 0.3 && localNoise < 0.9 && c.nature == Nature.NONE)
    					{
    						c.nature = Nature.BUSH;
    						if(localNoise < 0.88)
    							c.noise &= 0x01;
    					}
           			}        			
    			}
    			else
    				c.terrainID = Terrain.WATER; // sea
    			    			    			
    			grid.getCellExisting(x, y).set(c);
    		}
    		
    		// Update generation progress
    		progress = (int) (100 * (float)y / (float)grid.getHeight());
    	}
    	
    	finished = true;
    }
    
}
