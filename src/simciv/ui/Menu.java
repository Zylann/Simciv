package simciv.ui;

import org.newdawn.slick.SlickException;

public class Menu extends WidgetContainer
{
	IActionListener nullActionListener;
	
	public Menu(WidgetContainer parent, int x, int y, int width)
	{
		super(parent, x, y, width, 0);
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
		
		child.x = this.x;
		child.y = this.height;
		child.width = this.width;
		this.height += child.height;
		
		super.add(child);
	}
	
	public void add(MenuItem item, IActionListener actionListener) throws SlickException
	{
		item.setActionListener(actionListener);
		add(item);
	}
	
	/**
	 * Sets action performed if the menu disappears with no item being selected
	 */
	public void setNullActionListener(IActionListener listener)
	{
		nullActionListener = listener;
	}

	@Override
	public boolean mousePressed(int button, int x, int y)
	{
		if(!super.mousePressed(button, x, y))
		{
			if(!contains(x, y))
			{
				if(nullActionListener != null)
					nullActionListener.actionPerformed();
				setVisible(false);
				return false;
			}
			else
				return true;
		}
		return false;
	}	

}
