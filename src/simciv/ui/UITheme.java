package simciv.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import backend.ui.Button;
import backend.ui.ITheme;
import backend.ui.Label;
import backend.ui.MenuBarButton;
import backend.ui.MenuItem;
import backend.ui.Notification;
import backend.ui.Panel;
import backend.ui.ProgressBar;
import backend.ui.PushButton;
import backend.ui.ToolButton;
import backend.ui.UIRenderer;
import backend.ui.Window;
import backend.ui.WindowTitleBar;

import simciv.content.Content;

public class UITheme implements ITheme
{
	// Constants
	private static final int FRAME_BASE = 16;
	private static final int WINDOW_SHADOW_SIZE = 2;
	
	private SpriteSheet frameBackground;
	private SpriteSheet windowTitleBarBackground;
	private SpriteSheet progressBarSprites;
	private Font font;

	public void loadContent()
	{
		font = Content.globalFont;
		frameBackground = Content.sprites.uiFrame;
		windowTitleBarBackground = Content.sprites.uiWindowTitleBar;
		progressBarSprites = Content.sprites.uiProgressBar;
	}

	@Override
	public void renderPanel(Graphics gfx, Panel w)
	{
		UIRenderer.renderFrame(gfx, frameBackground,
				w.getAbsoluteX(), w.getAbsoluteY(),
				w.getWidth() + WINDOW_SHADOW_SIZE, 
				w.getHeight() + WINDOW_SHADOW_SIZE, 
				FRAME_BASE);
	}
	
	@Override
	public void renderWindow(Graphics gfx, Window w)
	{
		UIRenderer.renderFrame(gfx, frameBackground,
				w.getAbsoluteX(), w.getAbsoluteY(),
				w.getWidth() + WINDOW_SHADOW_SIZE, 
				w.getHeight() + WINDOW_SHADOW_SIZE, 
				FRAME_BASE);
	}
	
	@Override
	public void renderWindowTitleBar(Graphics gfx, WindowTitleBar w)
	{
		int x = w.getAbsoluteX();
		int y = w.getAbsoluteY();
		int b = WindowTitleBar.height;
		
		UIRenderer.renderBar(gfx,
			windowTitleBarBackground, x, y, w.getWidth(), w.getHeight(), b, 0);
		
		// Title
		if(w.getText() != null)
		{
			gfx.setColor(Color.white);
			gfx.drawString(w.getText(), x + b, y + 2);
		}
	}

	@Override
	public void renderWindowCloseButton(Graphics gfx, Button w)
	{
		renderButton(gfx, w, Content.sprites.uiWindowCloseButton, null, null, 0);
	}

	private void renderButton(
			Graphics gfx, Button w, SpriteSheet sprites,
			Image icon, String text, int pressOffset)
	{
		int x = w.getAbsoluteX();
		int y = w.getAbsoluteY();
				
		if(w.isPressed())
			y += pressOffset;
		
		int s = w.isMouseOver() && w.isEnabled() ? 1 : 0;
		
		if(sprites.getHorizontalCount() == 3)
		{
			UIRenderer.renderBar(gfx, sprites,
				x, y, w.getWidth(), w.getHeight(), 16, s);
		}
		else
			gfx.drawImage(sprites.getSprite(0, s), x, y);
		
		if(icon != null)
			gfx.drawImage(icon, x, y);
		
		if(text != null)
		{
			if(w.isEnabled())
				gfx.setColor(Color.black);
			else
				gfx.setColor(Color.gray);
			gfx.drawString(text, x + 4, y + 2);
		}
	}
	
	@Override
	public void renderMenuItem(Graphics gfx, MenuItem w)
	{
		renderButton(gfx, w, Content.sprites.uiMenuItem, null, w.getText(), 1);
	}
	
	@Override
	public void renderToolButton(Graphics gfx, ToolButton w)
	{
		renderButton(gfx, w, Content.sprites.uiToolButton, w.icon, null, 2);
	}

	@Override
	public void renderPushButton(Graphics gfx, PushButton w)
	{
		renderButton(gfx, w, Content.sprites.uiPushButton, null, w.getText(), 1);
	}

	@Override
	public void renderProgressBar(Graphics gfx, ProgressBar w)
	{
		int x = w.getAbsoluteX();
		int y = w.getAbsoluteY();
		int t = (int) (w.getProgressRatio() * w.getWidth());
		int b = ProgressBar.height;
		
		if(t == 0)
		{
			UIRenderer.renderBar(gfx,
					progressBarSprites,
					x, y, w.getWidth(), w.getHeight(), b, 0);
		}
		else if(t < w.getWidth())
		{
			// Left
			if(t <= b)
			{
				UIRenderer.renderImagePart(gfx,
					progressBarSprites.getSprite(0, 1), x, y, 0, 0, t, b);
				UIRenderer.renderImagePart(gfx,
					progressBarSprites.getSprite(0, 0), x + t, y, t, 0, b - t, b);
			}
			else
				gfx.drawImage(progressBarSprites.getSprite(0, 1), x, y);
			
			// Center
			if(t > b && t <= w.getWidth() - b)
			{
				UIRenderer.renderImageRepeatXY(gfx,
					progressBarSprites.getSprite(1, 1), x + b, y, t - b, b);
				UIRenderer.renderImageRepeatXY(gfx,
					progressBarSprites.getSprite(1, 0), x + t, y, w.getWidth() - b - t, b);
				gfx.drawImage(progressBarSprites.getSprite(2, 0), x + w.getWidth() - b, y);
			}
			else
			{
				int s = t <= b ? 0 : 1;
				UIRenderer.renderImageRepeatXY(gfx,
					progressBarSprites.getSprite(1, s), x + b, y, w.getWidth() - 2*b, b);
			}
			
			// Right
			if(t > w.getWidth() - b)
			{
				int t2 = t - w.getWidth() + b;
				UIRenderer.renderImagePart(gfx, progressBarSprites.getSprite(2, 1),
						x + w.getWidth() - b, y, 0, 0, t2, b);
				UIRenderer.renderImagePart(gfx, progressBarSprites.getSprite(2, 0),
						x + t, y, t2, 0, b - t2, b);
			}
			else
				gfx.drawImage(progressBarSprites.getSprite(2, 0), x + w.getWidth() - b, y);
		}
		else
			UIRenderer.renderBar(gfx,
				progressBarSprites, x, y, w.getWidth(), w.getHeight(), b, 1);			
	}
	
	@Override
	public void renderLabel(Graphics gfx, Label label)
	{
		renderLabel(gfx, label, 0);
	}

	public void renderLabel(Graphics gfx, Label label, int offY)
	{
		int x = label.getAbsoluteX();
		int y = label.getAbsoluteY() + offY;
		
		if(label.getImage() != null)
			gfx.drawImage(label.getImage(), x, y);
		
		if(label.getText() != null)
		{
			gfx.setColor(label.getTextColor());
			gfx.setFont(this.font);
			label.getText().render(gfx, x, y);
		}
	}

	@Override
	public void renderMenuBarButton(Graphics gfx, MenuBarButton w)
	{
		renderButton(gfx, w, Content.sprites.uiToolButton, null, null, 2);
		if(w.isPressed())
			renderLabel(gfx, w.getLabel(), 2);
		else
			renderLabel(gfx, w.getLabel(), 0);
	}
	
	@Override
	public void renderNotification(Graphics gfx, Notification n)
	{
		gfx.pushTransform();
		gfx.translate(n.getAbsoluteX() + 4, n.getAbsoluteY() + 2);
		
		UIRenderer.renderBar(gfx,
				Content.sprites.uiNotification,
				0, 0, n.getWidth(), n.getHeight(), n.getHeight(), 0);
		
		Image icon = n.getIcon();
		if(icon != null)
		{
			gfx.drawImage(icon, 0, 0);
			gfx.translate(icon.getWidth() + 2, 0);
		}
		
		if(n.getText() != null)
		{
			gfx.setColor(Color.black);
			gfx.drawString(n.getText(), 0, 1);
		}
		
		gfx.popTransform();
	}

	@Override
	public Font getFont()
	{
		return font;
	}
	
}



