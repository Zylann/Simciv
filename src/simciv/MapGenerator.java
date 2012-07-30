package simciv;

public class MapGenerator extends Thread
{
	private int seed;
	private Map mapRef;
	private int progress;
	private boolean finished;
	
	public MapGenerator(int seed, Map map)
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
    	
    	generate(mapRef);
	}

	public void generate(Map map)
    {
    	int x, y;
    	double n, n2, n3;
		MapCell c = new MapCell();
		
    	for(y = 0; y < map.getHeight(); y++)
    	{
    		for(x = 0; x < map.getWidth(); x++)
    		{
    			n = Noise.getPerlin((float)x, (float)y, seed, 4, 0.5f, 64.f); // height noise
    			n3 = Noise.getf(x, y, seed+1); // local noise
    			
				c.nature = 0;
    			c.noise = (byte)(255.f*n3);
				
    			if(n > 0.5)
    			{
        			n2 = Noise.getPerlin((float)x, (float)y, seed+1, 5, 0.5f, 24.f); // forest noise
        			
        			if(n < 0.6 && n2 > 0.5)
        			{
        				// Beach
        				c.terrainID = Terrain.DUST;
        			}
        			else
        			{
        				// Land
        				
        				c.terrainID = Terrain.GRASS;
        				
        				if(n2 < 0.4)
        				{
        					// Forest
        					if(n3 < 0.7)
            					c.nature = Nature.TREE;
        					else if(n3 < 0.85)
        						c.nature = Nature.BUSH;
        				}
        				else if(n2 < 0.45)
        				{
        					// Forest edge
        					if(n3 < 0.1)
        						c.nature = Nature.TREE;
        					else if(n3 < 0.2)
        						c.nature = Nature.BUSH;
        				}
        				else
        				{
        					// Bush group
        					double n4 = Noise.getPerlin(x, y, seed+2, 5, 0.5f, 24.f);
        					if(n4 < 0.3 && n3 < 0.9)
        					{
        						c.nature = Nature.BUSH;
        						if(n3 < 0.88)
        							c.noise &= 0x01;
        					}
        				}
           			}        			
    			}
    			else
    				c.terrainID = Terrain.WATER; // ocean
    			    			
    			map.getCellExisting(x, y).set(c);
    		}
    		
    		progress = (int) (100 * (float)y / (float)map.getHeight());
    	}
    	
    	finished = true;
    }
    
}
