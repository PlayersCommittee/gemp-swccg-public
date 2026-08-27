package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_2_056_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("sabotage", "2_56");
					put("spy", "7_5"); // Bothan Spy
					put("merc", "2_36"); // Merc Sunlet (thief skill)
				}},
				new HashMap<>()
				{{
					put("trooper", "1_194"); // Stormtrooper
					put("blaster", "1_317"); // Imperial Blaster (Use 1)
					put("vader", "1_168");
					put("saber", "1_314"); // Dark Jedi Lightsaber
					put("saber2", "1_314");
					put("liftTube", "1_308"); // vehicle, deploy 1
					put("droid", "1_163"); // 5D6-RA-7
					put("bolt", "1_205"); // Restraining Bolt (no Use X)
					put("pondaBlaster", "7_323"); // free on smuggler / 2 on warrior
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

	private void SetupSpyAndDarkTargetAtSite(VirtualTableScenario scn, PhysicalCardImpl... extraAtSite) {
		var site = scn.GetLSCard("starting-location");
		var spy = scn.GetLSCard("spy");
		var trooper = scn.GetDSCard("trooper");
		scn.MoveCardsToLocation(site, spy, trooper);
		scn.MakeCardGoUndercover(spy);
		if (extraAtSite.length > 0) {
			scn.MoveCardsToLocation(site, extraAtSite);
		}
	}

	@Test
	public void SabotageStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Sabotage
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Interrupt
		 * Subtype: Used
		 * Destiny: 5
		 * Icons: A New Hope
		 * Set: A New Hope
		 * Rarity: U1
		 */
		var scn = GetScenario();
		var card = scn.GetLSCard("sabotage").getBlueprint();

		assertEquals("Sabotage", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertEquals(5, card.getDestiny(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.INTERRUPT);
		}});
		assertEquals(CardSubtype.USED, card.getCardSubtype());
		assertEquals(1, card.getIconCount(Icon.A_NEW_HOPE));
		assertEquals(ExpansionSet.A_NEW_HOPE, card.getExpansionSet());
		assertEquals(Rarity.U1, card.getRarity());
	}

	@Test
	public void PlayableInLsControlWithUndercoverSpyAtSiteOtherwiseNot() {
		var scn = GetScenario();
		var sabotage = scn.GetLSCard("sabotage");
		var spy = scn.GetLSCard("spy");
		var site = scn.GetLSCard("starting-location");
		var trooper = scn.GetDSCard("trooper");
		var blaster = scn.GetDSCard("blaster");

		scn.MoveCardsToLSHand(sabotage);
		scn.StartGame();

		scn.MoveCardsToLocation(site, spy, trooper);
		scn.AttachCardsTo(trooper, blaster);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertFalse(scn.LSCardPlayAvailable(sabotage));

		scn.MakeCardGoUndercover(spy);
		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(scn.LSCardPlayAvailable(sabotage));

		scn.SkipToPhase(Phase.DEPLOY);
		assertFalse(scn.LSCardPlayAvailable(sabotage));
	}

	@Test
	public void DestinyGreaterThanDeployCostLosesDarkBlasterDestinyLessOrEqualDoesNot() {
		var scn = GetScenario();
		var sabotage = scn.GetLSCard("sabotage");
		var blaster = scn.GetDSCard("blaster");
		var trooper = scn.GetDSCard("trooper");

		scn.MoveCardsToLSHand(sabotage);
		scn.StartGame();
		SetupSpyAndDarkTargetAtSite(scn);
		scn.AttachCardsTo(trooper, blaster);

		float cost = Card2_056.getOnTableDeployCost(scn.game(), sabotage, blaster);
		assertEquals(1f, cost, scn.epsilon);

		scn.SkipToLSTurn(Phase.CONTROL);
		scn.PrepareLSDestiny(1); // 1 is not > 1
		assertTrue(scn.LSCardPlayAvailable(sabotage));
		scn.LSPlayCard(sabotage);
		scn.LSChooseCard(blaster);
		scn.PassAllResponses();
		scn.PassCardLeavingTable();
		assertEquals(Zone.ATTACHED, blaster.getZone());

		// Same copy is unique; retrieve and replay vs destiny 2 > 1
		scn.MoveCardsToLSHand(sabotage);
		scn.SkipToLSTurn(Phase.CONTROL);
		scn.PrepareLSDestiny(2);
		scn.LSPlayCard(sabotage);
		scn.LSChooseCard(blaster);
		scn.PassAllResponses();
		scn.PassCardLeavingTable();
		assertEquals(Zone.TOP_OF_LOST_PILE, blaster.getZone());
	}

	@Test
	public void ThiefUndercoverSpyMayStealDarkWeaponInsteadOfLose() {
		var scn = GetScenario();
		var sabotage = scn.GetLSCard("sabotage");
		var spy = scn.GetLSCard("spy");
		var merc = scn.GetLSCard("merc");
		var blaster = scn.GetDSCard("blaster");
		var trooper = scn.GetDSCard("trooper");
		var site = scn.GetLSCard("starting-location");

		scn.MoveCardsToLSHand(sabotage, merc);
		scn.StartGame();
		scn.MoveCardsToLocation(site, spy, trooper);
		scn.AttachCardsTo(trooper, blaster);

		// Deploy Merc Sunlet so play-option 1 actually grants Keyword.THIEF
		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSPlayCard(merc, "non-thief");
		scn.LSChooseCard(spy);
		scn.PassAllResponses();
		scn.MakeCardGoUndercover(spy);

		scn.SkipToLSTurn(Phase.CONTROL);
		scn.PrepareLSDestiny(2);
		assertTrue(scn.LSCardPlayAvailable(sabotage));
		scn.LSPlayCard(sabotage);
		if (scn.LSAnyDecisionsAvailable() && scn.LSGetDecision().getText().toLowerCase().contains("weapon")) {
			scn.LSChooseCard(blaster);
		}
		scn.PassAllResponses();

		assertTrue(scn.LSAnyDecisionsAvailable());
		assertTrue(scn.LSGetDecision().getText().toLowerCase().contains("steal"));
		scn.LSChooseYes();
		scn.PassAllResponses();

		assertEquals(Zone.ATTACHED, blaster.getZone());
		assertEquals(spy, blaster.getAttachedTo());
		assertEquals(scn.LS, blaster.getOwner());
		assertFalse(blaster.getZone() == Zone.TOP_OF_LOST_PILE);
	}

	@Test
	public void CancelInformantIsWiredButInformantIsUnimplemented() {
		// Informant (ANH Dark Used Interrupt) is not in GEMP yet, so the on-table cancel
		// action cannot appear. The being-played cancel is Houjix-style Filters.title("Informant").
		var scn = GetScenario();
		var sabotage = scn.GetLSCard("sabotage");
		var blaster = scn.GetDSCard("blaster");
		var trooper = scn.GetDSCard("trooper");

		scn.MoveCardsToLSHand(sabotage);
		scn.StartGame();
		SetupSpyAndDarkTargetAtSite(scn);
		scn.AttachCardsTo(trooper, blaster);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(scn.LSCardPlayAvailable(sabotage));
		assertFalse(scn.LSCardPlayAvailable(sabotage, "Cancel Informant"));
		assertTrue(scn.LSCardPlayAvailable(sabotage, "Target weapon"));
	}

	@Test
	public void VehicleIsLegalAndDarkJediLightsaberOnTableUsesBearerAbilityForX() {
		var scn = GetScenario();
		var sabotage = scn.GetLSCard("sabotage");
		var liftTube = scn.GetDSCard("liftTube");
		var vader = scn.GetDSCard("vader");
		var saber = scn.GetDSCard("saber");
		var saber2 = scn.GetDSCard("saber2");
		var trooper = scn.GetDSCard("trooper");

		scn.MoveCardsToLSHand(sabotage);
		scn.StartGame();
		SetupSpyAndDarkTargetAtSite(scn, liftTube, vader);
		scn.AttachCardsTo(vader, saber);
		scn.AttachCardsTo(trooper, saber2);

		float liftCost = Card2_056.getOnTableDeployCost(scn.game(), sabotage, liftTube);
		float djlOnVader = Card2_056.getOnTableDeployCost(scn.game(), sabotage, saber);
		float djlOnTrooper = Card2_056.getOnTableDeployCost(scn.game(), sabotage, saber2);
		assertEquals(1f, liftCost, scn.epsilon);
		// X = 7 - ability. Vader ability 6 => 1. Stormtrooper ability 1 => 6. Fake constant 7 means attachedTo was omitted.
		assertEquals(1f, djlOnVader, scn.epsilon);
		assertEquals(6f, djlOnTrooper, scn.epsilon);

		scn.SkipToLSTurn(Phase.CONTROL);
		scn.PrepareLSDestiny(2); // 2 > Vader saber 1; would not beat stormtrooper saber 6
		assertTrue(scn.LSCardPlayAvailable(sabotage));
		scn.LSPlayCard(sabotage);
		assertTrue(scn.LSHasCardChoiceAvailable(liftTube));
		assertTrue(scn.LSHasCardChoiceAvailable(saber));
		scn.LSChooseCard(saber);
		scn.PassAllResponses();
		scn.PassCardLeavingTable();
		assertEquals(Zone.TOP_OF_LOST_PILE, saber.getZone());
		assertEquals(Zone.ATTACHED, saber2.getZone());
	}
}
