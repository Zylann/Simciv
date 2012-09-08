package simciv;

import java.io.Serializable;

public abstract class City implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int ID;
	protected String name;
	
	public City()
	{
		ID = GameComponent.makeUniqueID();
		name = "<City>";
	}
	
	public int getID()
	{
		return ID;
	}
	
	public String getName()
	{
		return name;
	}
	
}

