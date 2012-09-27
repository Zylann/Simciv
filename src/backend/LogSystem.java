package backend;

import java.util.Date;

/**
 * Slick-based log system that supports console echo
 * @author Marc
 *
 */
public class LogSystem extends org.newdawn.slick.util.DefaultLogSystem
{
	public static boolean consoleEcho = false;
	
	@Override
	public void error(Throwable e)
	{
		super.error(e);
		if(consoleEcho)
			System.out.println(new Date() + " ERROR: " + e.getMessage());
	}

	@Override
	public void error(String message)
	{
		super.error(message);
		if(consoleEcho)
			System.out.println(new Date() + " ERROR: " + message);
	}

	@Override
	public void warn(String message)
	{
		super.warn(message);
		if(consoleEcho)
			System.out.println(new Date() + " WARN: " + message);
	}

	@Override
	public void warn(String message, Throwable e)
	{
		super.warn(message, e);
		if(consoleEcho)
			e.printStackTrace(System.out);
	}

	@Override
	public void info(String message)
	{
		super.info(message);
		if(consoleEcho)
			System.out.println(new Date() + " INFO: " + message);
	}

	@Override
	public void debug(String message)
	{
		super.debug(message);
		if(consoleEcho)
			System.out.println(new Date() + " DEBUG: " + message);
	}
	
}

