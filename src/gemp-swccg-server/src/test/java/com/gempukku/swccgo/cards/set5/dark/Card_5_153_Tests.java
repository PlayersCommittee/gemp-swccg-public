package com.gempukku.swccgo.cards.set5.dark;

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
 * Shocking Revelation (5_153) ? bug #54: attached device must go Lost with on-table scan source.
 */
public class Card_5_153_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>() {{
					put("karrde", "10_24");
					put("device", "7_54");
				}},
				new HashMap<>() {{
					put("shocking", "5_153");
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
	public void ShockingRevelationStatsAndKeywordsAreCorrect() {
		var scn = GetScenario();
		var card = scn.GetDSCard("shocking").getBlueprint();
		assertEquals("Shocking Revelation", card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		assertTrue(card.isCardType(CardType.INTERRUPT));
		assertEquals(5, card.getDestiny(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.CLOUD_CITY));
	}

	@Test
	public void ShockingRevelationTargetingKarrdeWithAttachedDeviceBothGoLost() throws DecisionResultInvalidException {
		var scn = GetScenario();

		var karrde = scn.GetLSCard("karrde");
		var device = scn.GetLSCard("device");
		var shocking = scn.GetDSCard("shocking");
		var site = scn.GetLSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, karrde);
		scn.AttachCardsTo(karrde, device);

		assertEquals(Zone.ATTACHED, device.getZone());
		assertEquals(karrde, device.getAttachedTo());
		assertTrue(karrde.getCardsAttached().contains(device));

		scn.SkipToDSTurn(Phase.CONTROL);
		assertTrue(scn.AwaitingDSControlPhaseActions());

		int lsLostBefore = scn.GetLSLostPileCount();

		var loseAction = new TopLevelGameTextAction(shocking, scn.DS, shocking.getCardId());
		loseAction.setText("Make opponent lose Force and card");
		loseAction.appendEffect(new LoseCardsFromOffTableSimultaneouslyEffect(loseAction, Collections.singleton(karrde), false));
		loseAction.appendEffect(new LoseCardFromTableEffect(loseAction, karrde));
		var awaiting = (CardActionSelectionDecision) scn.userFeedback().getAwaitingDecision(scn.DS);
		String[] actionIdsBefore = scn.DSGetADParam("actionId");
		int newIndex = actionIdsBefore == null ? 0 : actionIdsBefore.length;
		awaiting.addAction(loseAction);
		scn.DSDecided(String.valueOf(newIndex));
		scn.PassAllResponses();

		for (int i = 0; i < 8; i++) {
			if (!(scn.LSDecisionAvailable("Lost Pile") || scn.LSDecisionAvailable("lost")
					|| scn.LSDecisionAvailable("Choose card") || scn.LSDecisionAvailable("place"))) {
				break;
			}
			if (scn.LSHasCardChoiceAvailable(karrde)) {
				scn.LSChooseCard(karrde);
			} else if (scn.LSHasCardChoiceAvailable(device)) {
				scn.LSChooseCard(device);
			} else {
				scn.LSPass();
			}
			scn.PassAllResponses();
		}
		scn.PassAllResponses();

		assertInZone(Zone.LOST_PILE, karrde);
		assertInZone(Zone.LOST_PILE, device);
		assertTrue(scn.GetLSLostPileCount() >= lsLostBefore + 2);
	}
}
