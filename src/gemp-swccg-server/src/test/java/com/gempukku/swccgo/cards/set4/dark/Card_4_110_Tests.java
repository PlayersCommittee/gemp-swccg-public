package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_4_110_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("xwing","1_146");
                }},
				new HashMap<>()
				{{
                    put("mynock", "4_110");
                    //put("mynock2", "4_110");
                    put("warRoom","1_287"); //death star: war room
					put("vcsd", "2_155"); //power 6 starship
					put("dread", "106_013"); //power 5 starship
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
	public void MynockStatsAndKeywordsAreCorrect() {
		/**
		 * Title: Mynock
		 * Uniqueness: Unrestricted
		 * Side: Dark
		 * Type: Creature
         * Subtype: Space
		 * Destiny: 3
		 * Icons: Dagobah, Creature, Selective
		 * Game Text: Habitat: unlimited. Parasite: Starfighter. Host's power and hyperspeed are cumulatively -2;
         *          while both < 1, Mynocks randomly detach one at a time (cannot attach for remainder of turn).
         *          Moves like a starfighter.
		 * Lore: Silicon-based space borne lifeform. Frequently called a 'power sucker.' Feeds on energy such as
         *      stellar radiation and electrical discharges. Absorbs minerals from starship hulls.
		 * Set: Dagobah
		 * Rarity: C
		 */

		var scn = GetScenario();

		var card = scn.GetDSCard("mynock").getBlueprint();

		assertEquals("Mynock", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
		assertEquals(Side.DARK, card.getSide());
		assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getFerocity(), scn.epsilon);
        assertEquals(3, card.getSpecialDefenseValue(), scn.epsilon);
        assertEquals(0, card.getForfeit(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.CREATURE);
		}});
        assertEquals(null, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.SPACE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.PARASITE);
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.DAGOBAH);
			add(Icon.CREATURE);
            add(Icon.SELECTIVE_CREATURE);
		}});
		assertEquals(ExpansionSet.DAGOBAH,card.getExpansionSet());
		assertEquals(Rarity.C,card.getRarity());
	}

	@Test
	public void MynockDeployCostTest() {
        //Test1: deploys to a system
        //Test2: deploys to a site (interior, mobile)
        //Test3: deploys for 3 force
		var scn = GetScenario();

        var mynock = scn.GetDSCard("mynock");
        var warRoom = scn.GetDSCard("warRoom");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveLocationToTable(warRoom);

        scn.MoveCardsToDSHand(mynock);

        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(3,scn.GetDSForcePileCount());
        assertTrue(scn.DSDeployAvailable(mynock));
        scn.DSDeployCard(mynock);

        assertTrue(scn.DSHasCardChoiceAvailable(system)); //Test1: deploys on a system
        assertTrue(scn.DSHasCardChoiceAvailable(warRoom)); //Test2: deploys on a site
        assertEquals(0,scn.GetDSUsedPileCount());
        scn.DSChooseCard(warRoom);
        scn.PassAllResponses();

        assertEquals(3,scn.GetDSUsedPileCount()); //Test3: paid 3 to deploy
        assertTrue(scn.CardsAtLocation(warRoom, mynock)); //
    }

	@Test
	public void MynockUnattachedDefenseTotalEqualToAttackTotalTest() {
		//Test1: attacker total (5) against mynock defense total (5) at system does not defeat mynock
		var scn = GetScenario();

		var mynock = scn.GetDSCard("mynock");
		var dread = scn.GetDSCard("dread");
		var system = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(system, mynock, dread);

		scn.SkipToPhase(Phase.BATTLE);
		assertEquals(3,scn.GetDSForcePileCount()); //enough to attack
		assertTrue(scn.DSCardActionAvailable(system, "attack"));
		scn.DSUseCardAction(system, "attack");
		scn.PassAllResponses();
		scn.PassWeaponsSegmentActions();
		scn.PassAllResponses();

		assertTrue(scn.AwaitingLSBattlePhaseActions());
		assertTrue(scn.CardsAtLocation(system, mynock)); //Test1
	}

	@Test
	public void MynockUnattachedDefenseTotalLessThanAttackTotalTest() {
		//Test1: attacker total (6) against mynock defense total (5) at system defeats mynock
		var scn = GetScenario();

		var mynock = scn.GetDSCard("mynock");
		var vcsd = scn.GetDSCard("vcsd");
		var system = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(system, mynock, vcsd);

		scn.SkipToPhase(Phase.BATTLE);
		assertEquals(3,scn.GetDSForcePileCount()); //enough to attack
		assertTrue(scn.DSCardActionAvailable(system, "attack"));
		scn.DSUseCardAction(system, "attack");
		scn.PassAllResponses();
		scn.PassWeaponsSegmentActions();
		scn.PassAllResponses();

		assertTrue(scn.AwaitingLSBattlePhaseActions());
		assertFalse(scn.CardsAtLocation(system, mynock)); //Test1
		assertEquals(1,scn.GetDSLostPileCount());
	}

	@Test
	public void MynockAttachedDefenseTotalEqualToAttackTotalTest() {
		//Test1: attacker total (5) against mynock defense total (5) at system does not defeat mynock
		var scn = GetScenario();

		var xwing = scn.GetLSCard("xwing");

		var mynock = scn.GetDSCard("mynock");
		var dread = scn.GetDSCard("dread");
		var system = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(system, mynock, dread, xwing);

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSUseCardAction(mynock);
		scn.PassAllResponses();
		scn.PassWeaponsSegmentActions();
		scn.PassAllResponses();
		assertTrue(scn.IsAttachedTo(xwing, mynock));

		assertTrue(scn.AwaitingLSBattlePhaseActions());
		scn.LSPass();

		assertEquals(3,scn.GetDSForcePileCount()); //enough to attack
		assertTrue(scn.DSCardActionAvailable(system, "attack"));
		scn.DSUseCardAction(system, "attack");
		scn.PassAllResponses();
		scn.PassWeaponsSegmentActions();
		scn.PassAllResponses();

		assertTrue(scn.AwaitingLSBattlePhaseActions());
		assertTrue(scn.IsAttachedTo(xwing, mynock));
	}

	@Test
	public void MynockAttachedDefenseTotalLessThanAttackTotalTest() {
		//Test1: attacker total (6) against mynock defense total (5) at system defeats mynock
		var scn = GetScenario();

		var xwing = scn.GetLSCard("xwing");

		var mynock = scn.GetDSCard("mynock");
		var vcsd = scn.GetDSCard("vcsd");
		var system = scn.GetDSStartingLocation();

		scn.StartGame();

		scn.MoveCardsToLocation(system, mynock, vcsd, xwing);

		scn.SkipToPhase(Phase.BATTLE);
		scn.DSUseCardAction(mynock);
		scn.PassAllResponses();
		scn.PassWeaponsSegmentActions();
		scn.PassAllResponses();
		assertTrue(scn.IsAttachedTo(xwing, mynock));

		assertTrue(scn.AwaitingLSBattlePhaseActions());
		scn.LSPass();

		assertEquals(3,scn.GetDSForcePileCount()); //enough to attack
		assertTrue(scn.DSCardActionAvailable(system, "attack"));
		scn.DSUseCardAction(system, "attack");
		scn.PassAllResponses();
		scn.PassWeaponsSegmentActions();
		scn.PassAllResponses();

		assertTrue(scn.AwaitingLSBattlePhaseActions());
		assertFalse(scn.IsAttachedTo(xwing, mynock));
		assertEquals(1,scn.GetDSLostPileCount());
	}
}
