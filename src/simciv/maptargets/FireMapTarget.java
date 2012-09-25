package simciv.maptargets;

import java.util.List;

import simciv.Map;
import simciv.builds.Build;

public class FireMapTarget implements IExplicitMapTarget
{
	private static final long serialVersionUID = 1L;

	@Override
	public boolean evaluate(Map m, int x, int y)
	{
		List<Build> builds = m.getBuildsAround(x, y);
		for(Build b : builds)
		{
			if(b.isFireBurning())
				return true;
		}
		return false;
	}

}
