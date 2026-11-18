package com.gempukku.swccgo.cards.set3.dark;

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
 * I'd Just As Soon Kiss A Wookiee (3_127): dark twin of It Can Wait.
 * Issue 1163: "may deploy for free" after bounce must offer free + printed-cost deploy.
 */
public class Card_3_127_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("trooper", "1_28");
					put("presence", "1_28");
					put("mosEisley", "1_133");
				}},
				new HashMap<>()
				{{
					put("kissWookiee", "3_127");
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
		String[] cardIds = scn.GetADParam(scn.LS, "cardId");
		String[] actionTexts = scn.GetADParam(scn.LS, "actionText");
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

	@Test
	public void IdJustAsSoonKissAWookieeStatsAndKeywordsAreCorrect() {
		var scn = GetScenario();
		var card = scn.GetDSCard("kissWookiee").getBlueprint();

		assertEquals("I'd Just As Soon Kiss A Wookiee", card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
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
	public void IdJustAsSoonKissAWookieeOffersFreeAndPrintedRedeployAfterBounce() {
		var scn = GetScenario();
		var kissWookiee = scn.GetDSCard("kissWookiee");
		var trooper = scn.GetLSCard("trooper");
		var presence = scn.GetLSCard("presence");
		var mosEisley = scn.GetLSCard("mosEisley");

		scn.MoveCardsToDSHand(kissWookiee);
		scn.MoveCardsToLSHand(trooper);

		scn.StartGame();
		scn.MoveLocationToTable(mosEisley);
		scn.MoveCardsToLocation(mosEisley, presence);
		scn.LSActivateForceCheat(5);
		scn.DSActivateForceCheat(5);

		scn.SkipToLSTurn(Phase.DEPLOY);
		assertTrue(scn.LSDeployAvailable(trooper));
		scn.LSDeployCard(trooper);
		assertTrue(scn.LSDecisionAvailable("Choose where to deploy") || scn.LSHasCardChoiceAvailable(mosEisley));
		scn.LSChooseCard(mosEisley);

		if (!scn.DSCardActionAvailable(kissWookiee)) {
			scn.PassForceUseResponses();
		}
		assertTrue(scn.DSCardActionAvailable(kissWookiee));
		scn.DSPlayCard(kissWookiee);
		scn.PassAllResponses();

		assertEquals(Zone.HAND, trooper.getZone());

		scn.SkipToLSTurn(Phase.DEPLOY);
		assertEquals(Zone.HAND, trooper.getZone());

		List<String> deploys = deployTextsForCard(scn, trooper);
		assertEquals("deploy actions: " + deploys, 2, deploys.size());
		assertTrue("first action should be free: " + deploys, deploys.get(0).toLowerCase().contains("for free"));
		assertTrue("second action should state 1 Force: " + deploys, deploys.get(1).contains("for 1 Force"));
		assertTrue(scn.LSCardActionAvailable(trooper, "for free"));
		assertTrue(scn.LSCardActionAvailable(trooper, "for 1 Force"));
	}
}
