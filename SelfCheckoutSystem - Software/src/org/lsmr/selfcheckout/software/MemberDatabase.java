package org.lsmr.selfcheckout.software;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a cheap and dirty version of a database of members that the
 * simulation can interact with.
 */
public class MemberDatabase {
    /**
	 * Instances of this class are not needed, so the constructor is private.
	 */
	private MemberDatabase() {}

	/**
     * Map Containing Registered Members.
     */
    public static final Map<String, Member> REGISTERED_MEMBERS = new HashMap<>();

}