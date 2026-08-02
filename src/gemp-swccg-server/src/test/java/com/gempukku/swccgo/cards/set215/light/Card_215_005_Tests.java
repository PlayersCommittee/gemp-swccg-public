package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_215_005_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("cell", "215_005"); //Cell 2187 (V)
					put("blaster", "1_152");
					put("boushh","110_001");
				}},
				new HashMap<>()
				{{
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
	public void Cell2187VStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Cell 2187 (V)
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Effect
		 * Subtype: Normal
		 * Destiny: 5
		 * Icons: A New Hope, Set 15
		 * Game Text: If [A New Hope] Leia imprisoned, deploy on table. [Set 8] Luke is a spy and stormtrooper.
		 * 		Chewie, Leia, and stormtroopers are immune to Nevar Yalnal, Physical Choke, and
		 * 		Put All Sections On Alert. Once per turn, may [download] a blaster or rifle. Immune to
		 * 		This Is Some Rescue! [Immune to Alter.]
		 * Lore: Aren't you a little short for a stormtrooper?'
		 * Set: Set 15
		 * Rarity: V
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("cell").getBlueprint();

		assertEquals("Cell 2187", card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertTrue(card.hasVirtualSuffix());
		assertEquals(Side.LIGHT, card.getSide());
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.EFFECT);
		}});
		assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
		assertEquals(5, card.getDestiny(), scn.epsilon);
		scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.EFFECT);
			add(Icon.A_NEW_HOPE);
			add(Icon.VIRTUAL_SET_15);
		}});
		assertEquals(ExpansionSet.SET_15,card.getExpansionSet());
		assertEquals(Rarity.V, card.getRarity());
	}

	@Test
	public void Cell2187VCanDownloadBlaster() {
		var scn = GetScenario();

		var cell = scn.GetLSCard("cell");
		var blaster = scn.GetLSCard("blaster");
		var trooper1 = scn.GetLSFiller(1);
		var trooper2 = scn.GetLSFiller(2);
		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, trooper1, trooper2);
		scn.MoveCardsToLSHand(blaster);

		scn.MoveCardsToLSSideOfTable(cell);

		scn.SkipToLSTurn(Phase.CONTROL);
		scn.MoveCardsToTopOfLSReserveDeck(blaster);

		scn.SkipToPhase(Phase.DEPLOY);
		assertEquals(0,scn.GetLSUsedPileCount());
		scn.LSUseCardAction(cell, "Deploy a blaster");

		scn.LSChooseCard(blaster);
		scn.PassAllResponses();

		assertTrue(scn.LSHasCardChoiceAvailable(trooper1));
		assertTrue(scn.LSHasCardChoiceAvailable(trooper2));
		scn.LSChooseCard(trooper1);
		scn.PassAllResponses();

		assertTrue(scn.IsAttachedTo(trooper1,blaster));
		assertEquals(1,scn.GetLSUsedPileCount());
	}

	@Test
	public void Cell2187VCanDownloadBlasterOnUndercoverSpy() {
		var scn = GetScenario();

		var cell = scn.GetLSCard("cell");
		var blaster = scn.GetLSCard("blaster");
		var boushh = scn.GetLSCard("boushh");
		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLSHand(blaster, boushh);

		scn.MoveCardsToLSSideOfTable(cell);

		scn.SkipToLSTurn(Phase.CONTROL);

		scn.LSActivateForceCheat(5);

		scn.SkipToPhase(Phase.DEPLOY);
		scn.LSDeployCard(boushh);
		scn.LSChooseCard(site);
		scn.PassAllResponses();

		scn.MoveCardsToTopOfLSReserveDeck(blaster);

		scn.DSPass();

		assertTrue(boushh.isUndercover());
		scn.LSUseCardAction(cell, "Deploy a blaster");

		scn.LSChooseCard(blaster);
		scn.PassAllResponses();

		assertTrue(scn.IsAttachedTo(boushh,blaster));
	}

}
