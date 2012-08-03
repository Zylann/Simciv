package simciv.gamestates;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Cheats;
import simciv.CityBuilder;
import simciv.IntRange2D;
import simciv.MinimapUpdater;
import simciv.SoundEngine;
import simciv.Terrain;
import simciv.Vector2i;
import simciv.View;
import simciv.World;
import simciv.content.Content;
import simciv.ui.IActionListener;
import simciv.ui.InfoBar;
import simciv.ui.Menu;
import simciv.ui.MenuItem;
import simciv.ui.Minimap;
import simciv.ui.PushButton;
import simciv.ui.IndicatorsBar;
import simciv.ui.RootPane;
import simciv.ui.ToolButton;
import simciv.ui.ToolButtonGroup;
import simciv.ui.UIBasicGameState;
import simciv.ui.UIRenderer;
import simciv.ui.Window;
import simciv.units.Citizen;

/**
 * Main state of the game
 * @author Marc
 *
 */
public class GamePlay extends UIBasicGameState
{
	private int stateID = -1;
	private View view;
	private World world;
	private CityBuilder builder;
	private InfoBar infoBar;
	private String debugText = "";
	private Vector2i pointedCell = new Vector2i();
	private ToolButtonGroup buildCategoryButtonsGroup;
	private Minimap minimap;
	private Window minimapWindow;
	private Window pauseWindow;
	private MinimapUpdater minimapUpdater;
	private IndicatorsBar indicatorsBar;
	private boolean closeRequested = false;
	private boolean paused = false;
	private boolean debugInfoVisible = false;
	private long renderTime;
	private long updateTime;
	
	public GamePlay(int stateID)
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
	
	public void setWorld(World world)
	{
		this.world = world;
	}

	@Override
	protected void createUI(GameContainer container, final StateBasedGame game) throws SlickException
	{
		UIRenderer.instance().setGlobalScale(2);
		int gs = UIRenderer.instance().getGlobalScale();
		ui = new RootPane(container.getWidth() / gs, container.getHeight() / gs);
		
		// Pause window
		
		pauseWindow = new Window(ui, 0, 0, 150, 60, "Game paused");
		
		PushButton resumeButton = new PushButton(pauseWindow, 0, 10, "Resume game");
		PushButton quitButton = new PushButton(pauseWindow, 0, 28, "Quit game");
		resumeButton.alignToCenter(true, false);
		resumeButton.setAction(new TogglePauseAction());
		quitButton.alignToCenter(true, false);
		quitButton.setAction(new QuitGameAction());
		
		pauseWindow.add(resumeButton);
		pauseWindow.add(quitButton);
		pauseWindow.setOnCloseAction(new TogglePauseAction());
		pauseWindow.setDraggable(false);
		pauseWindow.setVisible(false);
		pauseWindow.alignToCenter();
		ui.add(pauseWindow);
		
		// Build categories
		buildCategoryButtonsGroup = new ToolButtonGroup();
		
		// Mouse tool
		
		ToolButton mouseButton = new ToolButton(ui, 10, 10, buildCategoryButtonsGroup);
		mouseButton.setActionListener(new ChangeBuildCategoryAction(CityBuilder.MODE_CURSOR));
		mouseButton.icon = Content.images.uiCategCursor;
		buildCategoryButtonsGroup.add(mouseButton);
		ui.add(mouseButton);
		
		// Erase
		
		ToolButton eraseButton = new ToolButton(ui, 34, 10, buildCategoryButtonsGroup);
		eraseButton.setActionListener(new ChangeBuildCategoryAction(CityBuilder.MODE_ERASE));
		eraseButton.icon = Content.images.uiCategErase;
		buildCategoryButtonsGroup.add(eraseButton);
		ui.add(eraseButton);
		
		// Roads

		ToolButton traceRoadsButton = new ToolButton(ui, 58, 10, buildCategoryButtonsGroup);
		traceRoadsButton.setActionListener(new ChangeBuildCategoryAction(CityBuilder.MODE_ROAD));
		traceRoadsButton.icon = Content.images.uiCategRoad;
		buildCategoryButtonsGroup.add(traceRoadsButton);
		ui.add(traceRoadsButton);
		
		// Houses
		
		ToolButton buildHousesButton = new ToolButton(ui, 82, 10, buildCategoryButtonsGroup);
		buildHousesButton.setActionListener(new ChangeBuildCategoryAction(CityBuilder.MODE_HOUSE, "House"));
		buildHousesButton.icon = Content.images.uiCategHouse;
		buildCategoryButtonsGroup.add(buildHousesButton);
		ui.add(buildHousesButton);
		
		// Food
		
		ToolButton foodBuildsButton = new ToolButton(ui, 106, 10, buildCategoryButtonsGroup);

		Menu foodBuildsMenu = new Menu(ui, 10, 34, 128);
		MenuItem waterSourceItem = new MenuItem(foodBuildsMenu, "Water source");
		MenuItem farmlandItem = new MenuItem(foodBuildsMenu, "Farm land");
		MenuItem huntersItem = new MenuItem(foodBuildsMenu, "Hunters");
		waterSourceItem.setEnabled(false);
		huntersItem.setEnabled(false);
		foodBuildsMenu
			.add(waterSourceItem, new SelectBuildAction(foodBuildsButton, "House"))
			.add(farmlandItem, new SelectBuildAction(foodBuildsButton, "FarmLand"))
			.add(huntersItem, new SelectBuildAction(foodBuildsButton, "House"))
			.setNullActionListener(new SelectBuildAction(foodBuildsButton, null))
			.setVisible(false);
		ui.add(foodBuildsMenu);

		foodBuildsButton.setActionListener(new ChangeBuildCategoryAction(CityBuilder.MODE_BUILDS, foodBuildsMenu));
		foodBuildsButton.icon = Content.images.uiCategFood;
		buildCategoryButtonsGroup.add(foodBuildsButton);
		ui.add(foodBuildsButton);
		
		// Industry
		
		ToolButton industryBuildsButton = new ToolButton(ui, 130, 10, buildCategoryButtonsGroup);
		
		Menu industryBuildsMenu = new Menu(ui, 10, 34, 128);
		industryBuildsMenu
			.add(new MenuItem(industryBuildsMenu, "Warehouse"), new SelectBuildAction(industryBuildsButton, "Warehouse"))
			.setNullActionListener(new SelectBuildAction(industryBuildsButton, null))
			.setVisible(false);
		ui.add(industryBuildsMenu);
		
		industryBuildsButton.setActionListener(new ChangeBuildCategoryAction(CityBuilder.MODE_BUILDS, industryBuildsMenu));
		industryBuildsButton.icon = Content.images.uiCategIndustry;
		buildCategoryButtonsGroup.add(industryBuildsButton);
		ui.add(industryBuildsButton);
		
		// Administration
		
		ToolButton adminBuildsButton = new ToolButton(ui, 154, 10, buildCategoryButtonsGroup);
		
		Menu adminBuildsMenu = new Menu(ui, 10, 34, 128);
		adminBuildsMenu
			.add(new MenuItem(adminBuildsMenu, "Taxmen office"), new SelectBuildAction(adminBuildsButton, "TaxmenOffice"))
			.setNullActionListener(new SelectBuildAction(adminBuildsButton, null))
			.setVisible(false);
		ui.add(adminBuildsMenu);
			
		adminBuildsButton.setActionListener(new ChangeBuildCategoryAction(CityBuilder.MODE_BUILDS, adminBuildsMenu));
		adminBuildsButton.icon = Content.images.uiCategAdmin;
		buildCategoryButtonsGroup.add(adminBuildsButton);
		ui.add(adminBuildsButton);
		
		// Minimap
		
		minimapWindow = new Window(ui, 0, 0, 134, 134, "Minimap");
		minimap = new Minimap(minimapWindow, 0, 0, 0, 0);
		minimap.setView(view);
		minimap.setViz(minimapUpdater.getViz());
		minimap.setVisible(true);
		minimapWindow.add(minimap);
		minimapWindow.adaptSize();
		minimapWindow.alignToCenter();
		minimapWindow.setVisible(false);
		ui.add(minimapWindow);
		
		// Resource bar
		
		indicatorsBar = new IndicatorsBar(ui, 0, 0);
		indicatorsBar.setPosition(ui.getWidth() - indicatorsBar.getWidth() - 10, 10);
		ui.add(indicatorsBar);
		
		// Info bar

		infoBar = new InfoBar(ui, 0, 0, 300);
		ui.add(infoBar);
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame game) throws SlickException
	{		
		minimapUpdater = new MinimapUpdater(world.map);
		
		builder = new CityBuilder(world);
		view = new View(0, 0, 2);
		view.setWorldSize(world.map.getWidth(), world.map.getHeight());
		
		// Because we will always draw the map on the entire screen at each frame
		gc.setClearEachFrame(false);
		
		super.enter(gc, game);
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
		
		if(closeRequested)
			gc.exit();
		
		view.update(gc, delta / 1000.f);
		Terrain.updateTerrains(delta);
		
		if(!paused)
		{
			// Pointed cell
			pointedCell = view.convertCoordsToMap(input.getMouseX(), input.getMouseY());
			builder.cursorMoved(pointedCell);

			world.update(gc, game, delta);
			builder.update(gc);
		}
		
		minimapUpdater.update(delta);
		minimap.setViz(minimapUpdater.getViz());
		
		SoundEngine.instance().update(delta);
		
		indicatorsBar.update(
				Citizen.totalCount,
				Citizen.totalWithJob,
				(int) world.playerCity.getMoney(),
				world.time.getMonthProgressRatio());
		
		// debug
		updateTime = gc.getTime() - beginUpdateTime;
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

		// World view
		view.configureGraphicsForWorldRendering(gfx);
		IntRange2D mapRange = view.getMapRange(gc);
		
		/* World */
		
		world.render(gc, game, gfx, mapRange);
		
		/* Builder */
		
		builder.render(gfx);
		
		/* HUD */
				
		gfx.resetTransform();
		gfx.setColor(Color.white);

		gc.setShowFPS(debugInfoVisible);
		if(debugInfoVisible)
			renderDebugInfo(gc, gfx);
		
		infoBar.setPosition(0, ui.getHeight() - infoBar.getHeight());
		infoBar.setText(builder.getInfoText());
		
		// debug
		renderTime = gc.getTime() - beginRenderTime;
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
	}
	
	@Override
	public void mousePressed(int button, int x, int y)
	{
		if(!paused)
		{
			builder.cursorPressed(button, pointedCell);
			if(button == Input.MOUSE_MIDDLE_BUTTON && !minimapWindow.isVisible())
				toggleShowMinimap();
		}
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy)
	{
		if(!paused)
		{
			pointedCell = view.convertCoordsToMap(newx, newy);
			builder.cursorMoved(pointedCell);
		}
	}

	@Override
	public void mouseReleased(int button, int x, int y)
	{
		if(!paused)
			builder.cursorReleased();
	}

	@Override
	public void keyReleased(int key, char c)
	{
		if(key == Input.KEY_G)
			world.map.toggleRenderGrid();
		if(key == Input.KEY_P || key == Input.KEY_PAUSE || key == Input.KEY_ESCAPE)
			togglePause();
		if(key == Input.KEY_F3)
		{
			debugInfoVisible = !debugInfoVisible;
			if(debugInfoVisible)
				ui.setY(100 / UIRenderer.instance().getGlobalScale());
			else
				ui.setY(0);
		}
		if(key == Input.KEY_TAB)
			toggleShowMinimap();
		if(key == Input.KEY_SPACE && Cheats.isFastForwardEnabled())
			world.setFastForward(!world.isFastForward());
	}
	
	public void togglePause()
	{
		paused = !paused;
		pauseWindow.setVisible(paused);
	}
	
	public void toggleShowMinimap()
	{
		minimapWindow.alignToCenter();
		minimapWindow.setVisible(!minimapWindow.isVisible());
	}
	
	// UI Actions
	
	class ChangeBuildCategoryAction implements IActionListener
	{
		private int category;
		private String buildingString;
		private Menu buildsMenu;
		
		public ChangeBuildCategoryAction(int categ) {
			this.category = categ;
		}

		public ChangeBuildCategoryAction(int categ, Menu buildsMenu) {
			this.category = categ;
			this.buildsMenu = buildsMenu;
		}
		
		public ChangeBuildCategoryAction(int categ, String buildingString) {
			this.category = categ;
			this.buildingString = buildingString;
		}
		
		@Override
		public void actionPerformed() {
			builder.setMode(category);
			if(buildingString != null)
				builder.setBuildingString(buildingString);
			if(buildsMenu != null)
				buildsMenu.setVisible(true);
		}
	}
	
	class SelectBuildAction implements IActionListener
	{
		private String buildingString;
		private ToolButton categButton;
		
		public SelectBuildAction(ToolButton categButton, String buildingString) {
			this.buildingString = buildingString;
			this.categButton = categButton;
		}
		
		@Override
		public void actionPerformed() {
			if(buildingString != null)
			{
				builder.setMode(CityBuilder.MODE_BUILDS);
				builder.setBuildingString(buildingString);
			}
			categButton.select(false);
		}
	}
	
	class TogglePauseAction implements IActionListener
	{
		@Override
		public void actionPerformed() {
			togglePause();
		}	
	}
	
	class QuitGameAction implements IActionListener
	{
		@Override
		public void actionPerformed() {
			closeRequested = true;
		}
	}

}


