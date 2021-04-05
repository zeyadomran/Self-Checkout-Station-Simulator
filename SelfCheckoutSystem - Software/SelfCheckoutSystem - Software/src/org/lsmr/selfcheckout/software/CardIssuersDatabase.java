package org.lsmr.selfcheckout.software;

import org.lsmr.selfcheckout.external.CardIssuer;

/**
 * Represents a cheap and dirty version of a database of CardIssuers that the
 * simulation can interact with.
 */
public class CardIssuersDatabase {
    /**
	 * Instances of this class are not needed, so the constructor is private.
	 */
    private CardIssuersDatabase() {}

    /**
     * Credit Card Issuer.
     */
    public static final CardIssuer CREDIT_CARD_ISSUER = new CardIssuer("Credit");
    
    /**
     * Debit Card Issuer.
     */
    public static final CardIssuer DEBIT_CARD_ISSUER = new CardIssuer("Debit");

}
