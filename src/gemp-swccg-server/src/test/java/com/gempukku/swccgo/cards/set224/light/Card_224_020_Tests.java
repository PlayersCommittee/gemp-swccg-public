package com.gempukku.swccgo.cards.set224.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_224_020_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("sos", "224_020");

					put("lightsaber", "3_071"); //Anakin's Lightsaber

					put("ywing", "1_147");  //Starfighter with permanent pilot
					put("naboo", "12_78"); //system

					put("snowspeeder", "3_69"); //vehicle with permanent pilot
				}},
				new HashMap<>()
				{{
					put("blaster", "1_317"); //Imperial Blaster
					put("vader", "1_168"); //Dark Jedi
				}},
				10,
				10,
				StartingSetup.DefaultLSGroundLocation,
				StartingSetup.DefaultDSGroundLocation,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void SonOfSkywalkerVStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Son Of Skywalker (V)
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Character
		 * Subtype: Rebel
		 * Destiny: 1
		 * Deploy: 5
		 * Power: 5
		 * Ability: 5
		 * Forfeit: 8
		 * Icons: Pilot, Warrior, Cloud City, Dagobah, Virtual Set 24
		 * Persona: Luke
		 * Game Text: [Pilot] 2. During battle, characters 'hit' by Luke may not fire weapons. May [download] Anakin's
		 * 		Lightsaber here. If a battle just initiated at same site, unless a vehicle here, may target a Dark Jedi
		 * 		here; for remainder of battle, exclude all other characters. Immune to attrition < 4.
		 * Lore: Luke Skywalker. Son of Anakin. Seeker of Yoda. Levitator of rocks. Ignorer of advice. Incapable of
		 * 		impossible. Reckless is he.
		 * Set: Set 24
		 * Rarity: V
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("sos").getBlueprint();

		assertEquals("Son Of Skywalker", card.getTitle());
		assertTrue(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertTrue(card.isCardType(CardType.REBEL));
		assertTrue(card.hasPersona(Persona.LUKE));
		assertEquals(1, card.getDestiny(), scn.epsilon);
		assertEquals(5, card.getDeployCost(), scn.epsilon);
		assertEquals(5, card.getPower(), scn.epsilon);
		assertEquals(5, card.getAbility(), scn.epsilon);
		assertEquals(8, card.getForfeit(), scn.epsilon);
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.REBEL);
			add(Icon.PILOT);
			add(Icon.WARRIOR);
			add(Icon.DAGOBAH);
			add(Icon.CLOUD_CITY);
			add(Icon.VIRTUAL_SET_24);
		}});
	}

	@Test
	public void SonofSkywalkerVAdds2ToPowerOfStarfighterHePilots() {
		var scn = GetScenario();

		var sos = scn.GetLSCard("sos");
		var ywing = scn.GetLSCard("ywing");

		var naboo = scn.GetLSCard("naboo");

		scn.StartGame();

		scn.MoveLocationToTable(naboo);
		scn.MoveCardsToLocation(naboo, ywing);

		assertEquals(2, scn.GetPower(ywing), scn.epsilon);

		scn.BoardAsPilot(ywing, sos);

		//Base 2, +2 from SOS's text
		assertEquals(4, scn.GetPower(ywing), scn.epsilon);
	}

	@Test
	public void SonofSkywalkerVAdds2ToPowerOfVehicleHePilots() {
		var scn = GetScenario();

		var sos = scn.GetLSCard("sos");
		var snowspeeder = scn.GetLSCard("snowspeeder");

		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, snowspeeder);

		assertEquals(3, scn.GetPower(snowspeeder), scn.epsilon);

		scn.BoardAsPilot(snowspeeder, sos);

		//Base 3, +2 from SOS's text
		assertEquals(5, scn.GetPower(snowspeeder), scn.epsilon);
	}

	@Test
	public void SonofSkywalkerVHittingCharacterPreventsThemUsingWeapon() {
		var scn = GetScenario();

		var sos = scn.GetLSCard("sos");
		var lightsaber = scn.GetLSCard("lightsaber");

		var stormtrooper = scn.GetDSFiller(1);
		var blaster = scn.GetDSCard("blaster");

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, stormtrooper, sos);
		scn.AttachCardsTo(sos, lightsaber);
		scn.AttachCardsTo(stormtrooper, blaster);

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);
		scn.PassBattleStartResponses();

		assertTrue(scn.DSCardActionAvailable(blaster));

		scn.DSPass();

		scn.PrepareLSDestiny(7);
		scn.PrepareLSDestiny(6);
		assertTrue(scn.LSCardActionAvailable(lightsaber));
		scn.LSUseCardAction(lightsaber);
		scn.LSChooseCard(stormtrooper);
		scn.PassAllResponses();

		assertTrue(stormtrooper.isHit());

		assertFalse(scn.DSCardActionAvailable(blaster));
		assertTrue(scn.AwaitingDSWeaponsSegmentActions());

		//TODO: add vader
	}

	@Test
	public void SonofSkywalkerVMayDeployAnakinsLightsaberFromReserve() {
		var scn = GetScenario();

		var sos = scn.GetLSCard("sos");
		var lightsaber = scn.GetLSCard("lightsaber");
		scn.MoveCardsToLSHand(sos);

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, sos);

		scn.SkipToLSTurn(Phase.CONTROL);
		scn.MoveCardsToTopOfOwnReserveDeck(lightsaber);
		scn.SkipToPhase(Phase.DEPLOY);

		assertInZone(Zone.RESERVE_DECK, lightsaber);
		assertAtLocation(site, sos);

		assertTrue(scn.LSCardActionAvailable(sos));

		scn.LSUseCardAction(sos);
		scn.LSChooseCard(lightsaber);
		scn.PassAllResponses();

		assertTrue(scn.IsAttachedTo(sos, lightsaber));
		assertTrue(scn.DSDecisionAvailable("deploy"));
	}

	@Test
	public void SonofSkywalkerVCannotUseDeployActionIfAnakinsLightsaberOnTable() {
		var scn = GetScenario();

		var sos = scn.GetLSCard("sos");
		var lightsaber = scn.GetLSCard("lightsaber");
		scn.MoveCardsToLSHand(sos);

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, sos);
		scn.AttachCardsTo(sos, lightsaber);

		scn.SkipToLSTurn(Phase.DEPLOY);

		assertTrue(scn.IsAttachedTo(sos, lightsaber));

		assertFalse(scn.LSCardActionAvailable(sos));
	}

	@Test
	public void SonofSkywalkerVBattleAbilityExcludesEveryoneFromBattleExceptSelfAndDarkJedi() {
		var scn = GetScenario();

		var rebel = scn.GetLSFiller(1);
		var sos = scn.GetLSCard("sos");

		var site = scn.GetLSStartingLocation();

		var stormtrooper = scn.GetDSFiller(1);
		var vader = scn.GetDSCard("vader");

		scn.StartGame();

		scn.MoveCardsToLocation(site, rebel, sos, stormtrooper, vader);

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);

		assertTrue(scn.IsParticipatingInBattle(rebel, sos, stormtrooper, vader));

		assertTrue(scn.LSCardActionAvailable(sos));
		scn.LSUseCardAction(sos);
		scn.LSChooseCard(vader);
		scn.PassAllResponses();

		assertFalse(scn.IsParticipatingInBattle(stormtrooper));
		assertFalse(scn.IsParticipatingInBattle(rebel));
		assertTrue(scn.IsParticipatingInBattle(sos, vader));
	}

	@Test
	public void SonofSkywalkerVBattleAbilityNotAvailableIfBattleTriggeredElsewhere() {
		var scn = GetScenario();

		var rebel = scn.GetLSFiller(1);
		var sos = scn.GetLSCard("sos");

		var site = scn.GetLSStartingLocation();
		var dssite = scn.GetDSStartingLocation();

		var stormtrooper = scn.GetDSFiller(1);
		var vader = scn.GetDSCard("vader");

		scn.StartGame();

		scn.MoveCardsToLocation(site, rebel, stormtrooper, vader);
		scn.MoveCardsToLocation(dssite, sos);

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);

		assertTrue(scn.AwaitingDSWeaponsSegmentActions());
		// assertFalse(scn.LSCardActionAvailable(sos));
	}

	@Test
	public void SonofSkywalkerVCannotUseBattleAbilityIfVehicleThere() {
		var scn = GetScenario();

		var rebel = scn.GetLSFiller(1);
		var sos = scn.GetLSCard("sos");
		var snowspeeder = scn.GetLSCard("snowspeeder");

		var site = scn.GetLSStartingLocation();

		var stormtrooper = scn.GetDSFiller(1);
		var vader = scn.GetDSCard("vader");

		scn.StartGame();

		scn.MoveCardsToLocation(site, rebel, sos, stormtrooper, vader, snowspeeder);

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);

		assertTrue(scn.IsParticipatingInBattle(rebel, sos, stormtrooper, vader, snowspeeder));

		assertTrue(scn.AwaitingDSWeaponsSegmentActions());
		// assertFalse(scn.LSCardActionAvailable(sos));
	}

	@Test
	public void SonofSkywalkerVImmuneToAttritionLessThan4() {
		var scn = GetScenario();

		var sos = scn.GetLSCard("sos");

		var site = scn.GetLSStartingLocation();

		var vader = scn.GetDSCard("vader");

		scn.StartGame();

		scn.MoveCardsToLocation(site, sos, vader);

		scn.SkipToLSTurn(Phase.BATTLE);
		scn.LSInitiateBattle(site);

		scn.PrepareDSDestiny(1);
		scn.PrepareLSDestiny(0);
		scn.SkipToEndOfPowerSegment(true);

		scn.PassAllResponses();

		assertTrue(scn.AwaitingLSAttritionPayment());
		//Drew 1 destiny, +1 from vader
		assertEquals(2, scn.GetUnpaidLSAttrition());

		scn.LSPayRemainingBattleDamageFromReserveDeck();
		assertTrue(scn.LSDecisionAvailable("Choose a card from battle to forfeit (if desired)"));
		scn.LSPass();

		assertFalse(scn.IsActiveBattle());
		assertAtLocation(site, sos);
	}

	@Test
	public void SonofSkywalkerVNotImmuneToAttritionOf4() {
		var scn = GetScenario();

		var sos = scn.GetLSCard("sos");

		var site = scn.GetLSStartingLocation();

		var vader = scn.GetDSCard("vader");

		scn.StartGame();

		scn.MoveCardsToLocation(site, sos, vader);

		scn.SkipToLSTurn(Phase.BATTLE);
		scn.LSInitiateBattle(site);

		scn.PrepareDSDestiny(3);
		scn.PrepareLSDestiny(0);
		scn.SkipToEndOfPowerSegment(true);

		scn.PassAllResponses();

		assertTrue(scn.AwaitingLSAttritionPayment());
		//Drew 3 destiny, +1 from vader
		assertEquals(4, scn.GetUnpaidLSAttrition());

		scn.LSPayRemainingBattleDamageFromReserveDeck();
		assertFalse(scn.LSDecisionAvailable("Choose a card from battle to forfeit (if desired)"));
		assertTrue(scn.LSDecisionAvailable("Choose a card from battle to forfeit"));
	}

}
