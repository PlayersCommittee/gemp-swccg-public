package com.gempukku.swccgo.cards.set8.light;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_8_044_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("careful","8_044"); //Careful Planning
                    put("ls_lars", "1_132"); //Tatooine: Lars' Moisture Farm
                    put("dune", "1_130"); //Tatooine: Dune Sea
				}},
				new HashMap<>()
				{{
				}},
				10,
				10,
				StartingSetup.LSStartingLocation("1_133"), //Tatooine: Mos Eisley
				StartingSetup.DSStartingLocation("1_294"), //Tatooine: Lars' Moisture Farm
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void CarefulPlanningStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Careful Planning
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Interrupt
		 * Subtype: Used or Starting
		 * Destiny: 5
		 * Icons: Endor
		 * Game Text: USED: If a battle was just initiated, draw destiny and activate up to that much Force.
		 * 		STARTING: If you have not deployed an Objective, deploy from Reserve Deck one battleground site
		 * 		(or two <> sites) related to your starting location. Place Interrupt in Reserve Deck.
		 * Lore: Alliance troops on planet must plan ahead to achieve success in military operations.
		 * Set: Endor
		 * Rarity: C
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("careful").getBlueprint();

		assertEquals("Careful Planning", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.INTERRUPT);
		}});
        assertEquals(CardSubtype.USED_OR_STARTING, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.ENDOR);
			add(Icon.INTERRUPT);
		}});
		assertEquals(ExpansionSet.ENDOR,card.getExpansionSet());
		assertEquals(Rarity.C,card.getRarity());
	}

	@Test
	public void CarefulPlanningLarsDrainWithoutCarefulPlanning() {
        //not playing Careful Planning allows force drain of 2 at (Dark) Lars' Moisture Farm
		var scn = GetScenario();

        var ds_lars = scn.GetDSStartingLocation();
		var trooper = scn.GetDSFiller(1);

        scn.StartGame();

        scn.LSChoose("Left"); //place Mos Eisley left of (Dark) Lars' site

		scn.LSPass(); //skip playing a starting interrupt

		assertTrue(scn.AwaitingDSActivatePhaseActions());
		scn.MoveCardsToLocation(ds_lars, trooper);

		scn.SkipToPhase(Phase.CONTROL);
		assertEquals(0, scn.GetLSLostPileCount());
		scn.DSUseCardAction(ds_lars, "drain");
		scn.PassAllResponses();

		scn.LSPayRemainingForceLossFromReserveDeck();
		scn.PassAllResponses();

		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertEquals(2, scn.GetLSLostPileCount());
    }

	@Test
	public void CarefulPlanningLarsDrainWithCarefulPlanning() {
		//playing Careful Planning allows force drain of 2 at (Dark) Lars' Moisture Farm
		//shows https://github.com/PlayersCommittee/gemp-swccg-public/issues/963 fixed
		var scn = GetScenario();

		var careful = scn.GetLSCard("careful");
		var dune = scn.GetLSCard("dune");

		var ds_lars = scn.GetDSStartingLocation();
		var trooper = scn.GetDSFiller(1);

		scn.StartGame();

		scn.LSChoose("Left"); //place Mos Eisley left of (Dark) Lars' site

		scn.LSChooseCard(careful);
		scn.LSChooseCard(dune);
		scn.LSChoose("Left"); //place Dune Sea left of Lars' site

		assertTrue(scn.AwaitingDSActivatePhaseActions());
		scn.MoveCardsToLocation(ds_lars, trooper);

		scn.SkipToPhase(Phase.CONTROL);
		assertEquals(0, scn.GetLSLostPileCount());
		scn.DSUseCardAction(ds_lars, "drain");
		scn.PassAllResponses();

		scn.LSPayRemainingForceLossFromReserveDeck();
		scn.PassAllResponses();

		assertTrue(scn.AwaitingLSControlPhaseActions());
		assertEquals(2, scn.GetLSLostPileCount());
	}
}
