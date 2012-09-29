package simciv;

import java.io.Serializable;

import backend.ui.Notification;


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
	// TODO base the time model on the real calendar

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
	
	/** Real time counter in milliseconds **/
	private int time;
	
	/** Virtual day number **/
	private int day;
	
	/** Virtual month number **/
	private int month;
	
	/** Virtual year number **/
	private int year;
	
	/**
	 * Constructs a time initialized to the first day of the first month of year 0.
	 */
	public WorldTime()
	{
		day = 1;
	}
	
	/**
	 * Updates virtual game time from real time.
	 * @param delta : real time elapsed in milliseconds
	 * @param currentMap : the map currently played
	 */
	public void update(int delta, Map currentMap)
	{
		time += delta;
		
		int lastDay = day;
		day = (time / millisecondsPerDay) % DAYS_PER_MONTH + 1;
		
		if(day == 1 && lastDay != 1)
		{
			month++;
			if(month == MONTHS_PER_YEAR)
			{
				year++;
				month = 0;
				
				if(currentMap != null)
				{
					currentMap.sendNotification(
						Notification.TYPE_INFO, 
						"We are now in year " + getYear() + ".");
				}
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
		return day == 1 && month == 0;
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


