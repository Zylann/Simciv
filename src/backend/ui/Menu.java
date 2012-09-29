package backend.ui;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

/**
 * Vertical list of clickable buttons.
 * @author Marc
 *
 */
public class Menu extends WidgetContainer
{
	private List<IActionListener> actionListeners;
	
	public Menu(WidgetContainer parent, int x, int y, int width)
	{
		super(parent, x, y, width, 0);
		actionListeners = new ArrayList<IActionListener>();
	}

	/**
	 * Adds a child to the container.
	 * Note : better use add(MenuItem, IActionListener)
	 * @param child: widget of class MenuItem
	 */
	@Override @Deprecated
	public void add(Widget child)
	{
		if(!MenuItem.class.isInstance(child)) {
			Log.error("Menu : Cannot add a child which is not a MenuItem.");
		}
		add((MenuItem)child, null);
	}

	/**
	 * Adds a new menu item with a custom action.
	 * @param item : MenuItem
	 * @param actionListener : action to execute. Not set if null.
	 * @return Menu object for chaining.
	 * @throws SlickException
	 */
	public Menu add(MenuItem item, IActionListener actionListener)
	{
		if(actionListener != null)
			item.addActionListener(actionListener);

		item.posX = 0;
		item.posY = this.height;
		item.width = this.width;
		this.height += item.height;
		
		super.add(item);

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
	
	/**
	 * Called when a menu item is selected.
	 * @param item
	 */
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
