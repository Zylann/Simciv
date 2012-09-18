package simciv.maptargets;

import java.util.List;

import simciv.Map;
import simciv.builds.Build;
import simciv.builds.WaterSource;

public class WaterSourceMapTarget implements IMapTarget
{
	private static final long serialVersionUID = 1L;

	@Override
	public boolean evaluate(Map m, int x, int y)
	{
		List<Build> list = m.getBuildsAround(x, y);
		for(Build b : list)
		{
			if(WaterSource.class.isInstance(b))
			{
				if(((WaterSource)b).getState() == Build.STATE_ACTIVE)
					return true;
			}
		}
		return false;
	}

}
