package simciv.builds;

import java.util.ArrayList;

public class BuildReport
{
	// Message type
	public static final byte INFO = 0;
	public static final byte PROBLEM_MINOR = 1;
	public static final byte PROBLEM_MAJOR = 2;
	private static final byte TCOUNT = 3;
	
	private ArrayList<String> problems[];
	
	@SuppressWarnings("unchecked")
	public BuildReport()
	{
		problems = new ArrayList[TCOUNT];
		for(int i = 0; i < problems.length; i++)
			problems[i] = new ArrayList<String>();
	}
	
	public void add(byte type, String message)
	{
		problems[type].add(message);
	}
	
	public ArrayList<String> getList(byte severity)
	{
		return problems[severity];
	}

	public boolean isEmpty()
	{
		for(int i = 0; i < problems.length; i++)
		{
			if(!problems[i].isEmpty())
				return false;
		}
		return true;
	}
	
}



