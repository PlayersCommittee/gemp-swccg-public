package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.framework.StartingSetup;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1_028_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("trooper", "1_28");
					put("ackbar", "9_6"); // ability 3
					put("biggs", "1_3"); // ability 2
				}},
				new HashMap<>()
				{{
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

	@Test
	public void RebelTrooperStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Rebel Trooper
		 * Uniqueness: Unrestricted
		 * Side: Light
		 * Type: Character
		 * Subtype: Rebel
		 * Destiny: 1
		 * Deploy: 1
		 * Power: 1
		 * Ability: 1
		 * Forfeit: 2
		 * Icons: Warrior
		 * Keywords: Trooper
		 * Species: Alderaanian
		 * Game Text: Deploys free to same site as one of your Rebels with ability > 2.
		 * Lore: Corellian Corvette trooper Ensign Chad Hilse, an Alderaanian, typifies the loyal Rebel volunteers
		 * 		dedicated to defeating the Empire. Trained in starship and ground combat.
		 * Set: Premiere
		 * Rarity: C3
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("trooper").getBlueprint();

		assertEquals("Rebel Trooper", card.getTitle());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertTrue(card.isCardType(CardType.REBEL));
		//assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
		assertEquals(1, card.getDestiny(), scn.epsilon);
		assertEquals(1, card.getDeployCost(), scn.epsilon);
		assertEquals(1, card.getPower(), scn.epsilon);
		assertEquals(1, card.getAbility(), scn.epsilon);
		assertEquals(2, card.getForfeit(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.REBEL));
		assertEquals(1, card.getIconCount(Icon.WARRIOR));
		assertTrue(card.hasKeyword(Keyword.TROOPER));
		assertEquals(Species.ALDERAANIAN, card.getSpecies());
	}

	@Test
	public void RebelTrooperDeploysFor1ForceNormally() {
		var scn = GetScenario();

		var trooper = scn.GetLSCard("trooper");
		scn.MoveCardsToLSHand(trooper);

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.SkipToLSTurn();

		assertEquals(Phase.ACTIVATE, scn.GetCurrentPhase());
		assertEquals(scn.LS, scn.GetCurrentPlayer());
		assertTrue(scn.AwaitingLSActivatePhaseActions());

		scn.SkipToPhase(Phase.DEPLOY);

		assertInHand(trooper);
		assertEquals(3, scn.GetLSForcePileCount());

		scn.LSDeployCardAndPassResponses(trooper, site);

		assertAtLocation(site, trooper);
		assertEquals(2, scn.GetLSForcePileCount());
	}

	@Test
	public void RebelTrooperDeploysFor1ForceWithAbility2Rebel() {
		var scn = GetScenario();

		var trooper = scn.GetLSCard("trooper");
		var biggs = scn.GetLSCard("biggs");
		scn.MoveCardsToLSHand(trooper, biggs);

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.SkipToLSTurn();

		scn.MoveCardsToLocation(site, biggs);

		assertEquals(Phase.ACTIVATE, scn.GetCurrentPhase());
		assertEquals(scn.LS, scn.GetCurrentPlayer());
		assertTrue(scn.AwaitingLSActivatePhaseActions());

		scn.SkipToPhase(Phase.DEPLOY);

		assertInHand(trooper);
		assertAtLocation(site, biggs);
		assertEquals(2, scn.GetAbility(biggs), scn.epsilon);
		assertEquals(3, scn.GetLSForcePileCount());

		scn.LSDeployCardAndPassResponses(trooper, site);

		assertAtLocation(site, trooper);
		assertEquals(2, scn.GetLSForcePileCount());
	}

	@Test
	public void RebelTrooperDeploysFor0ForceWithAbility3Rebel() {
		var scn = GetScenario();

		var trooper = scn.GetLSCard("trooper");
		var ackbar = scn.GetLSCard("ackbar");
		scn.MoveCardsToLSHand(trooper, ackbar);

		var site = scn.GetLSStartingLocation();

		scn.StartGame();

		scn.SkipToLSTurn();

		scn.MoveCardsToLocation(site, ackbar);

		assertEquals(Phase.ACTIVATE, scn.GetCurrentPhase());
		assertEquals(scn.LS, scn.GetCurrentPlayer());
		assertTrue(scn.AwaitingLSActivatePhaseActions());

		scn.SkipToPhase(Phase.DEPLOY);

		assertInHand(trooper);
		assertAtLocation(site, ackbar);
		assertEquals(3, scn.GetAbility(ackbar), scn.epsilon);
		assertEquals(3, scn.GetLSForcePileCount());

		scn.LSDeployCardAndPassResponses(trooper, site);

		assertAtLocation(site, trooper);
		assertEquals(3, scn.GetLSForcePileCount());
	}
}
