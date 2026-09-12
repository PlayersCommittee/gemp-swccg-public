package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromOffTableSimultaneouslyEffect;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Shocking Information (5_068) ? bug #54: from-hand scan source lost via OffTable path.
 */
public class Card_5_068_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>() {{
					put("shocking", "5_68");
				}},
				new HashMap<>() {{
					put("scan", "1_266");
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
	public void ShockingInformationStatsAndKeywordsAreCorrect() {
		var scn = GetScenario();
		var card = scn.GetLSCard("shocking").getBlueprint();
		assertEquals("Shocking Information", card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertTrue(card.isCardType(CardType.INTERRUPT));
		assertEquals(5, card.getDestiny(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.CLOUD_CITY));
	}

	@Test
	public void ShockingInformationVsScanningCrewFromHandScanSourceGoesLost() throws DecisionResultInvalidException {
		var scn = GetScenario();

		var shocking = scn.GetLSCard("shocking");
		var scan = scn.GetDSCard("scan");

		scn.StartGame();
		scn.MoveCardsToHand(scan);
		assertEquals(Zone.HAND, scan.getZone());

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(scn.AwaitingLSControlPhaseActions());

		var loseAction = new TopLevelGameTextAction(shocking, scn.LS, shocking.getCardId());
		loseAction.setText("Make opponent lose Force and card");
		loseAction.appendEffect(new LoseCardsFromOffTableSimultaneouslyEffect(loseAction, Collections.singleton(scan), false));
		loseAction.appendEffect(new LoseCardFromTableEffect(loseAction, scan));
		var awaiting = (CardActionSelectionDecision) scn.userFeedback().getAwaitingDecision(scn.LS);
		String[] actionIdsBefore = scn.LSGetADParam("actionId");
		int newIndex = actionIdsBefore == null ? 0 : actionIdsBefore.length;
		awaiting.addAction(loseAction);
		scn.LSDecided(String.valueOf(newIndex));
		scn.PassAllResponses();

		for (int i = 0; i < 6; i++) {
			if (!(scn.DSDecisionAvailable("Lost Pile") || scn.DSDecisionAvailable("lost")
					|| scn.DSDecisionAvailable("Choose card") || scn.DSDecisionAvailable("place"))) {
				break;
			}
			if (scn.DSHasCardChoiceAvailable(scan)) {
				scn.DSChooseCard(scan);
			} else {
				scn.DSPass();
			}
			scn.PassAllResponses();
		}
		scn.PassAllResponses();

		assertInZone(Zone.LOST_PILE, scan);
	}
}
