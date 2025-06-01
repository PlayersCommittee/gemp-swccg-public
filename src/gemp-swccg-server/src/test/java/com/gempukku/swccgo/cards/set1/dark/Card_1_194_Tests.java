package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.*;

public class Card_1_194_Tests
{
	protected VirtualTableScenario GetScenario() throws DecisionResultInvalidException {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
				}},
				new HashMap<>()
				{{
					put("stormtrooper", "1_194");
					put("motti", "1_164"); // ability 3
					put("chiraneau", "9_097"); //ability 2
				}},
				10,
				10,
				VirtualTableScenario.DefaultGroundLSLocation,
				VirtualTableScenario.DefaultGroundDSLocation,
				VirtualTableScenario.NoLSStarters,
				VirtualTableScenario.NoDSStarters,
				VirtualTableScenario.NoLSShields,
				VirtualTableScenario.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void StormtrooperStatsAndKeywordsAreCorrect() throws DecisionResultInvalidException {
		/**
		 * Title: Stormtrooper
		 * Uniqueness: Unrestricted
		 * Side: Dark
		 * Type: Character
		 * Subtype: Imperial
		 * Destiny: 1
		 * Deploy: 1
		 * Power: 1
		 * Ability: 1
		 * Forfeit: 2
		 * Icons: Warrior
		 * Game Text: Deploys free to same site as one of your Imperials with ability > 2.
		 * Lore: One of the countless elite shock troops totally loyal to the Emperor. Unquestioningly follows orders.
		 * 		Willing to sacrifice their lives to accomplish a mission. First-strike force.
		 * Set: Premiere
		 * Rarity: C3
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("stormtrooper").getBlueprint();

		assertEquals("Stormtrooper", card.getTitle());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		assertTrue(card.isCardType(CardType.IMPERIAL));
		//assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
		assertEquals(1, card.getDestiny(), scn.epsilon);
		assertEquals(1, card.getDeployCost(), scn.epsilon);
		assertEquals(1, card.getPower(), scn.epsilon);
		assertEquals(1, card.getAbility(), scn.epsilon);
		assertEquals(2, card.getForfeit(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.IMPERIAL));
		assertEquals(1, card.getIconCount(Icon.WARRIOR));
		assertTrue(card.hasKeyword(Keyword.STORMTROOPER));
	}

	@Test
	public void StormtrooperDeploysFor1ForceNormally() throws DecisionResultInvalidException {
		var scn = GetScenario();

		var stormtrooper = scn.GetDSCard("stormtrooper");
		scn.MoveCardsToDSHand(stormtrooper);

		var site = scn.GetDSStartingLocation();

		scn.StartGame();
		scn.DSActivateMaxForceAndPass();
		scn.PassControlActions();

		assertEquals(Phase.DEPLOY, scn.GetCurrentPhase());
		assertTrue(scn.DSDecisionAvailable("Choose Deploy action or Pass"));

		assertInHand(stormtrooper);
		assertEquals(3, scn.GetDSForcePileCount());

		scn.DSDeployCardAndPassResponses(stormtrooper, site);

		assertAtLocation(site, stormtrooper);
		assertEquals(2, scn.GetDSForcePileCount());
	}

	@Test
	public void StormtrooperDeploysFor1ForceWithAbility2Imperial() throws DecisionResultInvalidException {
		var scn = GetScenario();

		var stormtrooper = scn.GetDSCard("stormtrooper");
		var chiraneau = scn.GetDSCard("chiraneau");
		scn.MoveCardsToDSHand(stormtrooper, chiraneau);

		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, chiraneau);
		scn.SkipToPhase(Phase.DEPLOY);

		assertInHand(stormtrooper);
		assertAtLocation(site, chiraneau);
		assertEquals(2, scn.GetAbility(chiraneau), scn.epsilon);
		assertEquals(3, scn.GetDSForcePileCount());

		scn.DSDeployCardAndPassResponses(stormtrooper, site);

		assertAtLocation(site, stormtrooper);
		assertEquals(2, scn.GetDSForcePileCount());
	}

	@Test
	public void StormtrooperDeploysFor0ForceWithAbility3Imperial() throws DecisionResultInvalidException {
		var scn = GetScenario();

		var stormtrooper = scn.GetDSCard("stormtrooper");
		var motti = scn.GetDSCard("motti");
		scn.MoveCardsToDSHand(stormtrooper, motti);

		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(site, motti);
		scn.SkipToPhase(Phase.DEPLOY);

		assertInHand(stormtrooper);
		assertAtLocation(site, motti);
		assertEquals(3, scn.GetAbility(motti), scn.epsilon);
		assertEquals(3, scn.GetDSForcePileCount());

		scn.DSDeployCardAndPassResponses(stormtrooper, site);

		assertAtLocation(site, stormtrooper);
		assertEquals(3, scn.GetDSForcePileCount());
	}
}

