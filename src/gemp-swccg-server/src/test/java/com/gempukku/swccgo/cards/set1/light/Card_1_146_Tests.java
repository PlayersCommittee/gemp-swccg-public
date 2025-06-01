package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class Card_1_146_Tests {
	protected VirtualTableScenario GetScenario() throws DecisionResultInvalidException {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("xwing", "1_146");
				}},
				new HashMap<>()
				{{
				}},
				10,
				10,
				VirtualTableScenario.DefaultSpaceLSSystem,
				VirtualTableScenario.DefaultSpaceDSSystem,
				VirtualTableScenario.NoLSStarters,
				VirtualTableScenario.NoDSStarters,
				VirtualTableScenario.NoLSShields,
				VirtualTableScenario.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void XWingStatsAndKeywordsAreCorrect() throws DecisionResultInvalidException {
		/**
		 * Title: X-wing
		 * Uniqueness: Unrestricted
		 * Side: Light
		 * Type: Starship
		 * Subtype: Starfighter
		 * Destiny: 2
		 * Deploy: 2
		 * Power: 3
		 * Armor: null
		 * Maneuver: 4
		 * Hyperspeed: 5
		 * Forfeit: 4
		 * Icons: Pilot, Nav Computer, SCOMP Link
		 * Model: X-wing
		 * Game Text: Permanent pilot aboard provides ability of 1.
		 * Lore: Model T-65 by Incom Corporation. Delivered to Alliance by defecting design team. 12.5 meters long.
		 * 		Wings deploy in an 'X' position for better weapons coverage.
		 * Set: Premiere
		 * Rarity: C2
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("xwing").getBlueprint();

		assertEquals("X-wing", card.getTitle());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertTrue(card.isCardType(CardType.STARSHIP));
		assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
		assertEquals(2, card.getDestiny(), scn.epsilon);
		assertEquals(2, card.getDeployCost(), scn.epsilon);
		assertEquals(3, card.getPower(), scn.epsilon);
		assertNull(card.getArmor());
		assertEquals(4, card.getManeuver(), scn.epsilon);
		assertEquals(5, card.getHyperspeed(), scn.epsilon);
		assertEquals(4, card.getForfeit(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.STARSHIP));
		assertEquals(1, card.getIconCount(Icon.PILOT));
		assertEquals(1, card.getIconCount(Icon.NAV_COMPUTER));
		assertEquals(1, card.getIconCount(Icon.SCOMP_LINK));
		assertTrue(card.getModelTypes().contains(ModelType.X_WING));
	}


	@Test
	public void XWingHasAbility1FromPermanentPilot() throws DecisionResultInvalidException {
		var scn = GetScenario();

		var xwing = scn.GetLSCard("xwing");

		assertEquals(1, scn.GetBattleDestinyAbility(xwing), scn.epsilon);
	}
}
