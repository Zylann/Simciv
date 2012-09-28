package simciv.builds;

import java.util.ArrayList;

public class ProblemsReport
{
	// Problem severity
	public static final byte MINOR = 0;
	public static final byte SEVERE = 1;
	private static final byte COUNT = 2;
	
	private ArrayList<String> problems[];
	
	@SuppressWarnings("unchecked")
	public ProblemsReport()
	{
		problems = new ArrayList[COUNT];
		for(int i = 0; i < problems.length; i++)
		{
			problems[0] = new ArrayList<String>();
		}
	}
	
	public void add(byte severity, String message)
	{
		problems[severity].add(message);
	}
	
	public ArrayList<String> getList(byte severity)
	{
		return problems[severity];
	}
	
}



