package simciv;

import java.awt.Container;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.util.ArrayList;
import javax.swing.JFrame;
import org.newdawn.slick.CanvasGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.GameState;

import simciv.content.Content;
import simciv.gamestates.GameCreatingScreen;
import simciv.gamestates.CityView;
import simciv.gamestates.ContentLoadingScreen;
import simciv.gamestates.GameLoadingScreen;
import simciv.gamestates.MainMenu;
import simciv.ui.base.CrashWindow;
import simciv.ui.base.UIStateBasedGame;

/**
 * The main class
 * 
 * @author Marc
 * 
 */
public class Game extends UIStateBasedGame
{
	/* Static vars */ 
	
	// Game constants
	public static final String title = "Simciv indev 3.0";
	public static final int tilesSize = 16;
	public static final int defaultScreenWidth = 800;
	public static final int defaultScreenHeight = 600;
	
	// State constants
	public static final int STATE_NULL = 0;
	public static final int STATE_CONTENT_LOADING = 1;
	public static final int STATE_MAIN_MENU = 2;
	public static final int STATE_GAME_CREATING = 3;
	public static final int STATE_GAME_LOADING = 4;
	public static final int STATE_CITY_VIEW = 5;

	public static Settings settings;
	
	// Game container
	private static CanvasGameContainer canvas;
	private static Container contentPane;
	private static JFrame gameFrame;
	
	// The game
	private static Game game;

	// States
	private ArrayList<GameState> states;
	public CityView cityView; // direct access
	
	public static void main(String[] args)
	{		
		settings = new Settings();
		
		try
		{
			// Create game
			game = new Game(title);
			
			// Create canvas
			canvas = new CanvasGameContainer(game);
			canvas.setSize(defaultScreenWidth, defaultScreenHeight);
			canvas.getContainer().setAlwaysRender(true);
			canvas.getContainer().setTargetFrameRate(settings.framerate);
			canvas.getContainer().setVSync(settings.useVSync);
			canvas.getContainer().setSmoothDeltas(settings.smoothDeltasEnabled);
			canvas.getContainer().setUpdateOnlyWhenVisible(true);

			// Create main window
			gameFrame = new JFrame();
			gameFrame.setTitle(title);
			gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			gameFrame.setLocationRelativeTo(null);
			gameFrame.addWindowListener(new MainFrameListener());
			gameFrame.setVisible(true);
			// Note : frame borders are not available before the frame is shown
			// Setting frame size with content sized to default dimensions
			gameFrame.setSize(
					defaultScreenWidth + gameFrame.getInsets().left + gameFrame.getInsets().right,
					defaultScreenHeight + gameFrame.getInsets().top + gameFrame.getInsets().bottom);
			
			// Add canvas to the content pane
			contentPane = gameFrame.getContentPane();
			contentPane.addComponentListener(new MainComponentListener());
			contentPane.add(canvas);
									
			canvas.start(); // Starts the game
		
		} catch (Throwable t)
		{
			t.printStackTrace();
			onCrash(t);
		}
		
		// FIXME on game close : "AL lib: alc_cleanup: 1 device not closed" (serious or not?)
	}
	
	/**
	 * Closes the game and displays a crash report window.
	 * The report is made using the last exception caught and the stack trace.
	 * @param t : exception that caused the game to crash
	 */
	public static void onCrash(Throwable t)
	{
		// Close the game
		close();
		
		// Get the stack trace as string
		StackTraceElement[] stackTrace = t.getStackTrace();
		String logStr = "";
		for(StackTraceElement el : stackTrace)
		{
			logStr += el.toString() + "\n";
		}
		
		// Open the crash window
		CrashWindow win = new CrashWindow(gameFrame);
		win.setErrorDetails(logStr);
		win.setVisible(true);
	}

	public static void close()
	{
		canvas.getContainer().exit(); // Note : doesn't work without frame.dispose()
		gameFrame.dispose();
	}

	public Game(String title)
	{
		super(title);

		states = new ArrayList<GameState>();

		// Create states
		addState(new ContentLoadingScreen(STATE_CONTENT_LOADING));
		addState(new GameCreatingScreen(STATE_GAME_CREATING));
		addState(new GameLoadingScreen(STATE_GAME_LOADING));
		addState(new MainMenu(STATE_MAIN_MENU));
		cityView = new CityView(STATE_CITY_VIEW);
		addState(cityView);
	}

	@Override
	public void addState(GameState state)
	{
		states.add(state);
		super.addState(state);
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException
	{		
		Content.loadMinimalContent();

		// Initialize states
		for (GameState state : states)
			state.init(container, this);

		// Enter first state
		enterState(STATE_CONTENT_LOADING);
	}
	
	// Main window listeners

	static class MainFrameListener implements WindowListener
	{
		@Override
		public void windowActivated(WindowEvent e) {}

		@Override
		public void windowClosed(WindowEvent e) {
			System.exit(0);
		}

		@Override
		public void windowClosing(WindowEvent e) {
			canvas.setEnabled(false);
			canvas.dispose();
		}

		@Override
		public void windowDeactivated(WindowEvent e) {}

		@Override
		public void windowDeiconified(WindowEvent e) {}

		@Override
		public void windowIconified(WindowEvent e) {}

		@Override
		public void windowOpened(WindowEvent e) {
			canvas.requestFocus();
		}
	}
	
	static class MainComponentListener implements ComponentListener
	{
		@Override
		public void componentHidden(ComponentEvent e) {}

		@Override
		public void componentMoved(ComponentEvent e) {}

		@Override
		public void componentResized(ComponentEvent e) {
			game.onContainerResize(contentPane.getWidth(), contentPane.getHeight());
		}

		@Override
		public void componentShown(ComponentEvent e) {}
	}

}


