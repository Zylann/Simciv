package backend;

import java.util.Arrays;

import backend.geom.Vector2i;

/**
 * Simple byte matrix wrapping linearly stored data
 * @author Marc
 *
 */
public class ByteArray2D
{
	private byte data[];
	private int width;
	private int height;
	
	public ByteArray2D(int w, int h)
	{
		width = w;
		height = h;
		data = new byte[width * height];
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public byte get(int x, int y)
	{
		return data[y * width + x];
	}
	
	public byte get(Vector2i p)
	{
		return get(p.x, p.y);
	}
	
	public void set(int x, int y, byte v)
	{
		data[y * width + x] = v;
	}
	
	public void set(Vector2i p, byte v)
	{
		set(p.x, p.y, v);
	}
	
	public void fill(byte v)
	{
		Arrays.fill(data, v);
	}
	
	public boolean contains(int x, int y)
	{
		if(x < 0)
			return false;
		if(x >= width)
			return false;
		if(y < 0)
			return false;
		if(y >= height)
			return false;
		return true;
	}

	public boolean contains(Vector2i p)
	{
		return contains(p.x, p.y);
	}
	
	public String toString()
	{
		return "ByteArray(" + width + "x" + height + ")";
	}
	
}

