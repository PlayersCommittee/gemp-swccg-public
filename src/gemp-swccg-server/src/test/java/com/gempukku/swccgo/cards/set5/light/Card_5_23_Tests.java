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
 * Table-side mode covered; Force-pile / Beggar / Slip Sliding Away pending Force-pile engine (Chief ping).
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
}

