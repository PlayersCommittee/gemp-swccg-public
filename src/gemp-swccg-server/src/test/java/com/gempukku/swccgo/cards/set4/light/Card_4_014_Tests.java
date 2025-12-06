package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.querying.Battle;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class Card_4_014_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("suit", "4_014");
					put("suit2", "4_014");

					put("biggs", "1_3"); //Pilot, ability 2
					put("luke", "1_19"); //Pilot, ability 4
					put("zev", "3_27"); //Zev Senesca; maneuver +3 and draws 1 battle destiny when piloting Rogue 2
					put("leia", "1_017"); //non-pilot

					put("ywing", "1_147"); //starfighter
					put("rogue2", "3_67"); //Zev's snowspeeder; combat vehicle
					put("shuttle", "8_79"); //shuttle Tydirium
					put("landspeeder", "1_149"); //non-combat vehicle
					put("corvette", "1_140"); //Corellian Corvette; non-starfighter starship

					put("red10", "7_145"); //Immune to attrition <4 when matching pilot aboard

					put("captain", "7_038"); //Ralltiir Freighter Captain; nonunique and maneuver +1 when piloting
					put("captain2", "7_038");

					put("falcon", "1_143"); //Millenium Falcon
					put("han", "1_11"); //When piloting Falcon, also adds 2 to maneuver and may draw one battle destiny if not able to otherwise.
					put("chewie", "2_3"); //When piloting Falcon, also adds 1 to maneuver.
				}},
				new HashMap<>()
				{{
					put("tie", "1_304");
					put("vader", "7_175");

					put("lennox", "3_84"); //Captain Lennox, matches with the Star Destroyer Tyrant
					put("tyrant", "3_153");
				}},
				10,
				10,
				StartingSetup.DefaultLSSpaceSystem,
				StartingSetup.DefaultDSSpaceSystem,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void RebelFlightSuitStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Rebel Flight Suit
		 * Uniqueness: Unrestricted
		 * Side: Light
		 * Type: Device
		 * Destiny: 5
		 * Game Text: Deploy on your pilot character.  While piloting any starfighter, combat vehicle, or shuttle
		 * 			vehicle, that character is considered to be the "matching pilot" (pilot adds 2 to maneuver (limit +2)
		 * 			and draws one battle destiny if not able to otherwise).
		 * Lore: Pilot fatigues feature digital technology which can be customized for particular starfighters.
		 * 			Increases interface efficiency with a newly assigned craft.
		 * Set: Dagobah
		 * Rarity: C
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("suit").getBlueprint();

		assertEquals("Rebel Flight Suit", card.getTitle());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertTrue(card.isCardType(CardType.DEVICE));
		assertEquals(5, card.getDestiny(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.DAGOBAH));
	}


	@Test
	public void RebelFlightSuitDeploysOnCharacterWithPilot() {
		var scn = GetScenario();

		var suit = scn.GetLSCard("suit");
		var biggs = scn.GetLSCard("biggs");
		var luke = scn.GetLSCard("luke");
		var leia = scn.GetLSCard("leia");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToHand(suit);
		scn.MoveCardsToLocation(site, biggs, luke, leia);

		scn.SkipToLSTurn(Phase.DEPLOY);

		assertTrue(scn.LSDeployAvailable(suit));
		scn.LSDeployCard(suit);

		assertTrue(scn.LSHasCardChoicesAvailable(biggs, luke));
		assertFalse(scn.LSHasCardChoicesAvailable(leia));
	}

	@Test
	public void RebelFlightSuitGrantsMatchingStatusToBearerWhilePilotingStarfighter() {
		var scn = GetScenario();

		var suit = scn.GetLSCard("suit");
		var biggs = scn.GetLSCard("biggs");
		var ywing = scn.GetLSCard("ywing");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, ywing);
		scn.BoardAsPilot(ywing, biggs);

		assertFalse(scn.IsMatchingPilot(ywing, biggs));

		scn.AttachCardsTo(biggs, suit);

		assertTrue(scn.IsMatchingPilot(ywing, biggs));
	}

	@Test
	public void RebelFlightSuitGrantsMatchingStatusToBearerWhilePilotingCombatVehicle() {
		var scn = GetScenario();

		var suit = scn.GetLSCard("suit");
		var biggs = scn.GetLSCard("biggs");
		var snowspeeder = scn.GetLSCard("rogue2");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, snowspeeder);
		scn.BoardAsPilot(snowspeeder, biggs);

		assertFalse(scn.IsMatchingPilot(snowspeeder, biggs));

		scn.AttachCardsTo(biggs, suit);

		assertTrue(scn.IsMatchingPilot(snowspeeder, biggs));
	}

	@Test
	public void RebelFlightSuitGrantsMatchingStatusToBearerWhilePilotingShuttleVehicle() {
		var scn = GetScenario();

		var suit = scn.GetLSCard("suit");
		var biggs = scn.GetLSCard("biggs");
		var shuttle = scn.GetLSCard("shuttle");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, shuttle);
		scn.BoardAsPilot(shuttle, biggs);

		assertFalse(scn.IsMatchingPilot(shuttle, biggs));

		scn.AttachCardsTo(biggs, suit);

		assertTrue(scn.IsMatchingPilot(shuttle, biggs));
	}

	@Test
	public void RebelFlightSuitDoesNotGrantMatchingStatusToBearerWhilePilotingNonCombatVehicle() {
		var scn = GetScenario();

		var suit = scn.GetLSCard("suit");
		var biggs = scn.GetLSCard("biggs");
		var landspeeder = scn.GetLSCard("landspeeder");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, landspeeder);
		scn.BoardAsPilot(landspeeder, biggs);

		assertFalse(scn.IsMatchingPilot(landspeeder, biggs));

		scn.AttachCardsTo(biggs, suit);

		assertFalse(scn.IsMatchingPilot(landspeeder, biggs));
	}

	@Test
	public void RebelFlightSuitDoesNotGrantMatchingStatusToBearerWhilePilotingStarship() {
		var scn = GetScenario();

		var suit = scn.GetLSCard("suit");
		var biggs = scn.GetLSCard("biggs");
		var corvette = scn.GetLSCard("corvette");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, corvette);
		scn.BoardAsPilot(corvette, biggs);

		assertFalse(scn.IsMatchingPilot(corvette, biggs));

		scn.AttachCardsTo(biggs, suit);

		assertFalse(scn.IsMatchingPilot(corvette, biggs));
	}

	@Test
	public void RebelFlightSuitGrantsPlus2ManeuverWhenFlyingMatchingVehicle() {
		var scn = GetScenario();

		var suit = scn.GetLSCard("suit");
		var biggs = scn.GetLSCard("biggs");
		var ywing = scn.GetLSCard("ywing");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, ywing);
		scn.BoardAsPilot(ywing, biggs);

		assertEquals(3, scn.GetManeuver(ywing));

		scn.AttachCardsTo(biggs, suit);

		assertEquals(5, scn.GetManeuver(ywing));
	}

	@Test
	public void RebelFlightSuitGrantsPlus2ManeuverEvenWhenNonuniquePilotAdds1() {
		var scn = GetScenario();

		var suit = scn.GetLSCard("suit");
		var captain = scn.GetLSCard("captain");
		var ywing = scn.GetLSCard("ywing");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, ywing);
		scn.BoardAsPilot(ywing, captain);

		assertFalse(scn.IsMatchingPilot(ywing, captain));
		assertEquals(4, scn.GetManeuver(ywing));

		scn.AttachCardsTo(captain, suit);

		assertTrue(scn.IsMatchingPilot(ywing, captain));
		assertEquals(5, scn.GetManeuver(ywing));
	}

	@Test
	public void RebelFlightSuitLimitedTo2BonusManeuverWhenFlyingMatchingVehicle() {
		var scn = GetScenario();

		var suit = scn.GetLSCard("suit");
		var zev = scn.GetLSCard("zev");
		var rogue2 = scn.GetLSCard("rogue2");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, rogue2);

		//Ships are maneuverless when unpiloted
		assertEquals(0, scn.GetManeuver(rogue2));

		scn.BoardAsPilot(rogue2, zev);
		//Zev adds 3 maneuver to the base 4 maneuver while piloting Rogue 2
		assertEquals(7, scn.GetManeuver(rogue2));

		scn.AttachCardsTo(zev, suit);
		//Flight Suit adds 2 maneuver...and caps the pilot's total maneuver bonus to 2, resulting
		// in a net loss
		assertEquals(6, scn.GetManeuver(rogue2));
	}

	@Test
	public void RebelFlightSuitGrantsAndCapsManeuverBonusAt2EachWithMultipleSuitPilots() {
		var scn = GetScenario();

		var suit = scn.GetLSCard("suit");
		var suit2 = scn.GetLSCard("suit2");
		var han = scn.GetLSCard("han");
		var chewie = scn.GetLSCard("chewie");
		var falcon = scn.GetLSCard("falcon");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, falcon);
		scn.BoardAsPilot(falcon, han, chewie);

		assertTrue(scn.IsMatchingPilot(falcon, han));
		assertTrue(scn.IsMatchingPilot(falcon, chewie));

		//4 base, +2 from Han's text, +1 from chewie's text
		assertEquals(7, scn.GetManeuver(falcon));

		scn.AttachCardsTo(han, suit);

		//4 base, +2 from han/suit capped, +1 from chewie's text
		assertEquals(7, scn.GetManeuver(falcon));

		scn.AttachCardsTo(chewie, suit2);

		//4 base, +2 from han/suit capped, +2 from chewie/suit capped
		assertEquals(8, scn.GetManeuver(falcon));
	}

	@Ignore("RFS cumulative violation; see the block comment in ModifiersLogic.foundCumulativeConflict()")
	@Test
	public void RebelFlightSuitManeuverBonusDoesNotStackCumulativelyFromMultiplePilotsWithSameTitle() {
		var scn = GetScenario();

		var suit = scn.GetLSCard("suit");
		var suit2 = scn.GetLSCard("suit2");
		var captain = scn.GetLSCard("captain");
		var captain2 = scn.GetLSCard("captain2");
		var falcon = scn.GetLSCard("falcon");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, falcon);
		scn.BoardAsPilot(falcon, captain, captain2);

		assertFalse(scn.IsMatchingPilot(falcon, captain));
		assertFalse(scn.IsMatchingPilot(falcon, captain2));

		//4 base, +1 from captain1's text, +0 from captain2's text due to cumulative
		assertEquals(5, scn.GetManeuver(falcon));

		scn.AttachCardsTo(captain, suit);

		assertTrue(scn.IsMatchingPilot(falcon, captain));
		//4 base, +2 from captain1/suit capped, +0 from captain2's text due to cumulative
		assertEquals(6, scn.GetManeuver(falcon));

		scn.AttachCardsTo(captain2, suit2);

		assertTrue(scn.IsMatchingPilot(falcon, captain2));
		//4 base, +2 from captain/suit capped, +0 from captain2/suit capped due to cumulative
		assertEquals(6, scn.GetManeuver(falcon));
	}

	@Test
	public void RebelFlightSuitGrantsBattleDestinyWhenFlyingMatchingVehicle() {
		var scn = GetScenario();

		var suit = scn.GetLSCard("suit");
		var biggs = scn.GetLSCard("biggs");
		var ywing = scn.GetLSCard("ywing");

		var site = scn.GetLSStartingLocation();

		var tie = scn.GetDSCard("tie");

		scn.StartGame();

		scn.MoveCardsToLocation(site, ywing, tie);
		scn.BoardAsPilot(ywing, biggs);
		scn.AttachCardsTo(biggs, suit);

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);
		scn.SkipToPowerSegment();

		//DS destiny
		scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");

		//Not eligible for battle destiny normally
		assertEquals(2, scn.GetAbility(biggs));
		assertTrue(scn.LSDecisionAvailable("Do you want to draw 1 battle destiny?"));
	}

	@Test
	public void RebelFlightSuitDoesNotGrantBattleDestinyWhenBearerNativelyMatchesOtherTypeOfShip() {
		var scn = GetScenario();

		var suit = scn.GetLSCard("suit");
		var ywing = scn.GetLSCard("ywing");

		var site = scn.GetLSStartingLocation();

		var lennox = scn.GetDSCard("lennox");
		var tyrant = scn.GetDSCard("tyrant");

		scn.StartGame();

		scn.MoveCardsToLocation(site, tyrant, ywing);
		//Doing something a little different and cheating the flight suit onto an imperial
		// since almost all Light Side matching pilots either provide battle destiny or
		// are on a starfighter, combat vehicle, or shuttle, and we need to ensure that
		// no bonuses are applied from the flight suit if the matching pair are *not*
		// on one of those three things.
		scn.BoardAsPilot(tyrant, lennox);
		scn.AttachCardsTo(lennox, suit);

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);
		scn.SkipToPowerSegment();

		//Not eligible for battle destiny normally
		assertEquals(3, scn.GetDSAbilityAtLocation(site));
		//Flight Suit does not add a battle destiny, because the match is from the
		// Lennox/Tyrant relationship and not Flight Suit's starfighter/combat vehicle/shuttle
		assertFalse(scn.DSDecisionAvailable("Do you want to draw 1 battle destiny?"));
		scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
	}

	@Test
	public void RebelFlightSuitDoesNotGrantsBattleDestinyWhenOtherSourceOfBattleDestinyUsed() {
		var scn = GetScenario();

		var suit = scn.GetLSCard("suit");
		var luke = scn.GetLSCard("luke");
		var ywing = scn.GetLSCard("ywing");

		var site = scn.GetLSStartingLocation();

		var tie = scn.GetDSCard("tie");

		scn.StartGame();

		scn.MoveCardsToLocation(site, ywing, tie);
		scn.BoardAsPilot(ywing, luke);
		scn.AttachCardsTo(luke, suit);

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);
		scn.SkipToPowerSegment();

		//DS destiny
		scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");

		//Eligible for battle destiny normally
		assertEquals(4, scn.GetAbility(luke));
		//Only 1 battle destiny and not 2
		assertTrue(scn.LSDecisionAvailable("Do you want to draw 1 battle destiny?"));
	}

	@Test
	public void RebelFlightSuitGrantsImmunityToAttritionBelow4OnRed10() {
		var scn = GetScenario();

		var suit = scn.GetLSCard("suit");
		var biggs = scn.GetLSCard("biggs");
		var red10 = scn.GetLSCard("red10");

		var site = scn.GetLSStartingLocation();

		var tie = scn.GetDSCard("tie");
		var vader = scn.GetDSCard("vader");

		scn.StartGame();

		scn.MoveCardsToLocation(site, red10, tie);
		scn.BoardAsPilot(red10, biggs);
		scn.AttachCardsTo(biggs, suit);
		scn.BoardAsPilot(tie, vader);

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);
		scn.PrepareDSDestiny(3);
		scn.PrepareLSDestiny(7);
		scn.SkipToDamageSegment(true);

		assertEquals(5, scn.GetUnpaidDSBattleDamage());
		scn.DSChooseCard(vader);
		scn.PassResponses();

		assertEquals(3, scn.GetUnpaidLSAttrition());
		assertEquals(0, scn.GetUnpaidLSBattleDamage());
		assertTrue(scn.LSDecisionAvailable("Choose a card from battle to forfeit (if desired)"));
		scn.LSPass();
	}
}
