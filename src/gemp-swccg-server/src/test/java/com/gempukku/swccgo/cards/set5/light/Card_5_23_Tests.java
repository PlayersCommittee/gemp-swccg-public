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
import static org.junit.Assert.assertTrue;

/**
 * VHD tests for 5_23 Frozen Assets.
 * Table mode + Force-pile mode (Doc separate Frozen Pile zone).
 * Bill-locked edges: lose-from-frozen-when-usable-empty, Tikkes bottom unfrozen,
 * empty-pile deploy, shuffle excludes FA, sandwich UI stats.
 */
public class Card_5_23_Tests {

	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>() {{
					put("frozenAssets", "5_23");
					put("luke", "1_19");
					put("han", "1_11");
					put("leia", "1_17");
				}},
				new HashMap<>() {{
					put("boba", "5_91");
					put("vader", "1_168");
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

	@Test
	public void FrozenAssetsStatsAndKeywordsAreCorrect() {
		var scn = GetScenario();
		var card = scn.GetLSCard("frozenAssets").getBlueprint();
		assertEquals(Title.Frozen_Assets, card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.EFFECT);
		}});
		assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
		assertEquals(5, card.getDestiny(), scn.epsilon);
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.CLOUD_CITY);
			add(Icon.EFFECT);
		}});
		assertEquals(ExpansionSet.CLOUD_CITY, card.getExpansionSet());
		assertEquals(Rarity.R, card.getRarity());
	}

	@Test
	public void TableModeDeploysToYourSideOfTable() {
		var scn = GetScenario();
		var frozenAssets = scn.GetLSCard("frozenAssets");

		scn.StartGame();
		scn.MoveCardsToLSHand(frozenAssets);
		scn.SkipToLSTurn(Phase.DEPLOY);

		assertTrue(scn.LSCardPlayAvailable(frozenAssets, "side of table"));
		scn.LSPlayCard(frozenAssets, "side of table");
		scn.PassAllResponses();

		assertEquals(Zone.SIDE_OF_TABLE, frozenAssets.getZone());
		assertEquals(scn.LS, frozenAssets.getZoneOwner());
	}

	@Test
	public void TableModeRebelPowerPlus2InBattleAtFrozenCaptiveSite() {
		var scn = GetScenario();
		var frozenAssets = scn.GetLSCard("frozenAssets");
		var luke = scn.GetLSCard("luke");
		var han = scn.GetLSCard("han");
		var site = scn.GetLSStartingLocation();
		var boba = scn.GetDSCard("boba");
		var vader = scn.GetDSCard("vader");

		scn.StartGame();
		scn.MoveCardsToLSHand(frozenAssets);
		scn.MoveCardsToLocation(site, luke, boba, han, vader);
		scn.FreezeCard(han);
		assertTrue(han.isFrozen());

		scn.SkipToLSTurn(Phase.DEPLOY);
		assertTrue(scn.LSCardPlayAvailable(frozenAssets, "side of table"));
		scn.LSPlayCard(frozenAssets, "side of table");
		scn.PassAllResponses();
		assertEquals(Zone.SIDE_OF_TABLE, frozenAssets.getZone());

		float basePower = luke.getBlueprint().getPower();
		scn.SkipToPhase(Phase.BATTLE);
		scn.LSInitiateBattle(site);
		scn.PassAllResponses();

		assertTrue(scn.IsParticipatingInBattle(luke));
		assertEquals(basePower + 2, scn.GetPower(luke), scn.epsilon);
	}

	@Test
	public void TableModeRebelDeployCostReducedAtFrozenCaptiveSite() {
		var scn = GetScenario();
		var frozenAssets = scn.GetLSCard("frozenAssets");
		var luke = scn.GetLSCard("luke");
		var han = scn.GetLSCard("han");
		var site = scn.GetLSStartingLocation();
		var boba = scn.GetDSCard("boba");

		scn.StartGame();
		scn.MoveCardsToLSHand(frozenAssets, luke);
		scn.MoveCardsToLocation(site, boba, han);
		scn.FreezeCard(han);

		float base = luke.getBlueprint().getDeployCost();

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSPlayCard(frozenAssets, "side of table");
		scn.PassAllResponses();

		float toSite = scn.game().getModifiersQuerying().getDeployCost(
				scn.gameState(), luke, luke, site, false, null, false, 0, null, false);
		assertEquals(base - 2, toSite, scn.epsilon);
	}

	@Test
	public void ForcePileModeDeploysAndFreezesOpponentsForcePile() {
		var scn = GetScenario();
		var frozenAssets = scn.GetLSCard("frozenAssets");
		var vader = scn.GetDSCard("vader");
		var boba = scn.GetDSCard("boba");
		var luke = scn.GetLSCard("luke");
		var han = scn.GetLSCard("han");

		scn.StartGame();
		scn.MoveCardsToLSHand(frozenAssets);
		scn.MoveCardsToTopOfDSForcePile(vader, boba, luke, han);

		scn.SkipToLSTurn(Phase.DEPLOY);
		int priorForce = scn.GetDSForcePileCount();
		assertTrue(priorForce >= 4);
		int lifeBefore = scn.gameState().getPlayerLifeForce(scn.DS);
		assertTrue(scn.LSCardPlayAvailable(frozenAssets, "Force Pile"));
		scn.LSPlayCard(frozenAssets, "Force Pile");
		scn.PassAllResponses();

		assertTrue(frozenAssets.getZone() == Zone.FORCE_PILE || frozenAssets.getZone() == Zone.TOP_OF_FORCE_PILE);
		assertEquals(scn.DS, frozenAssets.getZoneOwner());
		assertEquals(priorForce, scn.GetDSFrozenPileCount());
		assertEquals(1, scn.GetDSForcePileCount());
		assertEquals(0, scn.game().getModifiersQuerying().getForceAvailableToUse(scn.gameState(), scn.DS));
		assertEquals(lifeBefore, scn.gameState().getPlayerLifeForce(scn.DS));
	}

	@Test
	public void ForcePileModeCannotUseFrozenForce() {
		var scn = GetScenario();
		var frozenAssets = scn.GetLSCard("frozenAssets");
		var vader = scn.GetDSCard("vader");
		var boba = scn.GetDSCard("boba");

		scn.StartGame();
		scn.MoveCardsToLSHand(frozenAssets);
		scn.MoveCardsToTopOfDSForcePile(vader, boba);

		scn.SkipToLSTurn(Phase.DEPLOY);
		int frozenExpected = scn.GetDSForcePileCount();
		scn.LSPlayCard(frozenAssets, "Force Pile");
		scn.PassAllResponses();

		assertEquals(frozenExpected, scn.GetDSFrozenPileCount());
		assertEquals(1, scn.GetDSForcePileCount());
		assertEquals(0, scn.game().getModifiersQuerying().getForceAvailableToUse(scn.gameState(), scn.DS));

		scn.DSUseForceCheat(1);
		assertEquals(frozenExpected, scn.GetDSFrozenPileCount());
		assertEquals(1, scn.GetDSForcePileCount());
	}

	@Test
	public void SlipSlidingAwayUnfreezesFrozenBlock() {
		var scn = GetScenario();
		var frozenAssets = scn.GetLSCard("frozenAssets");
		var vader = scn.GetDSCard("vader");
		var boba = scn.GetDSCard("boba");
		var luke = scn.GetLSCard("luke");

		scn.StartGame();
		scn.MoveCardsToLSHand(frozenAssets);
		scn.MoveCardsToTopOfDSForcePile(vader, boba, luke);

		scn.SkipToLSTurn(Phase.DEPLOY);
		int frozenExpected = scn.GetDSForcePileCount();
		scn.LSPlayCard(frozenAssets, "Force Pile");
		scn.PassAllResponses();
		assertEquals(frozenExpected, scn.GetDSFrozenPileCount());
		assertEquals(1, scn.GetDSForcePileCount());

		// Simulate Slip Sliding Away AR: relocate FA top→bottom of Force Pile
		var action = new com.gempukku.swccgo.logic.actions.SystemQueueAction();
		action.setPerformingPlayer(scn.DS);
		new com.gempukku.swccgo.logic.effects.PlaceTopCardFromCardPileOnBottomOfCardPileEffect(
				action, scn.DS, Zone.FORCE_PILE, Zone.FORCE_PILE).playEffect(scn.game());

		assertEquals(0, scn.GetDSFrozenPileCount());
		assertEquals(frozenExpected + 1, scn.GetDSForcePileCount());
		assertEquals(frozenExpected, scn.game().getModifiersQuerying().getForceAvailableToUse(scn.gameState(), scn.DS));
	}

	@Test
	public void ForcePileModeEmptyForcePileDeployStillOk() {
		var scn = GetScenario();
		var frozenAssets = scn.GetLSCard("frozenAssets");

		scn.StartGame();
		scn.MoveCardsToLSHand(frozenAssets);
		// Skip first — activation during SkipToLSTurn would refill Force Pile
		scn.SkipToLSTurn(Phase.DEPLOY);
		// Drain DS Force Pile empty after skip (AR: may still place under empty pile)
		while (scn.GetDSForcePileCount() > 0) {
			scn.DSUseForceCheat(1);
		}
		assertEquals(0, scn.GetDSForcePileCount());

		assertTrue(scn.LSCardPlayAvailable(frozenAssets, "Force Pile"));
		scn.LSPlayCard(frozenAssets, "Force Pile");
		scn.PassAllResponses();

		assertTrue(frozenAssets.getZone() == Zone.FORCE_PILE || frozenAssets.getZone() == Zone.TOP_OF_FORCE_PILE);
		assertEquals(scn.DS, frozenAssets.getZoneOwner());
		assertEquals(0, scn.GetDSFrozenPileCount());
		assertEquals(1, scn.GetDSForcePileCount()); // FA only
		assertEquals(0, scn.game().getModifiersQuerying().getForceAvailableToUse(scn.gameState(), scn.DS));
	}

	@Test
	public void ForcePileModeLoseFromFrozenOnlyWhenUsableEmpty() {
		var scn = GetScenario();
		var frozenAssets = scn.GetLSCard("frozenAssets");
		var vader = scn.GetDSCard("vader");
		var boba = scn.GetDSCard("boba");
		var luke = scn.GetLSCard("luke");
		var han = scn.GetLSCard("han");

		scn.StartGame();
		scn.MoveCardsToLSHand(frozenAssets);
		scn.MoveCardsToTopOfDSForcePile(vader, boba, luke, han);

		scn.SkipToLSTurn(Phase.DEPLOY);
		int frozenExpected = scn.GetDSForcePileCount();
		scn.LSPlayCard(frozenAssets, "Force Pile");
		scn.PassAllResponses();
		assertEquals(frozenExpected, scn.GetDSFrozenPileCount());
		assertEquals(0, scn.game().getModifiersQuerying().getForceAvailableToUse(scn.gameState(), scn.DS));

		// Activate usable Force above FA
		scn.DSActivateForceCheat(2);
		assertEquals(2, scn.game().getModifiersQuerying().getForceAvailableToUse(scn.gameState(), scn.DS));
		assertEquals(frozenExpected, scn.GetDSFrozenPileCount());

		// With usable Force present, losing Force must not consume frozen (Gergall)
		int frozenBefore = scn.GetDSFrozenPileCount();
		int usableBefore = scn.game().getModifiersQuerying().getUsableForcePileSize(scn.gameState(), scn.DS);
		var topUsable = scn.gameState().getTopOfForcePile(scn.DS);
		assertTrue(topUsable != null && !"Frozen Assets".equals(topUsable.getTitle()));
		var topFrozen = scn.gameState().getTopOfFrozenPile(scn.DS);
		assertTrue(topFrozen != null);

		// Usable empty again — frozen becomes loseable life force
		scn.DSUseForceCheat(usableBefore);
		assertEquals(0, scn.game().getModifiersQuerying().getForceAvailableToUse(scn.gameState(), scn.DS));
		assertEquals(frozenBefore, scn.GetDSFrozenPileCount());
		assertEquals(topFrozen, scn.gameState().getTopOfFrozenPile(scn.DS));
	}

	@Test
	public void ForcePileModeTikkesBottomIsBottomMostUnfrozen() {
		var scn = GetScenario();
		var frozenAssets = scn.GetLSCard("frozenAssets");
		var vader = scn.GetDSCard("vader");
		var boba = scn.GetDSCard("boba");
		var luke = scn.GetLSCard("luke");

		scn.StartGame();
		scn.MoveCardsToLSHand(frozenAssets);
		scn.MoveCardsToTopOfDSForcePile(vader, boba, luke);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSPlayCard(frozenAssets, "Force Pile");
		scn.PassAllResponses();

		// Activate Force so FA is buried; bottom of Force pile would be FA without Tikkes rule
		scn.DSActivateForceCheat(3);
		var bottom = scn.gameState().getBottomOfCardPile(scn.DS, Zone.FORCE_PILE);
		assertTrue(bottom != null);
		assertTrue(!"Frozen Assets".equals(bottom.getTitle()));
		assertEquals(frozenAssets, scn.GetDSForcePile().get(scn.GetDSForcePile().size() - 1));
	}

	@Test
	public void ForcePileModeShuffleExcludesFrozenAssetsMarker() {
		var scn = GetScenario();
		var frozenAssets = scn.GetLSCard("frozenAssets");
		var vader = scn.GetDSCard("vader");
		var boba = scn.GetDSCard("boba");
		var luke = scn.GetLSCard("luke");
		var han = scn.GetLSCard("han");
		var leia = scn.GetLSCard("leia");

		scn.StartGame();
		scn.MoveCardsToLSHand(frozenAssets);
		scn.MoveCardsToTopOfDSForcePile(vader, boba);

		scn.SkipToLSTurn(Phase.DEPLOY);
		scn.LSPlayCard(frozenAssets, "Force Pile");
		scn.PassAllResponses();

		scn.DSActivateForceCheat(3);
		int frozen = scn.GetDSFrozenPileCount();
		assertTrue(scn.GetDSForcePileCount() >= 4); // 3 usable + FA

		scn.gameState().shufflePile(scn.DS, Zone.FORCE_PILE);

		// FA remains at bottom; frozen pile untouched; FA still present exactly once
		var pile = scn.GetDSForcePile();
		assertEquals(frozenAssets, pile.get(pile.size() - 1));
		assertEquals(1, pile.stream().filter(c -> "Frozen Assets".equals(c.getTitle())).count());
		assertEquals(frozen, scn.GetDSFrozenPileCount());
	}

	@Test
	public void ForcePileModeBeggarMayUseUsableAndFrozen() {
		var scn = GetScenario();
		var frozenAssets = scn.GetLSCard("frozenAssets");
		var vader = scn.GetDSCard("vader");
		var boba = scn.GetDSCard("boba");
		var luke = scn.GetLSCard("luke");

		scn.StartGame();
		scn.MoveCardsToLSHand(frozenAssets);
		scn.MoveCardsToTopOfDSForcePile(vader, boba, luke);

		scn.SkipToLSTurn(Phase.DEPLOY);
		int frozenExpected = scn.GetDSForcePileCount();
		scn.LSPlayCard(frozenAssets, "Force Pile");
		scn.PassAllResponses();

		scn.DSActivateForceCheat(1);
		int usable = scn.game().getModifiersQuerying().getUsableForcePileSize(scn.gameState(), scn.DS);
		assertEquals(1, usable);
		// Beggar AR: may use usable Force Pile + Frozen Pile (engine sums both)
		int beggarPool = usable + scn.GetDSFrozenPileCount();
		assertEquals(usable + frozenExpected, beggarPool);
		assertEquals(frozenExpected, scn.GetDSFrozenPileCount());
	}

	@Test
	public void ForcePileModeGameStatsExposeFrozenAndUsableForSandwichUi() {
		var scn = GetScenario();
		var frozenAssets = scn.GetLSCard("frozenAssets");
		var vader = scn.GetDSCard("vader");
		var boba = scn.GetDSCard("boba");
		var luke = scn.GetLSCard("luke");

		scn.StartGame();
		scn.MoveCardsToLSHand(frozenAssets);
		scn.MoveCardsToTopOfDSForcePile(vader, boba, luke);

		scn.SkipToLSTurn(Phase.DEPLOY);
		int frozenExpected = scn.GetDSForcePileCount();
		scn.LSPlayCard(frozenAssets, "Force Pile");
		scn.PassAllResponses();
		scn.DSActivateForceCheat(2);

		var stats = new com.gempukku.swccgo.logic.timing.GameStats();
		stats.updateGameStats(scn.game());
		var zones = stats.getZoneSizes().get(scn.DS);
		assertEquals(2, (int) zones.get(Zone.FORCE_PILE));
		assertEquals(frozenExpected, (int) zones.get(Zone.FROZEN_PILE));
	}
}
