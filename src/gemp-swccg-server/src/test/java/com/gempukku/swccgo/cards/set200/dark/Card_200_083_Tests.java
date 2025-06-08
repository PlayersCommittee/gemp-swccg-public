package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

public class Card_200_083_Tests
{
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
				}},
				new HashMap<>()
				{{
					put("ig88", "200_083");
				}},
				10,
				10,
				StartingSetup.DefaultLSGroundLocation,
				StartingSetup.CarbonChamberObjective,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void IG88VStatsAndKeywordsAreCorrect() {
		/**
		 * Title: IG-88 (V)
		 * Uniqueness: UNIQUE
		 * Side: Dark
		 * Type: Character
		 * Subtype: Droid
		 * Destiny: 1
		 * Deploy: 4
		 * Power: 4
		 * Armor: 5
		 * Forfeit: 5
		 * Icons: Dagobah, Pilot, Presence, V 0, Warrior x2
		 * Keywords: Bounty Hunter
		 * Model Type: Assassin
		 * Game Text: [Pilot] 2. May use two weapons. Once per turn, if escorting a captive, may take any one card from
		 * 			Force Pile into hand; reshuffle. May lose 1 Force to cancel a just drawn weapon destiny targeting
		 * 			IG-88. Immune to attrition < 5.
		 * Lore: Bounty hunter. Went berserk upon activation. Murdered all designers at Holowan Mechanicals.
		 * 			IG-88's outstanding 'dismantle on sight' warrant ignored by Darth Vader.
		 * Set: Premiere
		 * Rarity: C3
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("ig88").getBlueprint();

		assertEquals("IG-88", card.getTitle());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		assertTrue(card.isCardType(CardType.DROID));
		//assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
		assertEquals(1, card.getDestiny(), scn.epsilon);
		assertEquals(4, card.getDeployCost(), scn.epsilon);
		assertEquals(4, card.getPower(), scn.epsilon);
		assertEquals(5, card.getArmor(), scn.epsilon);
		assertEquals(5, card.getForfeit(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.DAGOBAH));
		assertEquals(1, card.getIconCount(Icon.PILOT));
		assertEquals(1, card.getIconCount(Icon.PRESENCE));
		assertEquals(1, card.getIconCount(Icon.VIRTUAL_SET_0));
		assertEquals(2, card.getIconCount(Icon.WARRIOR));
		assertTrue(card.hasKeyword(Keyword.BOUNTY_HUNTER));
		assertTrue(card.getModelTypes().contains(ModelType.ASSASSIN));
	}

	@Test
	public void CarbonChamberTestingImprisonsLeiaAndStripsCardsWhenSheIsLost() {
		var scn = GetScenario();

		var tower = scn.GetDSCard("tower");

		var ig88 = scn.GetDSCard("ig88");
		var prize = scn.GetDSCard("prize");

		scn.StartGame();

		scn.MoveCardsToLocation(tower, ig88);

		scn.SkipToPhase(Phase.MOVE);

		assertTrue(prize.isCaptive());
		assertTrue(prize.isFrozen());
		assertTrue(prize.isImprisoned());
		assertNull(prize.getEscort());
		assertEquals(tower, prize.getAttachedTo());

		assertTrue(scn.DSActionAvailable("Take imprisoned captive into custody"));
		scn.DSChooseAction("Take imprisoned captive into custody");
		scn.PassAllResponses();

		assertTrue(prize.isCaptive());
		assertTrue(prize.isFrozen());
		assertFalse(prize.isImprisoned());
		assertEquals(ig88, prize.getEscort());
		assertEquals(ig88, prize.getAttachedTo());

		scn.LSPass();
		//IG-88's own text while escorting a captive
		assertTrue(scn.DSActionAvailable("Take card into hand from Force Pile"));
		assertTrue(scn.DSActionAvailable("Deliver captive to prison"));
		assertTrue(scn.DSActionAvailable("Leave frozen captive unattended"));
	}
}

