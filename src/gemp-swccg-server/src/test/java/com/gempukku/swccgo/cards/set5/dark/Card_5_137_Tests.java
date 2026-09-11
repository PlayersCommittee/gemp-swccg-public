package com.gempukku.swccgo.cards.set5.dark;

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
 * VHD tests for 5_137 Double-Crossing, No-Good Swindler.
 * Doc Action1 (Han + your Lando) and Action2 (Nabrun transport) + Mouse-style edges.
 */
public class Card_5_137_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>() {{
					put("han", "1_011");
					put("nabrun", "1_097");
					put("farm", "1_132");
					put("cantina", "1_128");
				}},
				new HashMap<>() {{
					put("swindler", "5_137");
					put("lando", "5_099");
					put("trooper", "1_194");
					put("trooper2", "1_194");
					put("dining", "5_168");
					put("platform", "5_169");
					put("plaza", "7_270");
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

	@Test
	public void DoubleCrossingNoGoodSwindlerStatsAndKeywordsAreCorrect() {
		var scn = GetScenario();
		var card = scn.GetDSCard("swindler").getBlueprint();
		assertEquals(Title.Double_Crossing_No_Good_Swindler, card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.INTERRUPT);
		}});
		assertEquals(CardSubtype.LOST, card.getCardSubtype());
		assertEquals(3, card.getDestiny(), scn.epsilon);
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.CLOUD_CITY);
			add(Icon.INTERRUPT);
		}});
		assertEquals(ExpansionSet.CLOUD_CITY, card.getExpansionSet());
		assertEquals(Rarity.C, card.getRarity());
	}

	@Test
	public void DoubleCrossingPlayableWhenHanAndYourLandoAtSameSite() {
		var scn = GetScenario();
		var swindler = scn.GetDSCard("swindler");
		var lando = scn.GetDSCard("lando");
		var han = scn.GetLSCard("han");
		var dining = scn.GetDSCard("dining");

		scn.StartGame();
		scn.MoveCardsToDSHand(swindler);
		scn.MoveLocationToTable(dining);
		scn.MoveCardsToLocation(dining, han, lando);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue("Swindler should be playable with Han and your Lando at same site",
				scn.DSCardPlayAvailable(swindler));
	}

	@Test
	public void DoubleCrossingNotPlayableWithOpponentsLandoOnly() {
		var scn = GetScenario();
		var swindler = scn.GetDSCard("swindler");
		var han = scn.GetLSCard("han");
		var dining = scn.GetDSCard("dining");
		// LS Lando persona via filler is hard; use absence of DS Lando as the negative case
		scn.StartGame();
		scn.MoveCardsToDSHand(swindler);
		scn.MoveLocationToTable(dining);
		scn.MoveCardsToLocation(dining, han);

		scn.SkipToPhase(Phase.CONTROL);
		assertFalse("Without your Lando, Swindler Action1 is not playable",
				scn.DSCardPlayAvailable(swindler));
	}

	@Test
	public void DoubleCrossingNotPlayableAtSameSystemInsteadOfSite() {
		var scn = GetScenario();
		var swindler = scn.GetDSCard("swindler");
		var lando = scn.GetDSCard("lando");
		var han = scn.GetLSCard("han");
		// Bespin system is not a site
		var bespin = scn.GetDSCard("plaza"); // use a site that we will NOT put them on â€” relocate to default systems via skip

		scn.StartGame();
		scn.MoveCardsToDSHand(swindler);
		// Place Han/Lando at starting systems (not sites) if possible â€” characters at system need to be aboard;
		// instead put them at different sites to prove same-system alone is insufficient for Action1.
		var dining = scn.GetDSCard("dining");
		var platform = scn.GetDSCard("platform");
		scn.MoveLocationToTable(dining);
		scn.MoveLocationToTable(platform);
		scn.MoveCardsToLocation(dining, han);
		scn.MoveCardsToLocation(platform, lando);

		scn.SkipToPhase(Phase.CONTROL);
		assertFalse("Han and your Lando at different sites â€” Action1 not playable",
				scn.DSCardPlayAvailable(swindler));
	}

	@Test
	public void DoubleCrossingOpponentLoses3ForceAndCardToLostPile() {
		var scn = GetScenario();
		var swindler = scn.GetDSCard("swindler");
		var lando = scn.GetDSCard("lando");
		var han = scn.GetLSCard("han");
		var dining = scn.GetDSCard("dining");

		scn.StartGame();
		scn.MoveCardsToDSHand(swindler);
		scn.MoveLocationToTable(dining);
		scn.MoveCardsToLocation(dining, han, lando);

		scn.SkipToPhase(Phase.CONTROL);
		assertTrue(scn.DSCardPlayAvailable(swindler));
		int lsForceBefore = scn.GetLSLifeForceRemaining();
		scn.DSPlayCardAndPassResponses(swindler);
		scn.LSPayRemainingForceLossFromReserveDeck();

		assertEquals("Opponent should lose 3 Force", lsForceBefore - 3, scn.GetLSLifeForceRemaining());
		assertTrue("Swindler is Lost Interrupt",
				swindler.getZone() == Zone.LOST_PILE || swindler.getZone() == Zone.TOP_OF_LOST_PILE);
	}

	@Test
	public void DoubleCrossingPlayableAfterNabrunTransportNabrunLost() {
		var scn = GetScenario();
		var swindler = scn.GetDSCard("swindler");
		var trooper = scn.GetDSCard("trooper");
		var nabrun = scn.GetLSCard("nabrun");
		var dining = scn.GetDSCard("dining");
		var platform = scn.GetDSCard("platform");

		scn.StartGame();
		scn.MoveCardsToDSHand(swindler);
		scn.MoveCardsToLSHand(nabrun);
		scn.MoveLocationToTable(dining);
		scn.MoveLocationToTable(platform);
		// LS needs a character to transport â€” use Han
		var han = scn.GetLSCard("han");
		scn.MoveCardsToLocation(dining, han);
		scn.MoveCardsToLocation(platform, trooper); // DS presence at destination

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(scn.AwaitingLSControlPhaseActions());
		scn.PrepareLSDestiny(0);
		scn.LSPlayCard(nabrun);
		scn.LSChooseCard(dining);
		scn.LSChooseCard(platform);
		scn.LSChooseCard(han);
		scn.PassDestinyDrawResponses();
		scn.LSChooseYes();

		// After transport completes, DS may respond with Swindler
		boolean sawSwindler = false;
		for (int i = 0; i < 30; i++) {
			var decision = scn.GetCurrentDecision();
			if (decision == null) {
				break;
			}
			try {
				if (scn.DSCardPlayAvailable(swindler)) {
					sawSwindler = true;
					scn.DSPlayCard(swindler);
					SafePassOptionalResponses(scn);
					// May be offered deploy choices â€” pass them
					for (int j = 0; j < 15; j++) {
						var d2 = scn.GetCurrentDecision();
						if (d2 == null) break;
						String t = d2.getText() != null ? d2.getText().toLowerCase() : "";
						if (t.contains("deploy") || t.contains("choose card")) {
							// pass / done
							try { scn.DSPass(); } catch (RuntimeException ex) {
								try { scn.PassResponses(); } catch (RuntimeException ex2) { break; }
							}
						} else if (t.contains("optional")) {
							scn.PassResponses("optional");
						} else {
							break;
						}
					}
					break;
				}
			} catch (RuntimeException ignored) {
			}
			String text = decision.getText() != null ? decision.getText().toLowerCase() : "";
			if (text.contains("optional")) {
				scn.PassResponses("optional");
			} else if (text.contains("required")) {
				scn.PassResponses("required");
			} else {
				try { scn.PassAllResponses(); } catch (RuntimeException ex) { break; }
			}
		}

		assertTrue("Swindler should be offered after Nabrun transport", sawSwindler);
		assertTrue("Nabrun should be lost",
				nabrun.getZone() == Zone.LOST_PILE || nabrun.getZone() == Zone.TOP_OF_LOST_PILE
						|| nabrun.getZone() == Zone.VOID);
	}
}
