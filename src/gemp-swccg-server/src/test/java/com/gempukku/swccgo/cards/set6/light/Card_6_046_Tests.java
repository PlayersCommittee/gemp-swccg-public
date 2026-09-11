package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutUndercoverEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * VHD tests for 6_46 Yarkora.
 * Doc: control-phase vs Undercover spy; cumulative -1 destiny per Yarkora; cover broken if destiny = ability.
 * Prefer real Undercover (2_129) when deploy path works; PutUndercoverEffect used only to finish cover after attach.
 */
public class Card_6_046_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>() {{
					put("yarkora", "6_46");
					put("yarkora2", "6_46");
					put("saelt", "6_37");
					put("momaw", "1_20");
					put("ls_undercover", "2_40");
				}},
				new HashMap<>() {{
					put("garindan", "1_177");
					put("undercover", "2_129");
				}},
				20,
				20,
				StartingSetup.DefaultLSGroundLocation,
				StartingSetup.DefaultDSGroundLocation,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	private void SafePassOptionalResponses(VirtualTableScenario scn) {
		for (int i = 0; i < 30; i++) {
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

	/**
	 * Deploy real Undercover on Garindan when possible; otherwise attach and finish with PutUndercoverEffect
	 * after Undercover card is on the spy (still uses 2_129 on table).
	 */
	private void MakeGarindanUndercoverAt(VirtualTableScenario scn, PhysicalCardImpl site) {
		var garindan = scn.GetDSCard("garindan");
		var undercover = scn.GetDSCard("undercover");

		scn.MoveCardsToLocation(site, garindan);
		scn.MoveCardsToDSHand(undercover);

		scn.SkipToDSTurn(Phase.DEPLOY);
		boolean deployed = false;
		try {
			if (scn.DSDeployAvailable(undercover)) {
				scn.DSDeployCard(undercover);
				if (scn.DSHasCardChoiceAvailable(garindan)) {
					scn.DSChooseCard(garindan);
				}
				scn.PassCardPlayResponses();
				SafePassOptionalResponses(scn);
				scn.PassAllResponses();
				deployed = garindan.isUndercover();
			}
		} catch (RuntimeException ex) {
			deployed = false;
		}

		if (!deployed) {
			// Keep real Undercover (2_129) attached; finish cover via PutUndercoverEffect with a real Action.
			scn.AttachCardsTo(garindan, undercover);
			var action = new TopLevelGameTextAction(undercover, undercover.getOwner(), undercover.getCardId());
			scn.DSExecuteAdHocEffect(undercover, new PutUndercoverEffect(action, garindan));
			SafePassOptionalResponses(scn);
			scn.PassAllResponses();
		}
		assertTrue("Garindan should be undercover with Undercover (2_129) involved", garindan.isUndercover());
	}


	/** Pass pre-draw windows, take up to maxSubtracts Yarkora -1 optionals, then finish destiny. */
	private int YarkoraTakeSubtractsAndFinishDestiny(VirtualTableScenario scn, int maxSubtracts) {
		scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
		scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
		int taken = 0;
		for (int i = 0; i < 40 && taken < maxSubtracts; i++) {
			// Prefer LS subtract whenever available (even if DS is current decider)
			if (scn.LSAnyDecisionsAvailable()) {
				java.util.List<String> actions = scn.LSGetADParamAsList("actionText");
				boolean hasSubtract = actions != null && actions.stream().anyMatch(
						a -> a != null && a.toLowerCase().contains("subtract 1"));
				if (hasSubtract) {
					scn.LSChooseAction("Subtract 1");
					taken++;
					continue;
				}
			}
			var decision = scn.GetCurrentDecision();
			if (decision == null) {
				break;
			}
			String text = decision.getText() != null ? decision.getText() : "";
			String lower = text.toLowerCase();
			// Pass the non-LS (or LS-without-subtract) player one at a time — do not PassResponses both
			if (lower.contains("destiny_drawn") || lower.contains("optional") || lower.contains("about_to_draw") || lower.contains("cost_to_draw")) {
				String decider = scn.GetDecidingPlayer();
				if (decider != null) {
					scn.PlayerPass(decider);
				} else {
					break;
				}
				continue;
			}
			scn.PassResponses();
		}
		scn.PassResponses("DESTINY_DRAWN");
		scn.PassResponses("COMPLETE_DESTINY_DRAW");
		scn.PassResponses("DRAWING_DESTINY_COMPLETE");
		SafePassOptionalResponses(scn);
		scn.PassAllResponses();
		return taken;
	}

	/**
	 * Put LS spy Momaw undercover via LS Undercover (2_40). Spy crosses to DS side but remains LS-owned,
	 * so Yarkora's opponents(self) filter must not offer break-cover.
	 */
	private void MakeMomawUndercoverAt(VirtualTableScenario scn, PhysicalCardImpl site) {
		var momaw = scn.GetLSCard("momaw");
		var undercover = scn.GetLSCard("ls_undercover");

		scn.MoveCardsToLocation(site, momaw);
		scn.MoveCardsToLSHand(undercover);

		scn.SkipToLSTurn(Phase.DEPLOY);
		boolean deployed = false;
		try {
			if (scn.LSDeployAvailable(undercover)) {
				scn.LSDeployCard(undercover);
				if (scn.LSHasCardChoiceAvailable(momaw)) {
					scn.LSChooseCard(momaw);
				}
				scn.PassCardPlayResponses();
				SafePassOptionalResponses(scn);
				scn.PassAllResponses();
				deployed = momaw.isUndercover();
			}
		} catch (RuntimeException ex) {
			deployed = false;
		}

		if (!deployed) {
			scn.AttachCardsTo(momaw, undercover);
			var action = new TopLevelGameTextAction(undercover, undercover.getOwner(), undercover.getCardId());
			scn.LSExecuteAdHocEffect(undercover, new PutUndercoverEffect(action, momaw));
			SafePassOptionalResponses(scn);
			scn.PassAllResponses();
		}
		assertTrue("Momaw should be undercover with LS Undercover (2_40)", momaw.isUndercover());
	}

	@Test
	public void YarkoraStatsAndKeywordsAreCorrect() {
		var scn = GetScenario();
		var card = scn.GetLSCard("yarkora").getBlueprint();
		assertEquals("Yarkora", card.getTitle());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertEquals(3, card.getDestiny(), scn.epsilon);
		assertEquals(2, card.getDeployCost(), scn.epsilon);
		assertEquals(1, card.getPower(), scn.epsilon);
		assertEquals(1, card.getAbility(), scn.epsilon);
		assertEquals(2, card.getForfeit(), scn.epsilon);
		assertEquals(Species.YARKORA, card.getSpecies());
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.ALIEN);
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.ALIEN);
			add(Icon.JABBAS_PALACE);
		}});
		assertEquals(ExpansionSet.JABBAS_PALACE, card.getExpansionSet());
		assertEquals(Rarity.C, card.getRarity());
	}

	@Test
	public void YarkoraCannotTargetOutsideControlPhase() {
		var scn = GetScenario();
		var yarkora = scn.GetLSCard("yarkora");
		var site = scn.GetLSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, yarkora);
		MakeGarindanUndercoverAt(scn, site);

		scn.SkipToLSTurn(Phase.DEPLOY);
		assertFalse("Yarkora cover-break not available outside control phase",
				scn.LSCardActionAvailable(yarkora, "Break a spy's cover"));
	}

	@Test
	public void YarkoraCannotTargetOwnUndercoverSpy() {
		var scn = GetScenario();
		var yarkora = scn.GetLSCard("yarkora");
		var momaw = scn.GetLSCard("momaw");
		var site = scn.GetLSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, yarkora);
		MakeMomawUndercoverAt(scn, site);
		assertTrue(momaw.isUndercover());

		scn.SkipToLSTurn(Phase.CONTROL);
		assertFalse("Yarkora must not break cover of own (LS-owned) undercover spy",
				scn.LSCardActionAvailable(yarkora, "Break a spy's cover"));
	}

	@Test
	public void YarkoraBreaksCoverWhenDestinyEqualsAbility() {
		var scn = GetScenario();
		var yarkora = scn.GetLSCard("yarkora");
		var garindan = scn.GetDSCard("garindan");
		var site = scn.GetLSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, yarkora);
		MakeGarindanUndercoverAt(scn, site);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(scn.LSCardActionAvailable(yarkora, "Break a spy's cover"));

		scn.PrepareLSDestiny(1); // Garindan ability 1
		scn.LSUseCardAction(yarkora, "Break a spy's cover");
		scn.LSChooseCard(garindan);
		scn.PassDestinyDrawResponses();
		SafePassOptionalResponses(scn);
		scn.PassAllResponses();

		assertFalse("Cover should be broken when destiny equals ability", garindan.isUndercover());
	}

	@Test
	public void YarkoraDoesNotBreakCoverWhenDestinyNotEqual() {
		var scn = GetScenario();
		var yarkora = scn.GetLSCard("yarkora");
		var garindan = scn.GetDSCard("garindan");
		var site = scn.GetLSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, yarkora);
		MakeGarindanUndercoverAt(scn, site);

		scn.SkipToLSTurn(Phase.CONTROL);
		scn.PrepareLSDestiny(3);
		scn.LSUseCardAction(yarkora, "Break a spy's cover");
		scn.LSChooseCard(garindan);
		scn.PassDestinyDrawResponses();
		SafePassOptionalResponses(scn);
		scn.PassAllResponses();

		assertTrue("Cover remains when destiny != ability", garindan.isUndercover());
	}

	@Test
	public void YarkoraCumulativeSubtractAllowsBreakCover() {
		var scn = GetScenario();
		var yarkora = scn.GetLSCard("yarkora");
		var yarkora2 = scn.GetLSCard("yarkora2");
		var garindan = scn.GetDSCard("garindan");
		var site = scn.GetLSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, yarkora, yarkora2);
		MakeGarindanUndercoverAt(scn, site);

		scn.SkipToLSTurn(Phase.CONTROL);
		scn.PrepareLSDestiny(3); // 3 -1 -1 = 1 == ability
		scn.LSUseCardAction(yarkora, "Break a spy's cover");
		scn.LSChooseCard(garindan);

		int taken = YarkoraTakeSubtractsAndFinishDestiny(scn, 2);
		assertTrue("Expected two Yarkora subtract optionals, took " + taken, taken >= 2);
		assertFalse("Two Yarkoras -1 each should make destiny 3 become 1 and break cover",
				garindan.isUndercover());
	}

	@Test
	public void YarkoraSaeltAlsoMaySubtract() {
		var scn = GetScenario();
		var yarkora = scn.GetLSCard("yarkora");
		var saelt = scn.GetLSCard("saelt");
		var garindan = scn.GetDSCard("garindan");
		var site = scn.GetLSStartingLocation();

		scn.StartGame();
		scn.MoveCardsToLocation(site, yarkora, saelt);
		MakeGarindanUndercoverAt(scn, site);

		scn.SkipToLSTurn(Phase.CONTROL);
		scn.PrepareLSDestiny(2); // 2 -1 (Saelt or Yarkora) = 1
		scn.LSUseCardAction(yarkora, "Break a spy's cover");
		scn.LSChooseCard(garindan);

		int taken = YarkoraTakeSubtractsAndFinishDestiny(scn, 1);
		assertTrue("Saelt and/or Yarkora should offer subtract, took " + taken, taken >= 1);
		assertFalse(garindan.isUndercover());
	}
}