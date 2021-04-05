package org.lsmr.selfcheckout.software;

import java.util.HashMap;
import java.util.Map;

public class AttendantDatabase {
	
	 /**
		 * Instances of this class are not needed, so the constructor is private.
		 */
		private AttendantDatabase() {}

		/**
	     * Map Containing Registered Members.
	     */
	    public static final Map<String, Attendant> REGISTERED_ATTENDANTS= new HashMap<>();

}
