package simciv;

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

	public void multiply(int s)
	{
		this.x *= s;
		this.y *= s;
	}

	public void set(Vector2i v)
	{
		this.x = v.x;
		this.y = v.y;
	}

	public boolean equals(Vector2i other)
	{
		return x == other.x && y == other.y;
	}
}

