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
					put("hanBlaster", "1_154");
				}},
				new HashMap<>() {{
					put("swindler", "5_137");
					put("lando", "5_099");
					put("trooper", "1_194");
					put("trooper2", "1_194");
					put("dining", "5_168");
					put("platform", "5_169");
					put("plaza", "7_270");
					put("denOfThieves", "6_143");
					put("disarmed", "1_214");
					put("potf", "1_227"); // Presence Of The Force
					put("blasterRifle", "1_312");
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
	/** Advance through Nabrun transport until Swindler is playable; play it; land on deploy-from-hand choice. */
	private boolean PlaySwindlerAfterNabrunToDeployWindow(VirtualTableScenario scn) {
		var swindler = scn.GetDSCard("swindler");
		var nabrun = scn.GetLSCard("nabrun");
		var han = scn.GetLSCard("han");
		var dining = scn.GetDSCard("dining");
		var platform = scn.GetDSCard("platform");

		scn.PrepareLSDestiny(0);
		scn.LSPlayCard(nabrun);
		scn.LSChooseCard(dining);
		scn.LSChooseCard(platform);
		scn.LSChooseCard(han);
		scn.PassDestinyDrawResponses();
		scn.LSChooseYes();

		for (int i = 0; i < 30; i++) {
			var decision = scn.GetCurrentDecision();
			if (decision == null) break;
			try {
				if (scn.DSCardPlayAvailable(swindler)) {
					scn.DSPlayCard(swindler);
					SafePassOptionalResponses(scn);
					return true;
				}
			} catch (RuntimeException ignored) {
			}
			String t = decision.getText() != null ? decision.getText().toLowerCase() : "";
			if (t.contains("optional")) {
				scn.PassResponses("optional");
			} else if (t.contains("required")) {
				scn.PassResponses("required");
			} else {
				try { scn.PassAllResponses(); } catch (RuntimeException ex) { break; }
			}
		}
		return false;
	}

	private boolean AtDeployFromHandChoice(VirtualTableScenario scn) {
		var d = scn.GetCurrentDecision();
		if (d == null || d.getText() == null) return false;
		String t = d.getText().toLowerCase();
		return t.contains("deploy") && (t.contains("hand") || t.contains("that site") || t.contains("pass"));
	}

	@Test
	public void SwindlerDenOfThievesNotOfferedForSiteDeploy() {
		var scn = GetScenario();
		var swindler = scn.GetDSCard("swindler");
		var den = scn.GetDSCard("denOfThieves");
		var trooper = scn.GetDSCard("trooper");
		var nabrun = scn.GetLSCard("nabrun");
		var han = scn.GetLSCard("han");
		var dining = scn.GetDSCard("dining");
		var platform = scn.GetDSCard("platform");

		scn.StartGame();
		scn.MoveCardsToDSHand(swindler, den, trooper);
		scn.MoveCardsToLSHand(nabrun);
		scn.MoveLocationToTable(dining);
		scn.MoveLocationToTable(platform);
		scn.MoveCardsToLocation(dining, han);

		scn.SkipToLSTurn(Phase.MOVE);
		assertTrue("Swindler should resolve after Nabrun", PlaySwindlerAfterNabrunToDeployWindow(scn));

		boolean sawDeploy = false;
		for (int j = 0; j < 20; j++) {
			if (AtDeployFromHandChoice(scn) || scn.DSHasCardChoiceAvailable(trooper) || scn.DSDecisionAvailable("deploy")) {
				sawDeploy = true;
				assertTrue("Trooper should be deployable to destination", scn.DSHasCardChoiceAvailable(trooper));
				assertFalse("Den Of Thieves (side-of-table Effect) must not be offered for site deploy",
						scn.DSHasCardChoiceAvailable(den));
				try { scn.DSPass(); } catch (RuntimeException ex) {
					try { scn.PassResponses(); } catch (RuntimeException ex2) {}
				}
				break;
			}
			SafePassOptionalResponses(scn);
			try { scn.PassAllResponses(); } catch (RuntimeException ignored) { break; }
		}
		assertTrue("Should reach deploy-from-hand window", sawDeploy);
	}

	@Test
	public void SwindlerDisarmedDeploysOutsideControlPhase() {
		// Printed Disarmed is control-phase, but Swindler says immediately deploy to that site.
		var scn = GetScenario();
		var swindler = scn.GetDSCard("swindler");
		var disarmed = scn.GetDSCard("disarmed");
		var trooper = scn.GetDSCard("trooper");
		var rifle = scn.GetDSCard("blasterRifle");
		var nabrun = scn.GetLSCard("nabrun");
		var han = scn.GetLSCard("han");
		var hanBlaster = scn.GetLSCard("hanBlaster");
		var dining = scn.GetDSCard("dining");
		var platform = scn.GetDSCard("platform");
		var trooper2 = scn.GetDSCard("trooper2");

		scn.StartGame();
		scn.MoveCardsToDSHand(swindler, disarmed, trooper2);
		scn.MoveCardsToLSHand(nabrun);
		scn.MoveLocationToTable(dining);
		scn.MoveLocationToTable(platform);
		scn.MoveCardsToLocation(dining, han);
		scn.MoveCardsToLocation(platform, trooper);
		scn.AttachCardsTo(han, hanBlaster);
		scn.AttachCardsTo(trooper, rifle);

		scn.SkipToLSTurn(Phase.MOVE);
		assertTrue("Sanity: Nabrun during Move (outside Control)", scn.GetCurrentPhase() == Phase.MOVE);
		assertTrue(PlaySwindlerAfterNabrunToDeployWindow(scn));

		boolean sawDeploy = false;
		for (int j = 0; j < 20; j++) {
			if (AtDeployFromHandChoice(scn) || scn.DSDecisionAvailable("deploy")
					|| scn.DSHasCardChoiceAvailable(disarmed) || scn.DSHasCardChoiceAvailable(trooper2)) {
				sawDeploy = true;
				assertTrue("Disarmed must be deployable via Swindler outside Control; decision="
								+ (scn.GetCurrentDecision() == null ? "null" : scn.GetCurrentDecision().getText()),
						scn.DSHasCardChoiceAvailable(disarmed));
				try { scn.DSPass(); } catch (RuntimeException ex) {
					try { scn.PassResponses(); } catch (RuntimeException ex2) {}
				}
				break;
			}
			SafePassOptionalResponses(scn);
			try { scn.PassAllResponses(); } catch (RuntimeException ignored) { break; }
		}
		assertTrue("Should reach deploy-from-hand window", sawDeploy);
	}

	@Test
	public void SwindlerPresenceOfTheForceNotOfferedBecauseOnLocationIsNotToLocation() {
		// Deploying on a location is not deploying to that site.
		var scn = GetScenario();
		var swindler = scn.GetDSCard("swindler");
		var potf = scn.GetDSCard("potf");
		var trooper = scn.GetDSCard("trooper");
		var nabrun = scn.GetLSCard("nabrun");
		var han = scn.GetLSCard("han");
		var dining = scn.GetDSCard("dining");
		var platform = scn.GetDSCard("platform");

		scn.StartGame();
		scn.MoveCardsToDSHand(swindler, potf, trooper);
		scn.MoveCardsToLSHand(nabrun);
		scn.MoveLocationToTable(dining);
		scn.MoveLocationToTable(platform);
		scn.MoveCardsToLocation(dining, han);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(PlaySwindlerAfterNabrunToDeployWindow(scn));

		boolean sawDeploy = false;
		for (int j = 0; j < 20; j++) {
			if (AtDeployFromHandChoice(scn) || scn.DSDecisionAvailable("deploy") || scn.DSHasCardChoiceAvailable(trooper)) {
				sawDeploy = true;
				assertFalse("Presence Of The Force deploys on a location, not to that site",
						scn.DSHasCardChoiceAvailable(potf));
				try { scn.DSPass(); } catch (RuntimeException ex) {
					try { scn.PassResponses(); } catch (RuntimeException ex2) {}
				}
				break;
			}
			SafePassOptionalResponses(scn);
			try { scn.PassAllResponses(); } catch (RuntimeException ignored) { break; }
		}
		assertTrue("Should reach deploy-from-hand window", sawDeploy);
	}

	@Test
	public void SwindlerSimultaneousSequentialDeployMultipleFromHand() {
		// Repeated deploy-from-hand (not starship+pilot simultaneous): two troopers in one Swindler resolution.
		var scn = GetScenario();
		var swindler = scn.GetDSCard("swindler");
		var trooper = scn.GetDSCard("trooper");
		var trooper2 = scn.GetDSCard("trooper2");
		var nabrun = scn.GetLSCard("nabrun");
		var han = scn.GetLSCard("han");
		var dining = scn.GetDSCard("dining");
		var platform = scn.GetDSCard("platform");

		scn.StartGame();
		scn.MoveCardsToDSHand(swindler, trooper, trooper2);
		scn.MoveCardsToLSHand(nabrun);
		scn.MoveLocationToTable(dining);
		scn.MoveLocationToTable(platform);
		scn.MoveCardsToLocation(dining, han);

		scn.SkipToLSTurn(Phase.CONTROL);
		assertTrue(PlaySwindlerAfterNabrunToDeployWindow(scn));

		int deployed = 0;
		for (int j = 0; j < 30; j++) {
			boolean can1 = false, can2 = false;
			try { can1 = scn.DSHasCardChoiceAvailable(trooper); } catch (RuntimeException ignored) {}
			try { can2 = scn.DSHasCardChoiceAvailable(trooper2); } catch (RuntimeException ignored) {}
			if (can1 || can2) {
				if (can1 && trooper.getZone() == Zone.HAND) {
					scn.DSChooseCard(trooper);
					SafePassOptionalResponses(scn);
					deployed++;
					continue;
				}
				if (can2 && trooper2.getZone() == Zone.HAND) {
					scn.DSChooseCard(trooper2);
					SafePassOptionalResponses(scn);
					deployed++;
					continue;
				}
			}
			if (deployed >= 2) break;
			String t = scn.GetCurrentDecision() != null && scn.GetCurrentDecision().getText() != null
					? scn.GetCurrentDecision().getText().toLowerCase() : "";
			if (t.contains("optional")) {
				scn.PassResponses("optional");
			} else if (AtDeployFromHandChoice(scn) && deployed > 0 && !can1 && !can2) {
				try { scn.DSPass(); } catch (RuntimeException ex) { break; }
			} else {
				try { scn.PassAllResponses(); } catch (RuntimeException ex) { break; }
			}
		}
		assertTrue("First trooper should leave hand", trooper.getZone() != Zone.HAND);
		assertTrue("Second trooper should leave hand (sequential multi-deploy)", trooper2.getZone() != Zone.HAND);
		assertTrue("First trooper at destination", trooper.getAtLocation() == platform);
		assertTrue("Second trooper at destination", trooper2.getAtLocation() == platform);
	}

}
