package simciv;

import java.io.Serializable;

/**
 * Maintains a virtual world time from real game time.
 * Note : this is not based on the real time model,
 * just because all months have the same number of days.
 * @author Marc
 *
 */
public class WorldTime implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final int millisecondsPerDay = 4000; // 1s = 6h
	private static final int MONTHS_PER_YEAR = 12;
	private static final int DAYS_PER_MONTH = 30;
	
	private static final String months[] = 
	{
		"January",
		"February",
		"March",
		"April",
		"May",
		"June",
		"July",
		"August",
		"September",
		"October",
		"November",
		"December"
	};
	
	private int time; // in real milliseconds
	private int day; // in virtual days
	private int month; // in virtual months
	private int year; // in virtual years
	private boolean isNewYearDay;
	
	public WorldTime()
	{
		day = 1;
	}
	
	public void update(int delta)
	{
		time += delta;
		
		int lastDay = day;
		day = (time / millisecondsPerDay) % DAYS_PER_MONTH + 1;
		isNewYearDay = false;
		
		if(day == 1 && lastDay != 1)
		{
			month++;
			if(month == MONTHS_PER_YEAR)
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
		return months[month] + " " + day + ", year " + year;
	}

	public boolean isFirstDayOfMonth()
	{
		return day == 1;
	}
	
}


