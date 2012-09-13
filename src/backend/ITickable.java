package backend;

/**
 * Objects implementing this interface may have a scheduled update system,
 * where tick() is called regularly from a certain time interval.
 * Trivia : "tick" is a reference to clocks.
 * @author Marc
 *
 */
public interface ITickable
{
	/**
	 * Returns the time interval between each tick in millisecon
	 * @return
	 */
	public int getTickTime();
	
	/**
	 * Gets time left before next tick in milliseconds
	 * @return
	 */
	public int getTimeBeforeNextTick();
	
	/**
	 * Updates the object when it's time.
	 */
	public void tick();
	
}

