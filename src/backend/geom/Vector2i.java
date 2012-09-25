package backend.geom;

import java.io.Serializable;

import backend.Direction2D;

/**
 * A simple class for handling 2D integer coordinates
 * @author Marc
 *
 */
public class Vector2i implements Serializable
{
	private static final long serialVersionUID = 1L;
	
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
	
	public Vector2i(Vector2i other)
	{
		this.x = other.x;
		this.y = other.y;
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
	
	public void divide(int d)
	{
		this.x /= d;
		this.y /= d;
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
        
    public static float length(int x, int y)
    {
    	return (float) Math.sqrt(x * x + y * y);
    }
    
    public float length()
    {
    	return length(x, y);
    }

    public float distanceFrom(Vector2i b)
	{
		return length(x - b.x, y - b.y);
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

	public void addDirection(byte d)
	{
		switch(d)
		{
		case Direction2D.WEST : x--; break;
		case Direction2D.EAST : x++; break;
		case Direction2D.NORTH : y--; break;
		case Direction2D.SOUTH : y++; break;
		default : break;
		}
	}

}

