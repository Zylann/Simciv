package simciv.ui.base;

import java.util.ArrayList;
import java.util.List;

public class ToolButtonGroup
{
	private List<ToolButton> toolButtons;
	
	public ToolButtonGroup()
	{
		toolButtons = new ArrayList<ToolButton>();
	}
	
	public void add(ToolButton tb)
	{
		toolButtons.add(tb);
	}
	
	/**
	 * Unselects all buttons except the one given as parameter
	 * @param exceptToolButton
	 */
	public void unselectAllExcept(ToolButton exceptToolButton)
	{
		for(ToolButton tb : toolButtons)
		{
			if(tb != exceptToolButton)
				tb.select(false);
		}
	}

	public void unselectAll()
	{
		for(ToolButton tb : toolButtons)
			tb.select(false);
	}
}
