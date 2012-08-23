package simciv.builds;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import simciv.Entity;
import simciv.Game;
import simciv.Map;
import simciv.MathHelper;
import simciv.ResourceSlot;
import simciv.World;
import simciv.content.Content;
import simciv.effects.SmokeExplosion;

// FIXME sometimes, the map is left unconstructible. Bug origin is unknown...

/**
 * Each object that can be constructed by the player.
 * Buildings cannot move.
 * @author Marc
 *
 */
public abstract class Build extends Entity
{
	// Common states
	public static final byte STATE_CONSTRUCTION = 0;
	public static final byte STATE_NORMAL = 1;
	public static final byte STATE_ACTIVE = 2;
	
	// Solidness
	protected int solidness;
	
	public Build(World w)
	{
		super(w);
		state = STATE_NORMAL;
		solidness = getSolidnessMax();
	}
	
	public abstract BuildProperties getProperties();
	
	public Color getMinimapColor()
	{
		return BuildCategory.get(getProperties().category).getColor();
	}
	
	/**
	 * Destroys the building (as gameplay meaning, for example with a bomb) with destruction effects.
	 * Note : to erase a building without "destroying" it, use dispose().
	 */
	public void destroy()
	{
		// Remove the building from the world
		dispose();

		// Make a sound
		Content.sounds.buildCollapse.play((float) (1.0 + MathHelper.randS(0.1f)), 0.5f);
		
		// Leave ruins
		for(int y = posY; y < posY + getHeight(); y++)
		{
			for(int x = posX; x < posX + getWidth(); x++)
			{
				Debris d = new Debris(worldRef);
				d.setPropertiesFromBuild(this);
				worldRef.placeBuild(d, x, y);
				
				worldRef.addGraphicalEffect(
						new SmokeExplosion(x, y, 8, 1.5f, Game.tilesSize/2));
			}
		}
	}
	
	@Override
	public void dispose()
	{
		worldRef.map.markBuilding(this, false);
		super.dispose();
	}
	
	@Override
	protected void tickEntity()
	{
		tickSolidness();
		super.tickEntity();
	}

	protected void tickSolidness()
	{
		if(Math.random() < 0.5)
			solidness--;
		if(solidness == 0)
			destroy();
	}
	
	public final boolean needsMaintenance()
	{
		return solidness < getMaintenanceThreshold();
	}
	
	public int getMaintenanceThreshold()
	{
		return 200;
	}
	
	public int getSolidnessMax()
	{
		return 250;
	}
	
	/**
	 * Returns the floating solidness ratio in [0,1].
	 * @return
	 */
	public float getSolidnessRatio()
	{
		return (float)solidness / (float)getSolidnessMax();
	}
	
	/**
	 * Called each time an architect passes near the building.
	 * @return true if success, false if not maintainable
	 */
	public abstract boolean onMaintenance();
	
	/**
	 * Restores the building's HP and solidness
	 */
	public void repair()
	{
		solidness = getSolidnessMax();
		healthPoints = 100;
	}
	
	/**
	 * Returns the on-floor width of the building in cells
	 * @return
	 */
	public final int getWidth()
	{
		return getProperties().width;
	}
	
	/**
	 * Returns the on-floor height of the building in cells
	 * @return
	 */
	public final int getHeight()
	{
		return getProperties().height;
	}
	
	/**
	 * Returns the 3D-height of the building in cells
	 * (can be used for rendering or future stuff)
	 * @return
	 */
	public final int getZHeight()
	{
		return getProperties().zHeight;
	}
	
	/**
	 * Returns true if the building occupies only one cell on the floor
	 * @return
	 */
	public final boolean is1x1()
	{
		return getProperties().width == 1 && getProperties().height == 1;
	}
	
	public final void renderAsConstructing(Graphics gfx)
	{
		// TODO handle size upper than 1x1
		gfx.drawImage(Content.images.buildConstructing1x1, 0, 0);
	}

	/**
	 * Returns true if the building is a house (class House)
	 * @return
	 */
	public boolean isHouse()
	{
		return false;
	}

	/**
	 * Returns true if the building is a workplace (inherits Workplace)
	 * @return
	 */
	public boolean isWorkplace()
	{
		return false;
	}
	
	@Override
	public boolean isVisible()
	{
		return true;
	}
	
	/**
	 * Returns a brief information about the building
	 * @return
	 */
	public abstract String getInfoString();

	/**
	 * Return true if the building can store resources
	 * @return
	 */
	public boolean isAcceptResources()
	{
		return false;
	}

	/**
	 * Stores a resource in the building. Depending on if the building is accepting
	 * resources, the given slot will or will not be modified.
	 * @param r : resource to store
	 */
	public void storeResource(ResourceSlot r)
	{
	}
	
	@Override
	public final void render(GameContainer gc, StateBasedGame game, Graphics gfx)
	{
		gfx.pushTransform();
		gfx.translate(posX * Game.tilesSize, posY * Game.tilesSize);
				
		renderBuilding(gc, game, gfx);
		
		if(gc.getInput().isKeyDown(Input.KEY_4))
			renderSolidnessRatio(gfx, 0, 0);

		gfx.popTransform();
	}
	
	/**
	 * Draws a progress bar representing the solidness ratio.
	 * @param gfx
	 * @param x : relative x bar pos in pixels
	 * @param y : relative y bar pos in pixels
	 */
	private void renderSolidnessRatio(Graphics gfx, int x, int y)
	{
		float w = (float)(getWidth() * Game.tilesSize - 1);
		float t = getSolidnessRatio() * w;
		gfx.setColor(Color.white);
		gfx.fillRect(x, y, t, 2);
		gfx.setColor(Color.darkGray);
		gfx.fillRect(x + t, y, w - t, 2);
	}

	protected void renderDefault(Graphics gfx, Image sprite)
	{
		gfx.drawImage(sprite, 0, -getZHeight() * Game.tilesSize);
	}

	protected abstract void renderBuilding(GameContainer gc, StateBasedGame game, Graphics gfx);

	/**
	 * Determines if the building can be placed on a certain position on the map.
	 * @param map
	 * @param x : origin X in cells
	 * @param y : origin Y in cells
	 * @return true if can be placed, false otherwise
	 */
	public boolean canBePlaced(Map map, int x, int y)
	{
		return map.canPlaceObject(x, y, getWidth(), getHeight());
	}

	@Override
	protected int getTickTime()
	{
		return 1000; // default is 1s
	}

	/**
	 * Returns the surface occupied by the building on the floor in cells^2
	 * @return
	 */
	public int getSurfaceArea()
	{
		return getWidth() * getHeight();
	}
	
}

