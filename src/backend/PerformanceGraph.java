package backend;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class PerformanceGraph
{
	public static final int WIDTH = 100;
	
	private float values[];
	private int index;
	private float drawMinY;
	private float drawMaxY;
	
	public PerformanceGraph(float vminY, float vmaxY)
	{
		values = new float[WIDTH];
		setDrawValuesBounds(vminY, vmaxY);
	}
		
	public void pushNextValue(float nextValue)
	{
		values[index] = nextValue;
		index++;
		if(index == values.length)
			index = 0;
	}
	
	public void setDrawValuesBounds(float min, float max)
	{
		drawMinY = min;
		drawMaxY = max;
	}
	
	protected void renderValue(Graphics gfx, int i, int h)
	{
		float barY = (float)h * (values[i] - drawMinY) / (drawMaxY - drawMinY);
		
		if(barY < 0)
			barY = 0;
		if(barY > h)
			barY = h;
		
		gfx.drawLine(i, h, i, h - values[i]);
	}
	
	public void render(Graphics gfx, int x, int y, int h)
	{
		gfx.pushTransform();
		gfx.translate(x, y);
		gfx.setLineWidth(1);
		
		Color barColor = new Color(0, 0, 0);
		float k = 1.f / (float)(values.length);
		
		int i = index;
		
		for(; i < values.length; i++)
		{
			gfx.setColor(barColor);
			renderValue(gfx, i, h);
			barColor.r += k;
			barColor.g += k;
			barColor.b += k;
		}
		
		i = 0;
		
		for(; i < index; i++)
		{
			gfx.setColor(barColor);
			renderValue(gfx, i, h);
			barColor.r += k;
			barColor.g += k;
			barColor.b += k;
		}
		
		gfx.setColor(Color.white);
		gfx.drawRect(0, 0, values.length, h);

		gfx.popTransform();
	}
	
}


