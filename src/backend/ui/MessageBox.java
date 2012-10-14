package backend.ui;

import org.newdawn.slick.Color;

public class MessageBox extends Window
{
	protected PushButton closeButton;
	protected Label message;

	public MessageBox(Widget parent, int x, int y, int width, int height, String title)
	{
		super(parent, x, y, width, height, title);
		
		WidgetContainer content = this.getContent();
		
		closeButton = new PushButton(content, 0, 0, "OK");
		closeButton.setAlign(ALIGN_CENTER, ALIGN_BOTTOM, 0, 8);
		closeButton.addActionListener(new CloseAction());
		
		message = new Label(content, "---");
		message.setTextColor(Color.black);
		message.setSize(width, height - closeButton.getHeight());
		message.setTextWrap(true);
		
		content.add(closeButton);
		content.add(message);
	}
	
	public MessageBox(Widget parent, int w, int h, String title)
	{
		this(parent, 0, 0, w, h, title);
	}
	
	public void setText(String text)
	{
		message.setText(text);
	}
	
	public void addCloseListener(IActionListener l)
	{
		closeButton.addActionListener(l); // Fof the push button
		addOnCloseAction(l); // For the title bar close button
	}
	
	class CloseAction implements IActionListener
	{
		@Override
		public void actionPerformed(Widget sender) {
			setVisible(false);
		}
	}

}

