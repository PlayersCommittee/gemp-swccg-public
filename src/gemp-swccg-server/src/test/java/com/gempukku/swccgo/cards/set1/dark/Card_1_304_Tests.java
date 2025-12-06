package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.framework.StartingSetup;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1_304_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
				}},
				new HashMap<>()
				{{
					put("tie", "1_304");
					put("star-destroyer", "2_155"); // capital
				}},
				10,
				10,
				StartingSetup.DefaultLSSpaceSystem,
				StartingSetup.DefaultDSSpaceSystem,
				StartingSetup.NoLSStartingInterrupts,
				StartingSetup.NoDSStartingInterrupts,
				StartingSetup.NoLSShields,
				StartingSetup.NoDSShields,
				VirtualTableScenario.Open
		);
	}

	@Test
	public void TIEFighterStatsAndKeywordsAreCorrect() {
		/**
		 * Title: TIE Fighter
		 * Uniqueness: Unrestricted
		 * Side: Dark
		 * Type: Starship
		 * Subtype: Starfighter
		 * Destiny: 1
		 * Deploy: 1
		 * Power: 1
		 * Armor: null
		 * Maneuver: 3
		 * Hyperspeed: null
		 * Forfeit: 2
		 * Icons: Warrior
		 * Game Text: Deploy -1 to same system as any Imperial capital starship.
		 * 		Permanent pilot aboard provides ability of 1.
		 * Lore: TIE or Twin Ion Engine. TIE/ln model is Empire's most common fighter. Quick and maneuverable.
		 * 		Solar-panel wings supplement power generator. Built by Sienar Fleet Systems.
		 * Set: Premiere
		 * Rarity: C2
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("tie").getBlueprint();

		assertEquals("TIE Fighter", card.getTitle());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		assertTrue(card.isCardType(CardType.STARSHIP));
		assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
		assertEquals(1, card.getDestiny(), scn.epsilon);
		assertEquals(1, card.getDeployCost(), scn.epsilon);
		assertEquals(1, card.getPower(), scn.epsilon);
		assertNull(card.getArmor());
		assertEquals(3, card.getManeuver(), scn.epsilon);
		assertNull(card.getHyperspeed());
		assertEquals(2, card.getForfeit(), scn.epsilon);
		assertEquals(1, card.getIconCount(Icon.STARSHIP));
		assertEquals(1, card.getIconCount(Icon.PILOT));
		assertTrue(card.hasKeyword(Keyword.NO_HYPERDRIVE));
		assertTrue(card.getModelTypes().contains(ModelType.TIE_LN));
	}

	@Test
	public void TIEFighterDeploysFor1ForceNormally() {
		var scn = GetScenario();

		var tie = scn.GetDSCard("tie");

		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToDSHand(tie);

		scn.DSActivateMaxForceAndPass();
		scn.PassControlActions();

		assertEquals(Phase.DEPLOY, scn.GetCurrentPhase());
		assertTrue(scn.AwaitingDSDeployPhaseActions());

		assertInHand(tie);
		assertEquals(3, scn.GetDSForcePileCount());

		scn.DSDeployCardAndPassResponses(tie, site);

		assertAtLocation(site, tie);
		assertEquals(2, scn.GetDSForcePileCount());
	}

	@Test
	public void TIEFighterDeploysFor0ForceWithImperialCapitalStarship() {
		var scn = GetScenario();

		var tie = scn.GetDSCard("tie");
		var starDestroyer = scn.GetDSCard("star-destroyer");

		var site = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToDSHand(tie);

		scn.MoveCardsToLocation(site, starDestroyer);
		scn.SkipToPhase(Phase.DEPLOY);

		assertInHand(tie);
		assertAtLocation(site, starDestroyer);
		assertEquals(3, scn.GetDSForcePileCount());

		scn.DSDeployCardAndPassResponses(tie, site);

		assertAtLocation(site, tie);
		assertEquals(3, scn.GetDSForcePileCount());
	}

	@Test
	public void TIEFighterHasAbility1FromPermanentPilot() {
		var scn = GetScenario();

		var tie = scn.GetDSCard("tie");

		assertEquals(1, scn.GetBattleDestinyAbility(tie));
	}
}
