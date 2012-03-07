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
    	double n;
    	
    	for(y = 0; y < map.getHeight(); y++)
    	{
    		for(x = 0; x < map.getWidth(); x++)
    		{
    			n = Noise.getPerlin((float)x, (float)y, seed, 2, 0.5f, 16.f);
    			if(n > 0.5)
    				map.setTerrain(x, y, Terrain.GRASS);
    			else
    				map.setTerrain(x, y, Terrain.WATER);
    		}
    	}
    }
}
