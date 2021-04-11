package org.lsmr.selfcheckout.software;

import org.lsmr.selfcheckout.Card;

/**
 * Represents a Member at a store.
 */
public class Member {
    private String name = null;
    private Card card = null;
    private String memberID = null;
    private int points = 0;
    
    /**
     * Get an instance of Member. 
     * 
     * @param name
     *          The name of the Member.
     * @param memberID
     *          The Member's ID.
     */
    public Member(String name, String memberID) {
        if(name == null || memberID == null) throw new NullPointerException("No argument may be null."); 
        this.name = name;
        this.memberID = memberID;
        this.card = new Card("Membership Card", memberID, name, null, null, false, false);
    }

    /**
     * Add points to the Member's account.
     * 
     * @param newPoints
     *          The points to be added.
     */
    public void addPoints(int newPoints) {
        if(newPoints <= 0) {
            throw new IllegalArgumentException("The points to add should be greater than 0.");
        }
        this.points += newPoints;
    }

    /**
     * Redeem points from the Member's account.
     * 
     * @param redeemedPoints
     *          The points to be redeemed.
     */
    public void redeemPoints(int redeemedPoints) {
        if(redeemedPoints <= 0) {
            throw new IllegalArgumentException("The points to redeem should be greater than 0.");
        }
        if(this.points < redeemedPoints) {
            throw new IllegalArgumentException("This member does not have enough points.");
        }
        this.points -= redeemedPoints;
    }

    /**
     * Get the Member's name.
     * 
     * @return The Member's name.
     */
    public String getName() { return this.name; }

    /**
     * Get the Member's ID.
     * 
     * @return The Member's ID.
     */
    public String getMemberID() { return this.memberID; }

    /**
     * Get the Member's Card.
     * 
     * @return The Member's Card.
     */
    public Card getMemberCard() { return this.card; }

    /**
     * Get the Member's points.
     * 
     * @return The Member's points.
     */
    public int getPoints() { return this.points; }
}
