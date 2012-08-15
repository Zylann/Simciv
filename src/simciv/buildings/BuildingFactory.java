package simciv.buildings;

import java.util.HashMap;

import org.newdawn.slick.SlickException;

import simciv.World;

/**
 * Associates Building classes to strings and
 * allows constructing them from their names
 * 
 * @author Marc
 * 
 */
public class BuildingFactory
{
	@SuppressWarnings("rawtypes")
	private static HashMap<String,Class> stringToClassMapping = new HashMap<String,Class>();
	@SuppressWarnings("rawtypes")
	private static HashMap<Class,String> classToStringMapping = new HashMap<Class,String>();
	
	static
	{
		// Important : string names must reflect building class names
		
		addMapping(House.class, "House");
		addMapping(FarmLand.class, "FarmLand");
		addMapping(Warehouse.class, "Warehouse");
		addMapping(TaxmenOffice.class, "TaxmenOffice");
		addMapping(Market.class, "Market");
		addMapping(ArchitectOffice.class, "ArchitectOffice");
	}

	@SuppressWarnings("rawtypes")
	private static void addMapping(Class buildingClass, String name)
	{
		stringToClassMapping.put(name, buildingClass);
		classToStringMapping.put(buildingClass, name);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Building createFromName(String s, World world) throws SlickException
	{
		Building b = null;
		try
		{
			Class buildingClass = (Class) stringToClassMapping.get(s);
			if(buildingClass != null)
			{
				b = (Building) buildingClass.getConstructor(new Class[]
				{ World.class }).newInstance(new Object[]
				{ world });
			}
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
			throw new SlickException("Build class not found (" + s + ")");
		}
		return b;
	}
	
	public static String getBuildingString(Building b)
	{
		return (String) classToStringMapping.get(b.getClass());
	}
	
}



