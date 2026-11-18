package com.gempukku.swccgo.cards.set3.light;

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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * It Can Wait (3_44): after bouncing a just-deployed card, that title may deploy for free
 * on opponent's next turn. Issue 1163: "may" means both free and printed-cost deploy
 * actions (same pattern as Wise Advice / Battle Plan).
 */
public class Card_3_044_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("itCanWait", "3_44");
					put("mosEisley", "1_133");
				}},
				new HashMap<>()
				{{
					put("stormtrooper", "1_194");
					put("presence", "1_194");
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

	private List<String> deployTextsForCard(VirtualTableScenario scn, PhysicalCardImpl card) {
		List<String> texts = new ArrayList<>();
		String id = String.valueOf(card.getCardId());
		String[] cardIds = scn.GetADParam(scn.DS, "cardId");
		String[] actionTexts = scn.GetADParam(scn.DS, "actionText");
		if (cardIds == null || actionTexts == null) {
			return texts;
		}
		for (int i = 0; i < cardIds.length; i++) {
			if (id.equals(cardIds[i]) && actionTexts[i] != null && actionTexts[i].toLowerCase().contains("deploy")) {
				texts.add(actionTexts[i]);
			}
		}
		return texts;
	}

	/**
	 * LS bounces DS Stormtrooper with It Can Wait; next DS Deploy phase the bounced
	 * Stormtrooper is back in hand under the may-deploy-for-free modifier.
	 */
	private void BounceStormtrooperThenSkipToNextDSDeploy(VirtualTableScenario scn) {
		var itCanWait = scn.GetLSCard("itCanWait");
		var mosEisley = scn.GetLSCard("mosEisley");
		var stormtrooper = scn.GetDSCard("stormtrooper");
		var presence = scn.GetDSCard("presence");

		scn.MoveCardsToLSHand(itCanWait);
		scn.MoveCardsToDSHand(stormtrooper);

		scn.StartGame();
		scn.MoveLocationToTable(mosEisley);
		scn.MoveCardsToLocation(mosEisley, presence);
		scn.LSActivateForceCheat(5);
		scn.DSActivateForceCheat(5);

		scn.SkipToDSTurn(Phase.DEPLOY);
		assertTrue(scn.DSDeployAvailable(stormtrooper));
		scn.DSDeployCard(stormtrooper);
		assertTrue(scn.DSDecisionAvailable("Choose where to deploy") || scn.DSHasCardChoiceAvailable(mosEisley));
		scn.DSChooseCard(mosEisley);

		if (!scn.LSCardActionAvailable(itCanWait)) {
			scn.PassForceUseResponses();
		}
		assertTrue(scn.LSCardActionAvailable(itCanWait));
		scn.LSPlayCard(itCanWait);
		scn.PassAllResponses();

		assertEquals(Zone.HAND, stormtrooper.getZone());
		assertTrue(itCanWait.getZone() == Zone.LOST_PILE || itCanWait.getZone() == Zone.TOP_OF_LOST_PILE);

		scn.SkipToDSTurn(Phase.DEPLOY);
		assertEquals(Zone.HAND, stormtrooper.getZone());
	}

	@Test
	public void ItCanWaitStatsAndKeywordsAreCorrect() {
		var scn = GetScenario();
		var card = scn.GetLSCard("itCanWait").getBlueprint();

		assertEquals("It Can Wait", card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.INTERRUPT);
		}});
		assertEquals(CardSubtype.LOST, card.getCardSubtype());
		assertEquals(2, card.getDestiny(), scn.epsilon);
		scn.BlueprintKeywordCheck(card, new ArrayList<>());
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.INTERRUPT);
			add(Icon.HOTH);
		}});
		assertEquals(ExpansionSet.HOTH, card.getExpansionSet());
		assertEquals(Rarity.C2, card.getRarity());
	}

	@Test
	public void ItCanWaitOffersFreeAndPrintedRedeployAfterBounce() {
		// Issue 1163: after It Can Wait returns a just-deployed Stormtrooper, opponent's
		// next Deploy phase must offer both free and printed-cost (1 Force) deploy actions.
		var scn = GetScenario();
		var stormtrooper = scn.GetDSCard("stormtrooper");

		BounceStormtrooperThenSkipToNextDSDeploy(scn);

		List<String> deploys = deployTextsForCard(scn, stormtrooper);
		assertEquals("deploy actions: " + deploys, 2, deploys.size());
		assertTrue("first action should be free: " + deploys, deploys.get(0).toLowerCase().contains("for free"));
		assertTrue("second action should state 1 Force: " + deploys, deploys.get(1).contains("for 1 Force"));
		assertTrue(scn.DSCardActionAvailable(stormtrooper, "for free"));
		assertTrue(scn.DSCardActionAvailable(stormtrooper, "for 1 Force"));
	}

	@Test
	public void ItCanWaitFreeRedeployPaysZeroForce() {
		var scn = GetScenario();
		var stormtrooper = scn.GetDSCard("stormtrooper");
		var mosEisley = scn.GetLSCard("mosEisley");

		BounceStormtrooperThenSkipToNextDSDeploy(scn);

		int forceBefore = scn.GetDSForcePileCount();
		assertTrue(scn.DSCardActionAvailable(stormtrooper, "for free"));
		scn.DSPlayCard(stormtrooper, "for free");
		if (scn.DSDecisionAvailable("Choose where to deploy") || scn.DSHasCardChoiceAvailable(mosEisley)) {
			scn.DSChooseCard(mosEisley);
		}
		scn.PassAllResponses();

		assertTrue(stormtrooper.getZone() == Zone.AT_LOCATION || stormtrooper.getZone() == Zone.ATTACHED);
		assertEquals(forceBefore, scn.GetDSForcePileCount());
	}

	@Test
	public void ItCanWaitPrintedRedeployPaysPrintedForce() {
		var scn = GetScenario();
		var stormtrooper = scn.GetDSCard("stormtrooper");
		var mosEisley = scn.GetLSCard("mosEisley");

		BounceStormtrooperThenSkipToNextDSDeploy(scn);

		int forceBefore = scn.GetDSForcePileCount();
		assertTrue(forceBefore >= 1);
		assertTrue(scn.DSCardActionAvailable(stormtrooper, "for 1 Force"));
		scn.DSPlayCard(stormtrooper, "for 1 Force");
		if (scn.DSDecisionAvailable("Choose where to deploy") || scn.DSHasCardChoiceAvailable(mosEisley)) {
			scn.DSChooseCard(mosEisley);
		}
		scn.PassAllResponses();

		assertTrue(stormtrooper.getZone() == Zone.AT_LOCATION || stormtrooper.getZone() == Zone.ATTACHED);
		assertEquals(forceBefore - 1, scn.GetDSForcePileCount());
	}
}
