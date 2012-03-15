package simciv;

/**
 * Represents a rectangular integer selection of the world,
 * defined by 2 points, min and max
 * @author Marc
 *
 */
public class IntRange2D
{
	public int minX;
	public int maxX;
	public int minY;
	public int maxY;
	
	public IntRange2D(int minX, int minY, int maxX, int maxY)
	{
		set(minX, minY, maxX, maxY);
	}
	
	public IntRange2D set(int minX, int minY, int maxX, int maxY)
	{
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		return this;
	}
	
	public IntRange2D divide(int d)
	{
		minX /= d;
		maxX /= d;
		minY /= d;
		maxY /= d;
		return this;
	}

	public int getWidth()
	{
		return maxX - minX;
	}
	
	public int getHeight()
	{
		return maxY - minY;
	}

	public IntRange2D multiply(int c)
	{
		minX *= c;
		maxX *= c;
		minY *= c;
		maxY *= c;
		return this;
	}

	public boolean contains(int x, int y)
	{
		return x >= minX && x <= maxX && y >= minY && y <= maxY;
	}
}
