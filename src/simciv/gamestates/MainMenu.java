package simciv.gamestates;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import backend.ui.IActionListener;
import backend.ui.Label;
import backend.ui.Panel;
import backend.ui.PushButton;
import backend.ui.RootPane;
import backend.ui.UIBasicGameState;
import backend.ui.UIRenderer;
import backend.ui.Widget;

import simciv.Game;
import simciv.persistence.GameSaveData;
import simciv.ui.SettingsWindow;

public class MainMenu extends UIBasicGameState
{
	private int stateID;
	private boolean newGame;
	private boolean loadGame;
	private boolean closeRequested;
	private Color bgColor = new Color(32, 32, 32);
	private PushButton loadGameBtn;
		
	public MainMenu(int stateID)
	{
		this.stateID = stateID;
	}
	
	@Override
	public int getID()
	{
		return stateID;
	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException
	{
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException
	{
		super.enter(container, game);

		newGame = false;
		loadGame = false;
		
		// Checks if there is at least one save file available
		loadGameBtn.setEnabled(GameSaveData.isSaveFiles());
	}

	@Override
	protected void createUI(GameContainer container, StateBasedGame game)
			throws SlickException
	{
		UIRenderer.setGlobalScale(2);
		int gs = UIRenderer.getGlobalScale();
		ui = new RootPane(container.getWidth() / gs, container.getHeight() / gs);
		
		// Settings window
		
		SettingsWindow settingsWindow = new SettingsWindow(ui);
		settingsWindow.setVisible(false);
		ui.add(settingsWindow);
		
		// Main menu panel
		
		Panel panel = new Panel(ui, 150, 100);
		panel.alignToCenter();
		ui.add(panel);
		
		PushButton newGameBtn = new PushButton(panel, 0, 10, "New game");
		newGameBtn.setAlignX(Widget.ALIGN_CENTER);
		newGameBtn.addActionListener(new NewGameAction());
		panel.add(newGameBtn);
		
		loadGameBtn = new PushButton(panel, 0, 28, "Load game");
		loadGameBtn.setAlignX(Widget.ALIGN_CENTER);
		loadGameBtn.addActionListener(new LoadGameAction());
		panel.add(loadGameBtn);
		
		PushButton settingsBtn = new PushButton(panel, 0, 46, "Settings");
		settingsBtn.setAlignX(Widget.ALIGN_CENTER);
		settingsBtn.addActionListener(settingsWindow.new OpenAction());
		panel.add(settingsBtn);

		PushButton exitBtn = new PushButton(panel, 0, 72, "Quit");
		exitBtn.setAlignX(Widget.ALIGN_CENTER);
		exitBtn.addActionListener(new QuitGameAction());
		panel.add(exitBtn);
		
		// Main title
		Label title = new Label(ui, Game.title);
		title.setPosition(0, 50);
		title.setAlignX(Widget.ALIGN_CENTER);
		title.setTextColor(Color.white);
		ui.add(title);
				
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
			throws SlickException
	{
		gfx.setBackground(bgColor);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException
	{
		if(closeRequested)
			simciv.Game.close();
		if(newGame)
			game.enterState(Game.STATE_GAME_CREATING);
		if(loadGame)
			game.enterState(Game.STATE_GAME_LOADING);
	}

	class QuitGameAction implements IActionListener
	{
		@Override
		public void actionPerformed(Widget sender) {
			closeRequested = true;
		}
	}
	
	class NewGameAction implements IActionListener
	{
		@Override
		public void actionPerformed(Widget sender) {
			newGame = true;
		}
	}
	
	class LoadGameAction implements IActionListener
	{
		@Override
		public void actionPerformed(Widget sender) {
			loadGame = true;
		}
	}

}
