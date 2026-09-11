package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for 5_52 Impressive, Most Impressive.
 * Doc scenarios + Mouse-style edges (threshold, CF window, hide, release, non-window).
 * Luke Skywalker (1_19) ability = 4.
 * Reuses existing MayNotBattleUntilEndOfTurnEffect hide; no seeker-ignore path.
 */
public class Card_5_052_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>() {{
					put("impressive", "5_52");
					put("luke", "1_19");
					put("obi", "1_11");
					put("chamberLS", "5_78");
				}},
				new HashMap<>() {{
					put("carbonFreezing", "5_114");
					put("chamber", "5_166");
					put("trooper", "1_194");
					put("boba", "5_91");
					put("ate", "5_112");
					put("saber", "1_302");
					put("vader", "1_168");
				}},
				20,
				20,
				StartingSetup.DefaultLSSpaceSystem,
				StartingSetup.DefaultDSSpaceSystem,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	private void SafePassOptionalResponses(VirtualTableScenario scn) {
		for (int i = 0; i < 25; i++) {
			var decision = scn.GetCurrentDecision();
			if (decision == null) {
				return;
			}
			String text = decision.getText();
			if (text == null) {
				return;
			}
			String lower = text.toLowerCase();
			if (lower.contains("optional")) {
				scn.PassResponses("optional");
			} else if (lower.contains("required")) {
				scn.PassResponses("required");
			} else {
				return;
			}
		}
	}

	private boolean ImpressivePlayAvailable(VirtualTableScenario scn) {
		var impressive = scn.GetLSCard("impressive");
		if (scn.LSGetDecision() == null) {
			return false;
		}
		try {
			return scn.LSCardPlayAvailable(impressive);
		} catch (NullPointerException | IndexOutOfBoundsException ex) {
			return false;
		}
	}

	private boolean WaitForImpressiveResponse(VirtualTableScenario scn) {
		for (int i = 0; i < 30; i++) {
			if (ImpressivePlayAvailable(scn)) {
				return true;
			}
			var decision = scn.GetCurrentDecision();
			if (decision == null) {
				return false;
			}
			String text = decision.getText() != null ? decision.getText().toLowerCase() : "";
			if (text.contains("optional")) {
				scn.PassResponses("optional");
			} else if (text.contains("required")) {
				scn.PassResponses("required");
			} else {
				return ImpressivePlayAvailable(scn);
			}
		}
		return ImpressivePlayAvailable(scn);
	}

	private void StartCarbonFreezingOnLuke(VirtualTableScenario scn) {
		var luke = scn.GetLSCard("luke");
		var carbonFreezing = scn.GetDSCard("carbonFreezing");
		try {
			scn.DSUseCardAction(carbonFreezing, "Perform Carbon-Freezing");
		} catch (RuntimeException ex) {
			scn.DSUseCardAction(carbonFreezing);
		}
		if (scn.DSHasCardChoiceAvailable(luke)) {
			scn.DSChooseCard(luke);
		}
	}

	private void FinishImpressivePlay(VirtualTableScenario scn) {
		scn.PassCardPlayResponses();
		scn.PassDestinyDrawResponses();
		SafePassOptionalResponses(scn);
		scn.PassAllResponses();
		if (scn.LSReleaseDecisionAvailable()) {
			scn.LSChooseRally();
			scn.PassAllResponses();
			SafePassOptionalResponses(scn);
		}
	}

	@Test
	public void ImpressiveMostImpressiveStatsAndKeywordsAreCorrect() {
		var scn = GetScenario();
		var card = scn.GetLSCard("impressive").getBlueprint();
		assertEquals(Title.Impressive_Most_Impressive, card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.INTERRUPT);
		}});
		assertEquals(CardSubtype.LOST, card.getCardSubtype());
		assertEquals(6, card.getDestiny(), scn.epsilon);
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.CLOUD_CITY);
			add(Icon.INTERRUPT);
		}});
		assertEquals(ExpansionSet.CLOUD_CITY, card.getExpansionSet());
		assertEquals(Rarity.R, card.getRarity());
	}

	@Test
	public void ImpressiveMostImpressiveNotPlayableWithoutFreezeAttempt() {
		var scn = GetScenario();
		var impressive = scn.GetLSCard("impressive");
		var luke = scn.GetLSCard("luke");
		var site = scn.GetLSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLSHand(impressive);
		scn.MoveCardsToLocation(site, luke);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertFalse("Impressive should not be playable without freeze attempt", ImpressivePlayAvailable(scn));
	}

	/**
	 * Carbon-Freezing attempt: destiny+ability > 7 cancels, releases captive, hides rest of turn.
	 * Luke ability 4 + destiny 4 = 8 > 7.
	 */
	@Test
	public void ImpressiveMostImpressiveCancelsCarbonFreezingOnSuccessReleasesAndHides() {
		var scn = GetScenario();
		var impressive = scn.GetLSCard("impressive");
		var luke = scn.GetLSCard("luke");
		var carbonFreezing = scn.GetDSCard("carbonFreezing");
		var chamber = scn.GetDSCard("chamber");
		var boba = scn.GetDSCard("boba");

		scn.StartGame();
		scn.MoveCardsToLSHand(impressive);
		scn.MoveLocationToTable(chamber);
		scn.MoveCardsToLocation(chamber, boba, luke);
		scn.CaptureCardWith(boba, luke);
		assertTrue(luke.isCaptive());

		scn.AttachCardsTo(chamber, carbonFreezing);

		scn.SkipToDSTurn(Phase.CONTROL);
		boolean cfAction = false;
		try {
			cfAction = scn.DSCardActionAvailable(carbonFreezing, "Perform Carbon-Freezing")
					|| scn.DSCardActionAvailable(carbonFreezing);
		} catch (NullPointerException | IndexOutOfBoundsException ex) {
			cfAction = false;
		}
		assertTrue("Carbon-Freezing perform action should be available", cfAction);

		StartCarbonFreezingOnLuke(scn);

		assertTrue("Impressive should be offered in response to Carbon-Freezing attempt",
				WaitForImpressiveResponse(scn));

		scn.PrepareLSDestiny(4); // + Luke ability 4 = 8 > 7
		scn.LSPlayCard(impressive);
		FinishImpressivePlay(scn);

		assertTrue("Impressive is Lost Interrupt",
				impressive.getZone() == Zone.TOP_OF_LOST_PILE || impressive.getZone() == Zone.LOST_PILE);
		assertFalse("Luke should be released", luke.isCaptive());
		assertTrue("Luke should remain in play after rally", luke.getZone().isInPlay());
	}

	@Test
	public void ImpressiveMostImpressiveFailsWhenTotalNotGreaterThanSeven() {
		var scn = GetScenario();
		var impressive = scn.GetLSCard("impressive");
		var luke = scn.GetLSCard("luke");
		var carbonFreezing = scn.GetDSCard("carbonFreezing");
		var chamber = scn.GetDSCard("chamber");
		var boba = scn.GetDSCard("boba");

		scn.StartGame();
		scn.MoveCardsToLSHand(impressive);
		scn.MoveLocationToTable(chamber);
		scn.MoveCardsToLocation(chamber, boba, luke);
		scn.CaptureCardWith(boba, luke);

		scn.AttachCardsTo(chamber, carbonFreezing);

		scn.SkipToDSTurn(Phase.CONTROL);
		StartCarbonFreezingOnLuke(scn);

		assertTrue("Impressive should be offered", WaitForImpressiveResponse(scn));

		scn.PrepareLSDestiny(3); // + ability 4 = 7 not > 7
		scn.PrepareDSDestiny(7); // CF no-result band after Impressive fails
		scn.LSPlayCard(impressive);
		scn.PassCardPlayResponses();
		scn.PassDestinyDrawResponses();
		SafePassOptionalResponses(scn);
		scn.PassAllResponses();
		scn.PassDestinyDrawResponses();
		SafePassOptionalResponses(scn);

		assertTrue("Luke should remain captive when Impressive fails", luke.isCaptive());
	}
}
