package simciv;

public class MathHelper
{
    public static float smoothCurve(float x)
    {
        return (float) (6 * Math.pow(x,5) - 15 * Math.pow(x, 4) + 10 * Math.pow(x,3));
    }
    
    /**
     * Interpolates 2 values using the t ratio
     * @param x0 : min value
     * @param x1 : max value
     * @param t : ratio
     * @return
     */
    public static float linearInterpolation(float x0, float x1, float t)
    {
        return x0 + (x1 - x0) * t;
    }
    
    /**
     * Interpolates 4 2D values using ratios x and y
     * (Param descriptions are described as in a 2D space)
     * @param x0y0 : lower left
     * @param x1y0 : lower right
     * @param x0y1 : upper left
     * @param x1y1 : upper right
     * @param x : X-axis ratio
     * @param y : Y-axis ratio
     * @return
     */
    static float biLinearInterpolation(
			float x0y0, float x1y0,
			float x0y1, float x1y1,
			float x, float y)
    {
        float tx = smoothCurve(x);
        float ty = smoothCurve(y);

        float u = linearInterpolation(x0y0, x1y0, tx);
        float v = linearInterpolation(x0y1, x1y1, tx);

        return linearInterpolation(u, v, ty);
    }
    
	/**
	 * Subtracts a to x and set it to zero if it crosses zero.
	 * @param x : velocity
	 * @param a : reduction
	 * @return reduced parameter
	 */
	public static float diminishVelocity(float x, float a)
	{
		if(x > 0)
		{
			x -= a;
			if(x < 0)
				x = 0;
		}
		else if(x < 0)
		{
			x += a;
			if(x > 0)
				x = 0;
		}
		return x;
	}
}
