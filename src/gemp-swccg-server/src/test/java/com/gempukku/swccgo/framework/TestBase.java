package com.gempukku.swccgo.framework;

import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.DefaultSwccgGame;
import com.gempukku.swccgo.logic.timing.DefaultUserFeedback;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This interface holds all the static definitions used throughout the test rig.  It is not a true interface, but
 * since Java does not support partial classes this will have to do.
 */
public interface TestBase {
	/**
	 * The Dark Side player's name
	 */
	String DS = "Dark Side Player";
	/**
	 * The Light Side player's name
	 */
	String LS = "Light Side Player";

	/**
	 * A constant used for performing floating-point numeric comparisons in test assertions.  In effect, any decimal
	 * difference smaller than this amount will be completely ignored when determining if two floating point numbers
	 * are "equal".
	 */
	double epsilon = 0.001;

	/**
	 * An empty outside-of-deck pile communicating that the Dark Side will have no shields or other out-of-game cards
	 * for a particular test scenario.
	 */
	HashMap<String, String> NoDSShields = new HashMap<>();
	/**
	 * An empty outside-of-deck pile communicating that the Light Side will have no shields or other out-of-game cards
	 * for a particular test scenario.
	 */
	HashMap<String, String> NoLSShields = new HashMap<>();

	/**
	 * An empty collection communicating that the Dark Side will have no objectives or starting interrupts played at
	 * the start of a particular test scenario.
	 */
	HashMap<String, String> NoDSStarters = new HashMap<>();
	/**
	 * An empty collection communicating that the Light Side will have no objectives or starting interrupts played at
	 * the start of a particular test scenario.
	 */
	HashMap<String, String> NoLSStarters = new HashMap<>();


	/*
	 * The default locations to be played by either player.  This will be included in the deck and automatically played
	 * at the start of the game.
	 *
	 * When choosing default locations, be sure to pick ones that leave both sides with identical lightsaber icons,
	 * whether that means that both are the same or that one is 1/2 and the other is 2/1.  This way testers do not need
	 * to remember nuances around default activation amounts being different per-side.
	 *
	 * Also ensure that any defaults that are chosen avoid altering the game state; ideally the sites would be blank,
	 * but if that cannot be arranged do not choose cards that grant modifiers (granting +/- X to any stat), as these
	 * inevitably are rakes that future tests will step on. Instead, fall back on locations that have optional actions
	 * that can be ignored.
	 */

	/**
	 * The default ground location used by Dark Side.  This will be played at the start of the game automatically.
	 */
	String DefaultGroundDSLocation = "12_176"; // Tatooine: Marketplace
	/**
	 * The default ground location used by Light Side.  This will be played at the start of the game automatically.
	 */
	String DefaultGroundLSLocation = "5_079"; // Cloud City: Chasm Walkway


	/**
	 * The default space system used by Dark Side.  This will be played at the start of the game automatically.
	 */
	String DefaultSpaceDSSystem = "1_282"; // Dantooine
	/**
	 * The default space system used by Light Side.  This will be played at the start of the game automatically.
	 */
	String DefaultSpaceLSSystem = "6_087"; // Tibrin


	/**
	 * @return Gets the default starting location that was played automatically for Dark Side using the test rig setup.
	 * If you manually played a starting location, this may not be coherent.
	 */
	default PhysicalCardImpl GetDSStartingLocation() { return GetDSCard("starting-location"); }
	/**
	 * @return Gets the default starting location that was played automatically for Light Side using the test rig setup.
	 * If you manually played a starting location, this may not be coherent.
	 */
	default PhysicalCardImpl GetLSStartingLocation() { return GetLSCard("starting-location"); }

	/**
	 * The default filler card to use for Force in the Dark Side deck.  These should essentially be ignored by tests
	 * except for activating and testing draw effects.
	 */
	String DefaultDSFiller = "1_194"; // Stormtrooper
	/**
	 * The default filler card to use for Force in the Light Side deck.  These should essentially be ignored by tests
	 * except for activating and testing draw effects.
	 */
	String DefaultLSFiller = "1_28"; // Rebel Trooper

	default PhysicalCardImpl GetDSFiller(int num) {
		return GetDSCard("filler-" + String.format("%02d", num));
	}

	default PhysicalCardImpl GetLSFiller(int num) {
		return GetLSCard("filler-" + String.format("%02d", num));
	}

	default List<PhysicalCardImpl> GetDSFillerRange(int count) { return GetDSFillerRange(1, count); }
	default List<PhysicalCardImpl> GetDSFillerRange(int first, int last) {
		var fillers = new ArrayList<PhysicalCardImpl>();
		for(int i = first; i <= last; ++i) {
			fillers.add(GetDSCard("filler-" + String.format("%02d", i)));
		}
		return fillers;
	}

	default PhysicalCardImpl[] GetLSFillerRange(int count) { return GetLSFillerRange(1, count); }
	default PhysicalCardImpl[] GetLSFillerRange(int first, int last) {
		var fillers = new ArrayList<PhysicalCardImpl>();
		for(int i = first; i <= last; ++i) {
			fillers.add(GetLSCard("filler-" + String.format("%02d", i)));
		}
		return fillers.toArray(new PhysicalCardImpl[]{});
	}


	/**
	 * A set of destiny cards that can be used to manipulate the exact outcome. By default these cards are included
	 * in the DS deck, but are removed from the game during startup.  If you wish to use them, use the helper functions
	 * below.
	 */
	HashMap<String, String> DSDestinyPack = new HashMap<>() {{
		put("ds-destiny-0", "4_154"); // Anoat
		put("ds-destiny-1", "1_194"); // Stormtrooper
		put("ds-destiny-2", "216_1"); // A Sith Legend
		put("ds-destiny-3", "1_208"); // A Disturbance in the Force
		put("ds-destiny-4", "7_218"); // A Bright Center to the Universe
		put("ds-destiny-5", "11_068"); // A Million Voices Crying Out
		put("ds-destiny-6", "7_219"); // A Day Long Remembered
		put("ds-destiny-7", "8_120"); // Closed Door
	}};

	/**
	 * A set of destiny cards that can be used to manipulate the exact outcome. By default these cards are included
	 * in the LS deck, but are removed from the game during startup.  If you wish to use them, use the helper functions
	 * below.
	 */
	HashMap<String, String> LSDestinyPack = new HashMap<>() {{
		put("ls-destiny-0", "211_48"); // Ahch-to
		put("ls-destiny-1", "1_28"); // Rebel Trooper
		put("ls-destiny-2", "202_7"); // Azure Angel
		put("ls-destiny-3", "9_62"); // A-wing
		put("ls-destiny-4", "6_52"); // A Gift
		put("ls-destiny-5", "221_43"); // A Jedi's Fury
		put("ls-destiny-6", "6_49"); // Arc Welder
		put("ls-destiny-7", "5_12"); // Bionic Hand
	}};

	/**
	 * Gets a card with a particular destiny number to be placed on the Dark Side player's Reserve Deck.
	 * @param amount An amount from 0-7 to retrieve.
	 * @return A reference to a card with the appropriate destiny number.
	 */
	default PhysicalCardImpl GetDSDestiny(int amount) {
		if(amount < 0 || amount > 7)
			throw new IllegalArgumentException("Default destiny pack only supports amounts from 0-7.");

		return GetDSCard("ds-destiny-" + amount);
	}

	/**
	 * Gets a card with a particular destiny number to be placed on the Light Side player's Reserve Deck.
	 * @param amount An amount from 0-7 to retrieve.
	 * @return A reference to a card with the appropriate destiny number.
	 */
	default PhysicalCardImpl GetLSDestiny(int amount) {
		if(amount < 0 || amount > 7)
			throw new IllegalArgumentException("Default destiny pack only supports amounts from 0-7.");

		return GetLSCard("ls-destiny-" + amount);
	}


	/*
	 * If other formats come in handy, those can also be listed here.
	 */
	/**
	 * The Open format name.
	 */
	String Open = "open";


	/*
	 * These three functions are used in the base interfaces but are unnecessary in the actual implementation, where the
	 * underlying fields can be used instead.
	 */

	/**
	 * @return Gets the virtual game table used by the test scenario.
	 */
	DefaultSwccgGame game();

	/**
	 * @return Gets the game state of the virtual table used by the test scenario.
	 */
	GameState gameState();

	/**
	 * @return Gets the user decision manager.  This contains information relating to the decision that Gemp is
	 * currently waiting on.
	 */
	DefaultUserFeedback userFeedback();


	/**
	 * Retrieves a Dark Side card from the game state.
	 * @param cardName The human-readable shorthand name that was assigned to the card in the GetScenario call.
	 * @return The physical card representation of this card.
	 * @throws IllegalArgumentException Thrown if the name provided cannot be found.
	 */
	PhysicalCardImpl GetDSCard(String cardName);
	/**
	 * Retrieves a Light Side card from the game state.
	 * @param cardName The human-readable shorthand name that was assigned to the card in the GetScenario call.
	 * @return The physical card representation of this card.
	 * @throws IllegalArgumentException Thrown if the name provided cannot be found.
	 */
	PhysicalCardImpl GetLSCard(String cardName);

	/**
	 * Used after a revert to avoid stale references.
	 */
	void ResetGameState();
}
