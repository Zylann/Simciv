package simciv.gamestates;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import backend.PerformanceGraph;
import backend.SoundEngine;
import backend.geom.Vector2i;
import backend.ui.IActionListener;
import backend.ui.PushButton;
import backend.ui.RootPane;
import backend.ui.UIBasicGameState;
import backend.ui.UIRenderer;
import backend.ui.Widget;
import backend.ui.Window;

import simciv.CityBuilder;
import simciv.Game;
import simciv.MinimapUpdater;
import simciv.Terrain;
import simciv.Map;
import simciv.builds.Build;
import simciv.builds.BuildReport;
import simciv.content.Content;
import simciv.persistence.GameSaveData;
import simciv.persistence.GameSaverThread;
import simciv.ui.BuildInfoWindow;
import simciv.ui.BuildMenu;
import simciv.ui.BuildMenuBar;
import simciv.ui.InfoBar;
import simciv.ui.Minimap;
import simciv.ui.IndicatorsBar;
import simciv.ui.MapNotificationArea;
import simciv.ui.TimeBar;

/**
 * Main state of the game (city management)
 * @author Marc
 *
 */
public class CityView extends UIBasicGameState
{
	/** Identifier of the game state **/
	private int stateID = -1;
	
	/** Map where the player build his city **/
	private Map map;
	
	/** City builder tool **/
	private CityBuilder builder;
	
	/** Info bar displaying informations about what the player is pointing **/
	private InfoBar infoBar;
		
	/** Pointed map cell position (computed from mouse position) **/
	private Vector2i pointedCell = new Vector2i();
	
	/** Smaller representation of the map allowing to scroll the view **/
	private Minimap minimap;
	
	/** Window containing the minimap **/
	private Window minimapWindow;
	
	/** Pause window allowing to resume, save or quit the game **/
	private Window pauseWindow;
	
	/** Minimap updater **/
	private MinimapUpdater minimapUpdater;
	
	/** Build menus controlling cityBuilder's state **/
	private BuildMenuBar menuBar;
	
	/** Info bar displaying city population and money **/
	private IndicatorsBar indicatorsBar;

	/** Info bar displaying the current world time **/
	private TimeBar timeBar;
	
	/** Area where we can read quick messages from the city **/
	private MapNotificationArea notificationArea;
	
	/** Window containing informations about the build we right-clicked on **/
	private BuildInfoWindow buildInfoWindow;
	
	/** Game saving thread started when the player wants to save the game **/
	private GameSaverThread gameSaver;
		
	/** Is the player wants to quit the game? **/
	private boolean quitGameRequested;
	
	/** Is the game paused ? **/
	private boolean paused;
	
	/** Is the game starts when we enter this state? 
	 * (can be false if we come from the AdminView or the WorldView,
	 * other future gamestates)
	 */
	private boolean isGameBeginning;
	
	/** Is the debug panel visible? **/
	private boolean debugInfoVisible;

	/** Debug graph showing the render time evolution **/
	private PerformanceGraph renderTimeGraph;

	/** Debug graph showing the update time evolution **/
	private PerformanceGraph updateTimeGraph;

	/** Debug text (on the debug panel) **/
	private String debugText = "";

	/** Time taken by the last render. Used for debug. **/
	private long renderTime;
	
	/** Time taken by the last game logic update. Used for debug. **/
	private long updateTime;
	
	/**
	 * Constructs the CityView state identified by the given ID.
	 * @param stateID
	 */
	public CityView(int stateID)
	{
		this.stateID = stateID;
	}
	
	@Override
	public int getID()
	{
		return stateID;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException
	{
	}
	
	/**
	 * Sets the isGameBeginning flag to the given value.
	 * If true, the state will enter as if its was the beginning of the game
	 * (and some of the attributes  of the state will be initialized, like the map).
	 * If false, the state will enter as if we are already in the game and
	 * will not re-initialize its attributes.
	 * @param b
	 */
	public void setIsGameBeginning(boolean b)
	{
		isGameBeginning = b;
	}
	
	public void setMap(Map m)
	{
		this.map = m;
	}

	@Override
	protected void createUI(GameContainer container, final StateBasedGame game) throws SlickException
	{
		UIRenderer.setGlobalScale(2);
		int gs = UIRenderer.getGlobalScale();
		ui = new RootPane(container.getWidth() / gs, container.getHeight() / gs);
		
		// Pause window
		
		pauseWindow = new Window(ui, 0, 0, 150, 85, "Game paused");
		
		PushButton resumeButton = new PushButton(pauseWindow, 0, 10, "Resume game");
		resumeButton.setAlignX(Widget.ALIGN_CENTER);
		resumeButton.addActionListener(new TogglePauseAction());
		pauseWindow.add(resumeButton);
		
		PushButton saveButton = new PushButton(pauseWindow, 0, 28, "Save game");
		saveButton.setAlignX(Widget.ALIGN_CENTER);
		saveButton.addActionListener(new SaveGameAction());
		pauseWindow.add(saveButton);

		PushButton quitButton = new PushButton(pauseWindow, 0, 54, "Quit game");
		quitButton.setAlignX(Widget.ALIGN_CENTER);
		quitButton.addActionListener(new QuitGameAction());
		pauseWindow.add(quitButton);
		
		pauseWindow.addOnCloseAction(new TogglePauseAction());
		pauseWindow.setDraggable(false);
		pauseWindow.setVisible(false);
		pauseWindow.alignToCenter();
		ui.add(pauseWindow);
		
		menuBar = new BuildMenuBar(ui, 10, 10);
		menuBar.cityBuilderRef = builder;
		
		// Minimap
		
		minimapWindow = new Window(ui, 0, 0, 134, 134, "Minimap");
		minimap = new Minimap(minimapWindow, 0, 0, 0, 0);
		minimap.setView(map.view);
		minimap.setViz(minimapUpdater.getViz());
		minimap.setVisible(true);
		minimapWindow.add(minimap);
		minimapWindow.adaptSizeFromChildren();
		minimapWindow.alignToCenter();
		minimapWindow.setVisible(false);
		ui.add(minimapWindow);

		// Mouse tool
		menuBar.addMode(Content.sprites.uiCategCursor, "Pointer", CityBuilder.MODE_CURSOR);
		
		// Erase
		menuBar.addMode(Content.sprites.uiCategErase, "Erase", CityBuilder.MODE_ERASE);
		
		// Roads
		menuBar.addMode(Content.sprites.uiCategRoad, "Trace roads", CityBuilder.MODE_ROAD);
		
		// Houses
		menuBar.addBuild(Content.sprites.uiCategHouse, "Place houses", "House");
		
		// Food
		BuildMenu foodMenu = new BuildMenu(menuBar, 0, menuBar.getHeight(), 128);
		foodMenu.addBuild("FarmLand");
		foodMenu.addBuild("Hunters");
		menuBar.addCategory(Content.sprites.uiCategFood, "Food", foodMenu);

		// Industry
		BuildMenu industryMenu = new BuildMenu(menuBar, 0, menuBar.getHeight(), 128);
		industryMenu.addBuild("Warehouse");
		industryMenu.addBuild("Loggers");
		industryMenu.addBuild("WoodManufacture");
		menuBar.addCategory(Content.sprites.uiCategIndustry, "Industry", industryMenu);
		
		// Administration
		BuildMenu adminMenu = new BuildMenu(menuBar, 0, menuBar.getHeight(), 128);
		adminMenu.addBuild("TaxmenOffice");
		adminMenu.addBuild("ArchitectOffice");
		adminMenu.addBuild("FireStation");
		adminMenu.addBuild("PoliceStation");
		menuBar.addCategory(Content.sprites.uiCategAdmin, "Administration", adminMenu);

		// Marketing
		BuildMenu marketMenu = new BuildMenu(menuBar, 0, menuBar.getHeight(), 128);
		marketMenu.addBuild("Market");
		menuBar.addCategory(Content.sprites.uiCategMarketing, "Marketing and exchanges", marketMenu);
		
		// Health
		BuildMenu healthMenu = new BuildMenu(menuBar, 0, menuBar.getHeight(), 128);
		healthMenu.addBuild("WaterSource");
		menuBar.addCategory(Content.sprites.uiCategHealth, "Health", healthMenu);
		
		ui.add(menuBar);
		
		// Build info window
		
		buildInfoWindow = new BuildInfoWindow(ui, "Build info");
		buildInfoWindow.addOnOpenAction(new SetPauseAction(true));
		buildInfoWindow.addOnCloseAction(new SetPauseAction(false));
		buildInfoWindow.setVisible(false);
		ui.add(buildInfoWindow);
		
		// Indicators bar
		
		indicatorsBar = new IndicatorsBar(ui, 0, 0);
		indicatorsBar.setPosition(ui.getWidth() - indicatorsBar.getWidth() - 10, 10);
		ui.add(indicatorsBar);
		
		// Time bar
		
		timeBar = new TimeBar(ui, 0, 0);
		timeBar.setPosition(ui.getWidth() - timeBar.getWidth() - 10, 34);
		ui.add(timeBar);
		
		// Info bar

		infoBar = new InfoBar(ui, 0, 0, 300);
		ui.add(infoBar);
		
		// Notifications area
		
		notificationArea = new MapNotificationArea(ui, 0, timeBar.getY() + timeBar.getHeight(), 200);
		notificationArea.setAlignX(Widget.ALIGN_RIGHT);
		notificationArea.setMapView(map.view);
		ui.add(notificationArea);
		builder.setNotificationListener(notificationArea);
		map.setNotificationListener(notificationArea);
		
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame game) throws SlickException
	{
		quitGameRequested = false;
		paused = false;
		
		if(isGameBeginning)
		{			
			// Create and init minimap
			minimapUpdater = new MinimapUpdater(map);
			map.grid.addListener(minimapUpdater);
			minimapUpdater.updateCompleteViz(map.grid);
					
			// Create CityBuilder
			builder = new CityBuilder(map);
			
			ui = null;
			
			isGameBeginning = false;
		}
		
		if(renderTimeGraph == null)
			renderTimeGraph = new PerformanceGraph(0, 10);
		if(updateTimeGraph == null)
			updateTimeGraph = new PerformanceGraph(0, 10);
				
		// Because we will always draw the map at first on the entire screen at each frame
		gc.setClearEachFrame(false); // No need to clear each frame
		
		super.enter(gc, game); // Note : the UI is created here, it depends on the code above
	}

	@Override
	public void leave(GameContainer gc, StateBasedGame game)
			throws SlickException
	{
		gc.setClearEachFrame(true);
		super.leave(gc, game);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta)
			throws SlickException
	{
		// Debug
		long beginUpdateTime = gc.getTime();
		
		// Prevent too large update intervals
		if(delta > 100)
			delta = 100;
		
		Input input = gc.getInput();
		
		Terrain.updateTerrains(delta);
		
		notificationArea.update(delta);

		if(gameSaver != null && !gameSaver.isFinished())
			paused = true; // The game is saving
		else
		{
			if(quitGameRequested)
				game.enterState(Game.STATE_MAIN_MENU);
		}
		
		if(paused)
		{
			// The scroll view stills updated even if on pause (convenience)
			map.view.update(gc, delta / 1000.f);
		}
		else
		{
			// Pointed cell
			pointedCell = map.view.convertCoordsToMap(input.getMouseX(), input.getMouseY());
			builder.cursorMoved(pointedCell);

			map.update(gc, game, delta);
			builder.update(gc);
			
			minimapUpdater.update(delta);
			minimap.setViz(minimapUpdater.getViz());
			
			indicatorsBar.update(
					map.playerCity.population,
					map.playerCity.workingPopulation,
					(int) map.playerCity.getMoney(),
					map.time.getMonthProgressRatio());
			
			timeBar.update(map.time.toString());
		}
		
		SoundEngine.instance().update(delta);
		
		// debug
		updateTime = gc.getTime() - beginUpdateTime;
		updateTimeGraph.pushNextValue(updateTime);
		debugText = "x=" + pointedCell.x + ", y=" + pointedCell.y;
		debugText += "  updateTime=" + updateTime;
		debugText += "  renderTime=" + renderTime;
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics gfx)
			throws SlickException
	{		
		// Debug
		long beginRenderTime = gc.getTime();
		
		/* World */
		
		map.render(gc, game, gfx);
		
		/* Builder */
		
		builder.render(gfx);
		
		/* HUD */
				
		gfx.resetTransform();
		gfx.setColor(Color.white);

		gc.setShowFPS(debugInfoVisible);
		if(debugInfoVisible)
			renderDebugInfo(gc, gfx);
		
		infoBar.setPosition(0, ui.getHeight() - infoBar.getHeight());
		infoBar.setText(builder.getInfoLine());
		
		// debug
		renderTime = gc.getTime() - beginRenderTime;
		renderTimeGraph.pushNextValue(renderTime);
	}
	
	public void renderDebugInfo(GameContainer gc, Graphics gfx)
	{
		gfx.setColor(new Color(0, 0, 0, 128));
		gfx.fillRect(0, 0, gc.getWidth(), 100);
		gfx.setColor(Color.white);
		
		builder.renderDebugInfo(gfx);
				
		gfx.drawString(debugText, 10, 50);
		gfx.drawString(
				"MEM total(used):   "
				+ (Runtime.getRuntime().totalMemory() / 1000000)
				+ "(" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000)
				+ ") MB",
				600, 10);
		
		gfx.pushTransform();
		
		gfx.translate(gc.getWidth() - PerformanceGraph.WIDTH - 10, 10);		
		renderTimeGraph.render(gfx, 0, 0, 50);
		
		gfx.translate(-PerformanceGraph.WIDTH - 2, 0);
		updateTimeGraph.render(gfx, 0, 0, 50);
		
		gfx.popTransform();
	}
	
	@Override
	public void mousePressed(int button, int x, int y)
	{
		if(!paused)
		{
			try {
				builder.cursorPressed(button, pointedCell);
			} catch (SlickException e) {
				e.printStackTrace();
			}
			
			if(button == Input.MOUSE_MIDDLE_BUTTON && !minimapWindow.isVisible())
				toggleShowMinimap();
			
			if(button == Input.MOUSE_RIGHT_BUTTON)
				openBuildInfoWindow();
		}
	}
	
	private void openBuildInfoWindow()
	{
		Build b = map.getBuild(pointedCell.x, pointedCell.y);
		if(b != null)
		{
			String text = b.getInfoLine() + '\n'
				+ "----------------------------------------------------------------\n";
			BuildReport problems = b.getReport();
			
			if(problems.isEmpty())
				text += "Everything is fine here :)";
			else
			{
				List<String> messages = problems.getList(BuildReport.PROBLEM_MAJOR);
				for(String msg : messages)
					text += "[!] " + msg + '\n';

				messages = problems.getList(BuildReport.PROBLEM_MINOR);
				for(String msg : messages)
					text += "- " + msg + '\n';
				
				messages = problems.getList(BuildReport.INFO);
				for(String msg : messages)
					text += msg + '\n';
			}
			
			buildInfoWindow.setTitle(b.getDisplayableName());
			buildInfoWindow.setInfoText(text);
			buildInfoWindow.open();
		}
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy)
	{
		if(!paused)
		{
			pointedCell = map.view.convertCoordsToMap(newx, newy);
			builder.cursorMoved(pointedCell);
		}
	}

	@Override
	public void mouseReleased(int button, int x, int y)
	{
		if(!paused)
		{
			try {
				builder.cursorReleased();
			} catch (SlickException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void keyReleased(int key, char c)
	{
		if(key == Input.KEY_G)
			map.grid.toggleRenderGrid();			
		if(key == Input.KEY_P || key == Input.KEY_PAUSE || key == Input.KEY_ESCAPE)
			togglePause();
		if(key == Input.KEY_F3)
		{
			debugInfoVisible = !debugInfoVisible;
			if(debugInfoVisible)
				ui.setY(100 / UIRenderer.getGlobalScale());
			else
				ui.setY(0);
		}
		if(key == Input.KEY_TAB && !paused)
			toggleShowMinimap();
		if(key == Input.KEY_SPACE)
			map.setFastForward(!map.isFastForward());
	}
	
	public void togglePause()
	{
		setPause(!paused);
		pauseWindow.setVisible(paused);
	}
	
	private void setPause(boolean p)
	{
		paused = p;
	}
	
	public void toggleShowMinimap()
	{
		minimapWindow.layout();
		minimapWindow.setVisible(!minimapWindow.isVisible());
	}
	
	// UI Actions
	
	class TogglePauseAction implements IActionListener
	{
		@Override
		public void actionPerformed(Widget sender) {
			togglePause();
		}	
	}
	
	class SetPauseAction implements IActionListener
	{
		boolean pause;
		public SetPauseAction(boolean p) {
			pause = p;
		}
		@Override
		public void actionPerformed(Widget sender) {
			setPause(pause);
		}	
	}
	
	class QuitGameAction implements IActionListener
	{
		@Override
		public void actionPerformed(Widget sender) {
			quitGameRequested = true;
		}
	}
	
	class SaveGameAction implements IActionListener
	{
		@Override
		public void actionPerformed(Widget sender) {
			GameSaveData saveData = new GameSaveData("map");
			saveData.map = map;
			gameSaver = new GameSaverThread(saveData);
			gameSaver.save();
		}
	}

}


