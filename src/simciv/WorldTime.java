package simciv;

import java.io.Serializable;

/**
 * Maintains a virtual world time from real game time
 * @author Marc
 *
 */
public class WorldTime implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final int millisecondsPerDay = 4000; // 1s = 6h
	
	private int time; // in real milliseconds
	private int day; // in virtual days
	private int month; // in virtual months
	private int year; // in virtual years
	private boolean isNewYearDay;
	
	public WorldTime()
	{
	}
	
	public void update(int delta)
	{
		time += delta;
		
		int lastDay = day;
		day = (time / millisecondsPerDay) % 30;
		isNewYearDay = false;
		
		if(day == 0 && lastDay != 0)
		{
			month++;
			if(month == 12)
			{
				year++;
				month = 0;
				isNewYearDay = true;
			}
		}
	}
	
	public int getDay()
	{
		return day;
	}
	
	public int getMonth()
	{
		return month;
	}
	
	public int getYear()
	{
		return year;
	}
	
	public float getMonthProgressRatio()
	{
		return (float)day / 30.f;
	}
	
	public boolean isNewYearDay()
	{
		return isNewYearDay;
	}
	
	@Override
	public String toString()
	{
		return "day " + day + ", month " + month + ", year " + year;
	}

	public boolean isFirstDayOfMonth()
	{
		return day == 0;
	}
	
}


