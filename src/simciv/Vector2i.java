package simciv;

/**
 * A simple class for handling 2D integer coordinates
 * @author Marc
 *
 */
public class Vector2i
{
	public int x;
	public int y;
	
	public Vector2i()
	{
		x = 0;
		y = 0;
	}
	
	public Vector2i(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public void set(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public void set(Vector2i v)
	{
		this.x = v.x;
		this.y = v.y;
	}

	public void multiply(int s)
	{
		this.x *= s;
		this.y *= s;
	}
	
    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Vector2i))
        {
            return false;
        }
        else
        {
            Vector2i vec = (Vector2i)obj;
            return x == vec.x && y == vec.y;
        }
    }
    
    public int manhattanDistanceFrom(Vector2i other)
    {
    	return Math.abs(x - other.x) + Math.abs(y - other.y);
    }
    
    @Override
    public int hashCode()
    {
        return x | (y << 16);
    }

	public boolean equals(Vector2i other)
	{
		return x == other.x && y == other.y;
	}
	
	@Override
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}

	public boolean equals(int x, int y)
	{
		return this.x == x && this.y == y;
	}
}

