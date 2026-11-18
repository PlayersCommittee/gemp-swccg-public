package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Regression for #201: Program Trap exploding mid-power-segment must stop remaining battle destiny draws.
 */
public class Card_2_124_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("luke", "1_19");
				}},
				new HashMap<>()
				{{
					put("trap", "2_124");
					put("septoid", "2_109");
					put("vader", "1_168");
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
	public void ProgramTrapStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Program Trap
		 * Uniqueness: UNIQUE
		 * Side: Dark
		 * Type: Effect
		 * Destiny: 4
		 * Game Text: Use 2 Force to deploy on an opponent's droid (except R2-D2 and C-3PO), 1 on your droid. When either
		 * 		player draws a destiny matching the number of characters at same site, droid 'explodes' (all characters
		 * 		present are lost).
		 * Set: A New Hope
		 * Rarity: U1
		 */
		var scn = GetScenario();
		var card = scn.GetDSCard("trap").getBlueprint();
		assertEquals("Program Trap", card.getTitle());
		assertEquals(4, card.getDestiny(), scn.epsilon);
	}

	@Test
	public void ProgramTrapExplodingMidPowerSegmentStopsFurtherBattleDestinyDraws() {
		var scn = GetScenario();

		var luke = scn.GetLSCard("luke");
		var site = scn.GetLSStartingLocation();

		var trap = scn.GetDSCard("trap");
		var septoid = scn.GetDSCard("septoid");
		var vader = scn.GetDSCard("vader");

		scn.StartGame();

		// 3 characters present: Vader, Septoid, Luke
		scn.MoveCardsToLocation(site, luke, vader, septoid);
		scn.AttachCardsTo(septoid, trap);

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSInitiateBattle(site);
		scn.SkipToPowerSegment();

		assertTrue(scn.IsActiveBattle());
		assertTrue(scn.IsReachedPowerSegment());

		// Printed destiny 2 + Vader's +1 battle destiny = 3, matching character count → Program Trap explodes
		scn.PrepareDSDestiny(2);
		assertTrue(scn.DSDecisionAvailable("battle destiny?"));
		scn.DSChooseYes();

		scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
		scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
		scn.PassResponses("EXPLODING_PROGRAM_TRAP");

		resolveProgramTrapCharacterLosses(scn, luke, vader, septoid, trap);

		assertInZone(Zone.LOST_PILE, luke, vader, septoid, trap);

		assertFalse("LS must not draw battle destiny after Program Trap cleared the site",
				scn.LSDecisionAvailable("battle destiny?"));
		assertFalse("Battle must not continue after both sides lost presence mid-power-segment",
				scn.IsActiveBattle());
	}

	private void resolveProgramTrapCharacterLosses(VirtualTableScenario scn, PhysicalCardImpl... cards) {
		for (int safety = 0; safety < 25; safety++) {
			if (allInLostPile(cards)) {
				return;
			}

			if (scn.DSDecisionAvailable("Choose card to be lost")) {
				scn.DSChooseCard(firstChoosable(scn, true, cards));
				scn.PassAllResponses();
				continue;
			}
			if (scn.LSDecisionAvailable("Choose card to be lost")) {
				scn.LSChooseCard(firstChoosable(scn, false, cards));
				scn.PassAllResponses();
				continue;
			}
			if (scn.DSDecisionAvailable("Choose card to put on Lost Pile")) {
				scn.DSChooseCard(firstChoosable(scn, true, cards));
				scn.PassAllResponses();
				continue;
			}
			if (scn.LSDecisionAvailable("Choose card to put on Lost Pile")) {
				scn.LSChooseCard(firstChoosable(scn, false, cards));
				scn.PassAllResponses();
				continue;
			}
			if (scn.GetCurrentDecision() != null
					&& scn.GetCurrentDecision().getText().toLowerCase().contains("optional")) {
				scn.PassResponses("optional");
				continue;
			}
			return;
		}
	}

	private boolean allInLostPile(PhysicalCardImpl... cards) {
		for (PhysicalCardImpl card : cards) {
			if (card.getZone() != Zone.LOST_PILE && card.getZone() != Zone.TOP_OF_LOST_PILE) {
				return false;
			}
		}
		return true;
	}

	private PhysicalCardImpl firstChoosable(VirtualTableScenario scn, boolean dark, PhysicalCardImpl... cards) {
		for (PhysicalCardImpl card : cards) {
			boolean available = dark ? scn.DSHasCardChoiceAvailable(card) : scn.LSHasCardChoiceAvailable(card);
			if (available) {
				return card;
			}
		}
		throw new AssertionError("No choosable card among Program Trap losers");
	}
}
