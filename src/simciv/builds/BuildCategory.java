package simciv.builds;

import org.newdawn.slick.Color;

public class BuildCategory
{
	public static final byte HOUSES = 0;
	public static final byte FOOD = 1;
	public static final byte INDUSTRY = 2;
	public static final byte ADMINISTRATION = 3;
	public static final byte TRADE = 4;
	public static final byte RUINS = 5;
	public static final byte COUNT = 6;
	
	private static BuildCategory categories[];
	
	private byte ID;
	private String name;
	private Color color;
	
	private BuildCategory(byte ID, String name, Color color)
	{
		this.ID = ID;
		this.name = name;
		this.color = color;		
	}
	
	public static void initialize()
	{
		categories = new BuildCategory[COUNT];
		
		set(new BuildCategory(HOUSES, "Houses", new Color(255, 192, 128)));
		set(new BuildCategory(FOOD, "Food", new Color(64, 224, 0)));
		set(new BuildCategory(INDUSTRY, "Industry", new Color(128, 128, 128)));
		set(new BuildCategory(ADMINISTRATION, "Administration", new Color(64, 128, 255)));
		set(new BuildCategory(TRADE, "Marketing", new Color(255, 128, 128)));
		set(new BuildCategory(RUINS, "Ruins", new Color(64, 64, 64)));
		
	}
	
	private static void set(BuildCategory bc)
	{
		categories[bc.getID()] = bc;
	}
	
	public static BuildCategory get(byte ID)
	{
		return categories[ID];
	}
	
	public byte getID()
	{
		return ID;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Color getColor()
	{
		return color;
	}
	
}


