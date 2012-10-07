package simciv.builds;

import java.util.HashMap;

import org.newdawn.slick.SlickException;

import simciv.Map;

/**
 * Associates Building classes to strings and
 * allows constructing them from their names
 * 
 * @author Marc
 * 
 */
public class BuildFactory
{
	@SuppressWarnings("rawtypes")
	private static HashMap<String,Class> stringToClassMapping = new HashMap<String,Class>();
	@SuppressWarnings("rawtypes")
	private static HashMap<Class,String> classToStringMapping = new HashMap<Class,String>();
	
	static
	{
		// Important : string names must reflect building class names
		// TODO replace by the same system as Citizen (with IDs)
		
		addMapping(House.class, "House");
		addMapping(FarmLand.class, "FarmLand");
		addMapping(Warehouse.class, "Warehouse");
		addMapping(TaxmenOffice.class, "TaxmenOffice");
		addMapping(Market.class, "Market");
		addMapping(ArchitectOffice.class, "ArchitectOffice");
		addMapping(Ruins.class, "Ruins");
		addMapping(WaterSource.class, "WaterSource");
		addMapping(FireStation.class, "FireStation");
		addMapping(PoliceStation.class, "PoliceStation");
		addMapping(Hunters.class, "Hunters");
		addMapping(Loggers.class, "Loggers");
	}

	@SuppressWarnings("rawtypes")
	private static void addMapping(Class buildingClass, String name)
	{
		stringToClassMapping.put(name, buildingClass);
		classToStringMapping.put(buildingClass, name);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Build createFromName(String s, Map map) throws SlickException
	{
		Build b = null;
		try
		{
			Class buildingClass = (Class) stringToClassMapping.get(s);
			if(buildingClass != null)
			{
				b = (Build) buildingClass.getConstructor(new Class[]
				{ Map.class }).newInstance(new Object[]
				{ map });
			}
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
			throw new SlickException("Build class not found (" + s + ")");
		}
		return b;
	}
	
	public static String getBuildString(Build b)
	{
		return (String) classToStringMapping.get(b.getClass());
	}
	
}



