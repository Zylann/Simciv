package simciv;

import java.awt.Container;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;

import java.util.ArrayList;
import javax.swing.JFrame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.util.Log;

import backend.CanvasGameContainer2;
import backend.ITopExceptionListener;
import backend.LogSystem;

import simciv.content.Content;
import simciv.gamestates.GameCreatingScreen;
import simciv.gamestates.CityView;
import simciv.gamestates.ContentLoadingScreen;
import simciv.gamestates.GameLoadingScreen;
import simciv.gamestates.MainMenu;
import simciv.gamestates.TestPathFinder;
import simciv.gamestates.TestText;
import simciv.ui.base.CrashWindow;
import simciv.ui.base.UIStateBasedGame;

/**
 * The main class
 * 
 * @author Marc
 * 
 */
// TODO separate GameWindow and the main class where we use it
public class Game extends UIStateBasedGame
{
	/* Static vars */ 
	
	// Game constants
	public static final String title = "Simciv indev R3";
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
	// Test state constants
	public static final int STATE_TEST_PATHFINDER = 42;
	public static final int STATE_TEST_TEXT = 43;

	public static Settings settings;
	
	// Game container
	private static CanvasGameContainer2 canvas;
	private static Container contentPane;
	private static JFrame gameFrame;
	
	// Log stream
	private static PrintStream logStream;

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
			// Configure log system (overwrite if already exists)
			FileOutputStream logFile = new FileOutputStream("log.txt");
			logStream = new PrintStream(logFile);
			LogSystem.out = logStream;
			LogSystem.consoleEcho = true;
			LogSystem logSystem = new LogSystem();
			Log.setLogSystem(logSystem);
			
			// Create game
			game = new Game(title);
			
			// Create canvas
			canvas = new CanvasGameContainer2(game);
			canvas.setSize(defaultScreenWidth, defaultScreenHeight);
			canvas.setTopExceptionListener(new TopExceptionListener());
			
			// Configure game container
			GameContainer gc = canvas.getGameContainer();
			gc.setTargetFrameRate(settings.getTargetFramerate());
			gc.setVSync(settings.isUseVSync());
			gc.setSmoothDeltas(settings.isSmoothDeltasEnabled());
			gc.setUpdateOnlyWhenVisible(true);

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
									
			canvas.start(); // Start the game
		
		} catch (Throwable t)
		{
			t.printStackTrace();
			onCrash(t);
		}
		
		// FIXME on game close : "AL lib: alc_cleanup: 1 device not closed" (serious or not?)
	}
	
	/**
	 * Displays a crash report window.
	 * @param t : exception that caused the game to crash
	 */
	public static void onCrash(Throwable t)
	{
		// Close log
		logStream.flush();
		logStream.close();
		
		String logContent = "---";
		
		try
		{
			System.out.println("Retrieving log content...");
			
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream("log.txt"));
			StringWriter sw = new StringWriter();
			
			int b;
			while((b = bis.read()) != -1)
				sw.write(b);
			
			sw.flush();
			sw.close();
			bis.close();
			
			logContent = sw.toString();
			
			System.out.println("Done.");
			
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// Open the crash window
		CrashWindow win = new CrashWindow(gameFrame);
		win.setErrorDetails(logContent);
		win.setVisible(true);
	}

	public static void close()
	{
		canvas.getGameContainer().exit(); // Note : doesn't work without frame.dispose()
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
		addState(new TestPathFinder(STATE_TEST_PATHFINDER));
		addState(new TestText(STATE_TEST_TEXT));
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
	
	static class TopExceptionListener implements ITopExceptionListener
	{
		@Override
		public void onTopException(Throwable t) {
			onCrash(t);
		}
	}

}


