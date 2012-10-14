package backend.ui;

import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

/**
 * Defines the visual appearance of UI components.
 * @author Marc
 *
 */
public interface ITheme
{
	/**
	 * Get the global font used by this theme.
	 * If null, the default font will be used.
	 * @return
	 */
	public Font getFont();
	
	/*
	 * All of the methods below draw one type of widget.
	 */
	
	public void renderPanel(Graphics gfx, Panel w);	
	public void renderWindow(Graphics gfx, Window w);	
	public void renderWindowTitleBar(Graphics gfx, WindowTitleBar w);
	public void renderWindowCloseButton(Graphics gfx, Button w);
	public void renderMenuItem(Graphics gfx, MenuItem w);
	public void renderToolButton(Graphics gfx, ToolButton w);
	public void renderPushButton(Graphics gfx, PushButton w);
	public void renderProgressBar(Graphics gfx, ProgressBar w);	
	public void renderLabel(Graphics gfx, Label label);
	public void renderMenuBarButton(Graphics gfx, MenuBarButton w);	
	public void renderNotification(Graphics gfx, Notification n);
	public void renderCheckBox(Graphics gfx, Checkbox checkBox);

}
