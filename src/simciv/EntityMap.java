package simciv;

import org.newdawn.slick.util.Log;

import backend.GameComponent;
import backend.GameComponentMap;

/**
 * GameComponentMap specialized for Entities.
 * Redefines stage/get methods to ensure that only entities will be contained in.
 * @author Marc
 *
 */
public class EntityMap extends GameComponentMap
{
	/** Use stageEntity instead (ensures that this container only contains Entities) **/
	@Override @Deprecated
	public final void stageComponent(GameComponent e)
	{
		if(Entity.class.isInstance(e))
			super.stageComponent(e);
		else
			Log.info("cannot add a non-entity to an EntityMap !");
	}

	/**
	 * Stages an entity into the EntityMap.
	 * @param e : the entity to add
	 */
	public void stageEntity(Entity e)
	{
		super.stageComponent(e);
	}

	public Entity get(int ID)
	{
		return (Entity)super.get(ID);
	}

}


