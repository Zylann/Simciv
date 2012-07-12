package simciv;

public class MapGenerator
{	
	int seed;
	
	public MapGenerator(int seed)
	{
		this.seed = seed;
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
    			n = Noise.getPerlin((float)x, (float)y, seed, 3, 0.5f, 32.f);
    			n3 = Noise.getf(x, y, seed+1);
    			
				c.nature = 0;
				
    			if(n > 0.5)
    			{
        			n2 = Noise.getPerlin((float)x, (float)y, seed+1, 4, 0.3f, 16.f);
        			
    				if(n2 < 0.4 && n3 < 0.7)
    					c.nature = Nature.TREE;
    				else if(n2 < 0.45 && n3 < 0.1)
    					c.nature = Nature.TREE;
    				
    				c.terrainID = Terrain.GRASS;
    			}
    			else
    				c.terrainID = Terrain.WATER;
    			
    			c.noise = (byte)(255.f*n3);
    			
    			map.getCellExisting(x, y).set(c);
    		}
    	}
    }
    
}
