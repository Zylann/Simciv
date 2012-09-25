package backend;

import backend.geom.Vector2i;

// TODO replace by geom.Rectangle
/**
 * Represents a rectangular integer selection of the world,
 * defined by 2 points, min and max.
 * The minimal area is a 1x1 quad.
 * @author Marc
 *
 */
public class IntRange2D
{
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	
	public IntRange2D()
	{
	}
	
	public IntRange2D(IntRange2D other)
	{
		minX = other.minX;
		minY = other.minY;
		maxX = other.maxX;
		maxY = other.maxY;
	}

	public IntRange2D(int minX, int minY, int maxX, int maxY)
	{
		set(minX, minY, maxX, maxY);
	}
		
	public int minX()
	{
		return minX;
	}
	
	public int minY()
	{
		return minY;
	}
	
	public int maxX()
	{
		return maxX;
	}
	
	public int maxY()
	{
		return maxY;
	}
	
	public int getWidth()
	{
		return maxX - minX + 1;
	}
	
	public int getHeight()
	{
		return maxY - minY + 1;
	}
	
	public IntRange2D set(int minX, int minY, int maxX, int maxY)
	{
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		correct();
		return this;
	}
	
	private void correct()
	{
		if(minX > maxX)
		{
			int t = minX;
			minX = maxX;
			maxX = t;
		}
		if(minY > maxY)
		{
			int t = minY;
			minY = maxY;
			maxY = t;
		}
	}
	
	public IntRange2D set(Vector2i A, Vector2i B)
	{
		return set(A.x, A.y, B.x, B.y);
	}
	
	public IntRange2D divide(int d)
	{
		minX /= d;
		maxX /= d;
		minY /= d;
		maxY /= d;
		if(d < 0)
			correct();
		return this;
	}

	public IntRange2D multiply(int c)
	{
		minX *= c;
		maxX *= c;
		minY *= c;
		maxY *= c;
		if(c < 0)
			correct();
		return this;
	}

	public boolean contains(int x, int y)
	{
		return x >= minX && x <= maxX && y >= minY && y <= maxY;
	}

	public boolean intersects(int minX, int minY, int maxX, int maxY)
	{
        if(maxX < this.minX || minX > this.maxX)
            return false;
        return maxY >= this.minY && minY <= this.maxY;
	}
	
	public boolean intersects(IntRange2D r)
	{
		return intersects(r.minX, r.minY, r.maxX, r.maxY);
	}

	/**
	 * @return true if the area is 1.
	 */
	public boolean isUnit()
	{
		return minX == maxX && minY == maxY;
	}
	
	public int getArea()
	{
		return getWidth() * getHeight();
	}
	
	@Override
	public String toString()
	{
		return "(min=(" + minX + ", " + minY + "), max=(" + maxX + ", " + maxY + "))";
	}
	
}



