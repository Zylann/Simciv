package simciv;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

/**
 * Math utility methods and shortcuts
 * @author Marc
 *
 */
public class MathHelper
{
	/**
	 * Returns the value of f(x), where f is a slow-begin-slow-end curve equation :
	 * |       ..
	 * |     .
	 * |   .
	 * |..
	 * O---------> X
	 * @param x : value between 0 and 1
	 * @return f(x), between 0 and 1
	 */
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
	
	/**
	 * Computes the color mean of an image
	 * @param img
	 * @return
	 */
	public static Color mean(Image img)
	{
		Color m = new Color(0,0,0,255);
		
		int x, y;
		for(y = 0; y < img.getHeight(); y++)
		{
			for(x = 0; x < img.getWidth(); x++)
			{
				Color pix = img.getColor(x, y);
				m.r += pix.r;
				m.g += pix.g;
				m.b += pix.b;
			}
		}
		
		float d = img.getWidth() * img.getHeight();
		m.r /= d;
		m.g /= d;
		m.b /= d;
		
		return m;
	}
	
	/**
	 * Returns a random integer number between min and max
	 * @param min
	 * @param max
	 * @return
	 */
	public static int randInt(int min, int max)
	{
		return (int) ((float)(max - min) * Math.random() + (float)min);
	}
	
	/**
	 * Returns a random float number between -k and k
	 * @return
	 */
	public static float randS(float k)
	{
		return k * (float) (2.0 * Math.random() - 0.5);
	}

}




