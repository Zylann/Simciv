package simciv;

/**
 * A class for generating different kinds of noise (random numbers)
 * depending on a seed and various parameters.
 * @author Marc
 *
 */
public class Noise
{
	// These values have been set at random.
	private static final int RAND_SEQ_X = 72699;
	private static final int RAND_SEQ_Y = 31976;
	private static final int RAND_SEQ_SEED = 561;
	private static final int RAND_SEQ1 = 11126;
	private static final int RAND_SEQ2 = 98756;
	private static final int RAND_SEQ3 = 423005601;
	
	/**
	 * Generates a random value between 0 and maxint.
	 * The same parameters will return the same value.
	 * @param x : X position
	 * @param y : Y position
	 * @param seed : random seed
	 * @return value between 0 and maxint.
	 */
    public static int get(int x, int y, int seed)
    {
        int n = RAND_SEQ_X * x + RAND_SEQ_Y * y + RAND_SEQ_SEED * seed;
        n &= 0x7fffffff;
        n = (n >> 13) ^ n;
        n = n * (n * n * RAND_SEQ1 + RAND_SEQ2) + RAND_SEQ3;
        n &= 0x7fffffff;
        return n;
    }
    
    /**
     * Uses get to generate a random float value between 0 and 1.
     * The same parameters will return the same value.
     * @param x
     * @param y
     * @param seed
     * @return
     */
    public static float getf(int x, int y, int seed)
    {
    	return (float)get(x, y, seed) / 0x7fffffff;
    }
    
    static float getGradient(float x, float y, int seed)
    {
        // Calculate the integer coordinates
        int x0 = (x > 0.0 ? (int)x : (int)x - 1);
        int y0 = (y > 0.0 ? (int)y : (int)y - 1);
        
        // Calculate the remaining part of the coordinates
        float xl = x - (float)x0;
        float yl = y - (float)y0;
        
        // Get values for corners of square
        float v00 = getf(x0, y0, seed);
        float v10 = getf(x0+1, y0, seed);
        float v01 = getf(x0, y0+1, seed);
        float v11 = getf(x0+1, y0+1, seed);
        
        // Interpolate
        return MathHelper.biLinearInterpolation(v00, v10, v01, v11, xl, yl);
    }
    
    /**
     * Generates Perlin noise
     * @param x : x coordinate
     * @param y : y coordinate
     * @param seed : random seed
     * @param octaves : noise details
     * @param persistence : between 0 and 1, usually 0.5
     * @param period : patterns wideness
     * @return noise value in [0, 1]
     */
    public static float getPerlin(
            float x, float y, int seed,
            int octaves, float persistence,
            float period)
    {
        if(octaves < 1)
            return 0;

        x /= period;
        y /= period;

        float noise = 0; // noise
        float f = 1.0f;
        float amp = 1.0f; // amplitude of an octave
        float ampMax = 0; // total amplitude

        for(int i = 0; i < octaves; i++)
        {
            noise += amp * getGradient(x*f, y*f, seed+i);
            ampMax += amp;
            f *= 2.0;
            amp *= persistence; // reduce next amplitude
        }

        return noise / ampMax;
    }
}
