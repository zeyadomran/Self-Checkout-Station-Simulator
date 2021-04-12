package org.lsmr.selfcheckout.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.software.Member;
import org.lsmr.selfcheckout.software.MemberDatabase;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class MemberTest {
    SelfCheckoutSoftware control = null;

    /* Initialize a new SelfCheckoutSoftware object before each test. */
    @Before
    public void init() {
        Currency c = Currency.getInstance(Locale.CANADA);
        int[] noteDenom = {5, 10, 20, 50, 100};
        BigDecimal[] coinDenom = {new BigDecimal("0.05"), new BigDecimal("0.1"), new BigDecimal("0.25"), new BigDecimal("0.5"), new BigDecimal("1"), new BigDecimal("2")};
        SelfCheckoutStation s = new SelfCheckoutStation(c, noteDenom, coinDenom, 10000, 1);
        control = new SelfCheckoutSoftware(s);
    }

    /* Tests the expected use of adding a member. */
    @Test
    public void addMemberExpectedTest() {
        String name = "Zeyad";
        String id = "1234";
        Member member = new Member(name, id);
        control.addMember(name, id);
        assertEquals(member.getName(), MemberDatabase.REGISTERED_MEMBERS.get("1234").getName());
    }

    /* Tests if a null member is added. */
    @Test (expected = NullPointerException.class)
    public void addMemberNullTest() {
        control.addMember(null, null);
    }

    /* Tests if the same member is added twice. */
    @Test (expected = IllegalArgumentException.class)
    public void addMemberExistsTest() {
        String name = "Zeyad";
        String id = "1234";
        control.addMember(name, id);
        control.addMember(name, id);
    }

    /* Tests the expected use of remove member. */
    @Test
    public void removeMemberExpectedTest() {
        String name = "Zeyad";
        String id = "1234";
        control.addMember(name, id);
        control.removeMember( id);
        assertFalse(MemberDatabase.REGISTERED_MEMBERS.containsKey("1234"));
    }

    /* Tests removing a null member. */
    @Test (expected = NullPointerException.class)
    public void removeMemberNullTest() {
        control.removeMember(null);
    }

    /* Tests removing a member that does not exist. */
    @Test (expected = IllegalArgumentException.class)
    public void removeMemberDNETest() {
        control.removeMember("1234");
    }

    /* Tests the expected use of swiping a membership card. */
    @Test
    public void swipeMembershipCardExpectedTest() {
        int success = 0;
        String name = "Zeyad";
        String id = "1234";
        control.addMember(name, id);
        Card card = MemberDatabase.REGISTERED_MEMBERS.get(id).getMemberCard();
        for(int i = 0; i < 100; i++) { // Testing for randomness
        	try
        	{
        		if(control.swipeMembershipCard(card)) success += 1;
        	}
        	catch(IllegalArgumentException e)
        	{
        		//ignore
        	}
        }
        if(success < 50) fail();
    }

    /* Tests swiping a null membership card. */
    @Test (expected = NullPointerException.class)
    public void swipeMembershipCardNullTest() {
        control.swipeMembershipCard(null);
    }

    /* Tests swiping a membership card not assigned to a member. */
    @Test (expected = IllegalArgumentException.class)
    public void swipeMembershipCardDNETest() {
        Card card = new Card("Membership Card", "1213", "Zeyad", null, null, false, false);
        control.swipeMembershipCard(card);
    }

    /* Tests creating a null member. */
    @Test (expected = NullPointerException.class)
    public void createNullMemberTest() {
        new Member(null, null);
    }

    /* Tests getting a member's name. */
    @Test
    public void getMemberNameTest() {
        Member member = new Member("Zeyad", "1234");
        assertEquals("Zeyad", member.getName());
    }

    /* Tests adding points to a member. */
    @Test
    public void addPointsMemberExpectedTest() {
        Member member = new Member("Zeyad", "1234");
        member.addPoints(10);
        assertEquals(10, member.getPoints());
    }

    /* Tests adding 0 points to a member. */
    @Test (expected = IllegalArgumentException.class)
    public void addPointsMember0Test() {
        Member member = new Member("Zeyad", "1234");
        member.addPoints(0);
    }

    /* Tests redeeming points from a member. */
    @Test
    public void redeemPointsMemberExpectedTest() {
        Member member = new Member("Zeyad", "1234");
        member.addPoints(10);
        member.redeemPoints(5);
        assertEquals(5, member.getPoints());
    }

    /* Tests redeeming 0 points from a member. */
    @Test (expected = IllegalArgumentException.class)
    public void redeemPointsMember0Test() {
        Member member = new Member("Zeyad", "1234");
        member.redeemPoints(0);
    }

    /* Tests redeeming more points than the member has. */
    @Test (expected = IllegalArgumentException.class)
    public void redeemPointsMemberNotEnoughTest() {
        Member member = new Member("Zeyad", "1234");
        member.redeemPoints(1);
    }

    /* Tests entering valid membership info. */
    @Test
    public void enterMembershipInfoTestValid() {
    	control.addMember("Bob", "1234");
    	assertTrue(control.enterMembershipInfo("1234"));
    	assertEquals("1234", control.getCurrentMember());
    }
    
    /* Tests entering invalid membership info. */
    @Test (expected = IllegalArgumentException.class)
    public void enterMembershipInfoTestInvalid() {
    	control.enterMembershipInfo("1234");
    }
    
    
    /* Tests entering membership info. */
    @Test (expected = NullPointerException.class)
    public void enterMembershipInfoTestNull() {
    	control.enterMembershipInfo(null);
    }
    
    
    /* Clear the REGISTERED_MEMBERS and SelfCheckoutSoftware instance */
    @After
    public void Teardown() {
        control = null;
        MemberDatabase.REGISTERED_MEMBERS.clear();
    }

}
