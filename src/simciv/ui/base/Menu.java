package simciv.ui.base;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.SlickException;

public class Menu extends WidgetContainer
{
	List<IActionListener> actionListeners;
	
	public Menu(Widget parent, int x, int y, int width)
	{
		super(parent, x, y, width, 0);
		actionListeners = new ArrayList<IActionListener>();
	}

	/**
	 * Adds an item to the menu.
	 * @param child: widget of class MenuItem
	 */
	@Override
	public void add(Widget child) throws SlickException
	{
		if(!MenuItem.class.isInstance(child))
			throw new SlickException("Menu : Cannot add a child which is not a MenuItem.");
		
		child.posX = 0;
		child.posY = this.height;
		child.width = this.width;
		this.height += child.height;
		
		super.add(child);
	}

	public Menu add(MenuItem item, IActionListener actionListener) throws SlickException
	{
		item.addActionListener(actionListener);
		add(item);
		return this;
	}
	
	/**
	 * Sets action performed when any item is selected.
	 * (see also the action listener in MenuItem)
	 * @return the object itself for chaining
	 */
	public Menu addActionListener(IActionListener listener)
	{
		actionListeners.add(listener);
		return this;
	}
	
	public void onItemSelect(MenuItem item)
	{
		setVisible(false);
		for(IActionListener l : actionListeners)
			l.actionPerformed(item);
	}

	@Override
	public boolean mousePressed(int button, int x, int y)
	{
		super.mousePressed(button, x, y);
		if(isVisible() && !contains(x, y))
			onItemSelect(null);
		return true;
	}

}
