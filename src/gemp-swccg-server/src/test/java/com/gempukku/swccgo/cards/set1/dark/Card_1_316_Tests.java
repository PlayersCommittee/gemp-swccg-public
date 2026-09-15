package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Han Seeker (1_316) ? bug #54: character lost by Seeker must take attached weapon to Lost Pile.
 */
public class Card_1_316_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>() {{
					put("beru", "1_2");
					put("weapon", "8_86");
				}},
				new HashMap<>() {{
					put("seeker", "1_316");
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
	public void HanSeekerStatsAndKeywordsAreCorrect() {
		var scn = GetScenario();
		var card = scn.GetDSCard("seeker").getBlueprint();
		assertEquals("Han Seeker", card.getTitle());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		assertTrue(card.isCardType(CardType.WEAPON));
		assertEquals(3, card.getDestiny(), scn.epsilon);
	}

	@Test
	public void HanSeekerLosesCharacterWithAttachedWeaponBothGoLost() throws DecisionResultInvalidException {
		var scn = GetScenario();

		var beru = scn.GetLSCard("beru");
		var weapon = scn.GetLSCard("weapon");
		var seeker = scn.GetDSCard("seeker");
		var site = scn.GetLSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, seeker, beru);
		scn.AttachCardsTo(beru, weapon);

		assertEquals(Zone.ATTACHED, weapon.getZone());
		assertEquals(beru, weapon.getAttachedTo());
		assertTrue(beru.getCardsAttached().contains(weapon));

		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.AwaitingDSControlPhaseActions());

		int lsLostBefore = scn.GetLSLostPileCount();
		int dsLostBefore = scn.GetDSLostPileCount();

		var loseAction = new TopLevelGameTextAction(seeker, scn.DS, seeker.getCardId());
		loseAction.setText("Make a character lost");
		loseAction.appendEffect(new LoseCardsFromTableEffect(loseAction, Arrays.asList(beru, seeker), true));
		var awaiting = (CardActionSelectionDecision) scn.userFeedback().getAwaitingDecision(scn.DS);
		String[] actionIdsBefore = scn.DSGetADParam("actionId");
		int newIndex = actionIdsBefore == null ? 0 : actionIdsBefore.length;
		awaiting.addAction(loseAction);
		scn.DSDecided(String.valueOf(newIndex));
		scn.PassAllResponses();

		for (int i = 0; i < 12; i++) {
			boolean dsPending = scn.DSGetDecision() != null;
			boolean lsPending = scn.LSGetDecision() != null;
			if (!dsPending && !lsPending) {
				break;
			}
			if (dsPending && scn.DSHasCardChoiceAvailable(beru)) {
				scn.DSChooseCard(beru);
				scn.PassAllResponses();
				continue;
			}
			if (dsPending && scn.DSHasCardChoiceAvailable(seeker)) {
				scn.DSChooseCard(seeker);
				scn.PassAllResponses();
				continue;
			}
			if (lsPending && scn.LSHasCardChoiceAvailable(beru)) {
				scn.LSChooseCard(beru);
				scn.PassAllResponses();
				continue;
			}
			if (lsPending && scn.LSHasCardChoiceAvailable(weapon)) {
				scn.LSChooseCard(weapon);
				scn.PassAllResponses();
				continue;
			}
			scn.PassAllResponses();
		}
		if (scn.DSGetDecision() != null || scn.LSGetDecision() != null) {
			scn.PassAllResponses();
		}

		assertInZone(Zone.LOST_PILE, beru);
		assertInZone(Zone.LOST_PILE, weapon);
		assertInZone(Zone.LOST_PILE, seeker);
		assertTrue(scn.GetLSLostPileCount() >= lsLostBefore + 2);
		assertTrue(scn.GetDSLostPileCount() >= dsLostBefore + 1);
	}
}
