package com.gempukku.swccgo.framework;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCardImpl;

import javax.xml.stream.Location;

import static com.gempukku.swccgo.framework.TestBase.epsilon;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Assertions {

	/**
	 * Asserts that one or more cards are all "at" the provided location.  If this is not the case, an AssertionError
	 * will be thrown with no message.
	 * @param location The location to check.
	 * @param cards One more cards which must all be at that location.
	 */
	public static void assertAtLocation(PhysicalCardImpl location, PhysicalCardImpl...cards) {
		for(var card : cards) {
			assertEquals(Zone.AT_LOCATION, card.getZone());
			assertEquals(location, card.getAtLocation());
		}
	}

	/**
	 * Asserts that none of the provided cards are "at" the provided location.  If this is not the case, an AssertionError
	 * will be thrown with no message.
	 * @param location The location to check.
	 * @param cards One more cards which must all not be at that location.
	 */
	public static void assertNotAtLocation(PhysicalCardImpl location, PhysicalCardImpl...cards) {
		for(var card : cards) {
			assertTrue(Zone.AT_LOCATION != card.getZone() || location != card.getAtLocation());
		}
	}

	/**
	 * Asserts that one or more cards are all contained within the provided zone.  If any are not there, an AssertionError
	 * will be thrown with no message.
	 * @param zone The zone to check.
	 * @param cards One or more cards which must all be in that zone.
	 */
	public static void assertInZone(Zone zone, PhysicalCardImpl...cards) {
		for(var card : cards) {
			//This makes it so that being in the "top of reserve pile" is treated as equivalent
			// to "reserve pile", etc.
			assertEquals(zone.getHumanReadable(), card.getZone().getHumanReadable());
		}
	}

	/**
	 * Asserts that one or more cards are all in either player's hand.  If any are in any other zone, an AssertionError
	 * will be thrown with no message.
	 * @param cards One or more cards which must all be in hand.
	 */
	public static void assertInHand(PhysicalCardImpl...cards) { assertInZone(Zone.HAND, cards); }

    /**
     * Asserts that a float value can safely be considered an int.
     * Catches cases where non-int values are used (Braniac, etc)
     * @param value The float to check.
     */
    public static void assertIsInt(float value) {
        assertTrue("Value cannot safely be treated as int",Math.abs(value - Math.round(value))  < epsilon);
    }

}

