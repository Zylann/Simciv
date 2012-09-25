package backend;

import java.awt.Canvas;

import javax.swing.SwingUtilities;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

/**
 * A game container that displays the game on an AWT Canvas.
 * (slightly modified by Marc Gilleron to support a new Exception handler)
 * 
 * @author kevin
 */
public class CanvasGameContainer2 extends Canvas {
	
	private static final long serialVersionUID = 1L;
	
	/** The actual container implementation */
	protected Container container;
	/** The game being held in this container */
	protected Game game;

	/**
	 * Create a new panel
	 * 
	 * @param game The game being held
	 * @throws SlickException Indicates a failure during creation of the container
	 */
	public CanvasGameContainer2(Game game) throws SlickException {
		this(game, false);
	}

	/**
	 * Create a new panel
	 * 
	 * @param game The game being held
	 * @param shared True if shared GL context should be enabled. This allows multiple panels
	 * to share textures and other GL resources.
	 * @throws SlickException Indicates a failure during creation of the container
	 */
	public CanvasGameContainer2(Game game, boolean shared) throws SlickException {
		super();

		this.game = game;
		setIgnoreRepaint(true);
		requestFocus();
		setSize(500,500);
		
		container = new Container(game, shared);
		container.setForceExit(false);
	}

	/**
	 * Start the game container rendering
	 * 
	 * @throws SlickException Indicates a failure during game execution
	 */
	public void start() throws SlickException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Input.disableControllers();
					
					try {
						Display.setParent(CanvasGameContainer2.this);
					} catch (LWJGLException e) {
						throw new SlickException("Failed to setParent of canvas", e);
					}
					
					container.setup();
					scheduleUpdate();
				} catch (SlickException e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
		});
	}
	
	/**
	 * Schedule an update on the EDT
	 */
	private void scheduleUpdate() {
		if (!isVisible()) {
			return;
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					container.gameLoop();
				} catch (SlickException e) {
					e.printStackTrace();
				}
				container.checkDimensions();
				scheduleUpdate();
			}
		});
	}
	/**
	 * Dispose the container and any resources it holds
	 */
	public void dispose() {
	}

	/**
	 * Get the GameContainer providing this canvas
	 * (renamed from getContainer() of Slick2D)
	 * 
	 * @return The game container providing this canvas
	 */
	public GameContainer getGameContainer() {
		return container;
	}
	
	/**
	 * Sets the top exception listener 
	 * (Added by Marc Gilleron)
	 * @param l
	 */
	public void setTopExceptionListener(ITopExceptionListener l) {
		if(container != null)
			container.topExceptionListener = l;
	}

	/**
	 * A game container to provide the canvas context
	 * 
	 * @author kevin
	 */
	private class Container extends AppGameContainer {
		/** Listener for exceptions occurring on top level of the game execution **/
		public ITopExceptionListener topExceptionListener; // Added by Marc Gilleron
		
		/**
		 * Create a new container wrapped round the game
		 * 
		 * @param game
		 *            The game to be held in this container
		 * @param shared True if shared GL context should be enabled. This allows multiple panels
		 * to share textures and other GL resources.
		 * @throws SlickException Indicates a failure to initialise
		 */
		public Container(Game game, boolean shared) throws SlickException {
			super(game, CanvasGameContainer2.this.getWidth(), CanvasGameContainer2.this.getHeight(), false);

			width = CanvasGameContainer2.this.getWidth();
			height = CanvasGameContainer2.this.getHeight();
			
			if (shared) {
				enableSharedContext();
			}
		}

		/**
		 * Updated the FPS counter
		 */
		protected void updateFPS() {
			super.updateFPS();
		}

		/**
		 * @see org.newdawn.slick.GameContainer#running()
		 */
		protected boolean running() {
			return super.running() && CanvasGameContainer2.this.isDisplayable();
		}

		/**
		 * @see org.newdawn.slick.GameContainer#getHeight()
		 */
		public int getHeight() {
			return CanvasGameContainer2.this.getHeight();
		}

		/**
		 * @see org.newdawn.slick.GameContainer#getWidth()
		 */
		public int getWidth() {
			return CanvasGameContainer2.this.getWidth();
		}

		/**
		 * Check the dimensions of the canvas match the display
		 */
		public void checkDimensions() {
			if ((width != CanvasGameContainer2.this.getWidth()) ||
			    (height != CanvasGameContainer2.this.getHeight())) {
				
				try {
					setDisplayMode(CanvasGameContainer2.this.getWidth(), 
								   CanvasGameContainer2.this.getHeight(), false);
				} catch (SlickException e) {
					Log.error(e);
				}
			}
		}
		
		// Added by Marc Gilleron
		@Override
		protected void setup() throws SlickException {
			super.setup();
		}
		
		// Added by Marc Gilleron
		@Override
		protected void gameLoop() throws SlickException {
			
			// Note : this is almost the same code as AppGameContainer
			
			// The game will be updated only if it is running
			if(running) // Added by Marc Gilleron
			{
				int delta = getDelta();
				if (!Display.isVisible() && updateOnlyOnVisible) {
					try { Thread.sleep(100); } catch (Exception e) {}
				} else {
					try {
						updateAndRender(delta);
					} catch (SlickException e) {
						Log.error(e);
						running = false;
						
						// Added by Marc Gilleron
						if(topExceptionListener != null)
							topExceptionListener.onTopException(e);
						
						return;
					}
				}
			}

			// The display stills updated to prevent app freezing
			
			updateFPS();

			Display.update();
			
			if (Display.isCloseRequested()) {
				if (game.closeRequested()) {
					running = false;
				}
			}
		}
		
	}
}

