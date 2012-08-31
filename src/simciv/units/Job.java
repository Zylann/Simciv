package simciv.units;

import simciv.Map;
import simciv.builds.Workplace;

/**
 * List of all jobs associated with citizen units.
 * @author Marc
 *
 */
public abstract class Job
{
	// Job IDs
	public static final byte EMPLOYER = 0;
	public static final byte CONVEYER = 1;
	public static final byte TAXMAN = 2;
	public static final byte MARKET_DELIVERY = 3;
	public static final byte ARCHITECT = 4;
	
	public static Citizen createUnitFromJobID(byte jobID, Map m, Workplace w)
	{
		switch(jobID)
		{
		case CONVEYER : 		return new Conveyer(m, w);
		case TAXMAN : 			return new Taxman(m, w);
		case MARKET_DELIVERY : 	return new MarketDelivery(m, w);
		case ARCHITECT : 		return new Architect(m, w);
		
		default :
			System.out.println("ERROR: unknown job ID: " + jobID);
			return null;
		}
	}
	
}




