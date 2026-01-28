package com.gempukku.swccgo.cards.set110.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_110_003_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
					put("seethree", "110_003");
                    put("seethree_2", "110_003");
					put("c3p0", "1_005"); //premiere
                    put("c3p0_2", "1_005"); //premiere
                    put("agift", "6_052");
                    put("jp", "7_131"); //jabba's palace
                    put("jp_ac", "6_081"); //audience chamber
				}},
				new HashMap<>()
				{{
                    put("stormtrooper", "1_194");
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
	public void SeeThreepioStatsAndKeywordsAreCorrect() {
		/**
		 * Title: See-Threepio
		 * Uniqueness: Unique
		 * Side: Light
		 * Type: Character
		 * Subtype: Droid
		 * Destiny: 2
		 * Deploy: 3
		 * Power: 1
		 * Ability: 0
		 * Forfeit: 4
		 * Icons: Premium
		 * Persona: C-3P0
		 * Game Text: Deploys only to a Jabba's Palace site. Once per game, when replacing another C-3PO,
         *      retrieve 3 Force. When in battle with your other droid and a Rebel, adds one battle destiny.
         *      R2-D2 deploys and moves for free to same location.
		 * Lore: C-3PO was Jabba's 'khan chita,' or translator. Survived more battles than most members of the Alliance.
         *      Wasn't informed of R2-D2's role in the rescue of Han.
		 * Set: Enhanced Jabba's Palace
		 * Rarity: PM
		 */

		var scn = GetScenario();

		var card = scn.GetLSCard("seethree").getBlueprint();

		assertEquals("See-Threepio", card.getTitle());
		assertFalse(card.hasVirtualSuffix());
		assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
		assertEquals(Side.LIGHT, card.getSide());
		assertEquals(2, card.getDestiny(), scn.epsilon);
		assertEquals(3, card.getDeployCost(), scn.epsilon);
		assertEquals(1, card.getPower(), scn.epsilon);
		assertEquals(0, card.getAbility(), scn.epsilon);
		assertEquals(4, card.getForfeit(), scn.epsilon);
		scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
			add(CardType.DROID);
		}});
		scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            //null
		}});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.PROTOCOL);
        }});
		scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.C3PO);
		}});
		scn.BlueprintIconCheck(card, new ArrayList<>() {{
			add(Icon.DROID);
			add(Icon.PREMIUM);
		}});
		assertEquals(ExpansionSet.ENHANCED_JABBAS_PALACE,card.getExpansionSet());
		assertEquals(Rarity.PM,card.getRarity());
	}

    //this test demonstrates bug https://github.com/PlayersCommittee/gemp-swccg-public/issues/894
	@Test @Ignore
	public void SeeThreepioRetrieves3Force() {
        //Test1: persona replace C-3P0 and verify retrieval works
        //Test2: retrieval occurs before persona replacement (CURRENTLY FAILS)
		var scn = GetScenario();

		var seethree = scn.GetLSCard("seethree");
		var c3p0 = scn.GetLSCard("c3p0");
		var jp_ac = scn.GetLSCard("jp_ac");

		scn.StartGame();

		scn.MoveLocationToTable(jp_ac);
        scn.MoveCardsToLocation(jp_ac,c3p0);

        scn.MoveCardsToLSHand(seethree);

        scn.MoveCardsToTopOfOwnLostPile(scn.GetTopOfLSReserveDeck());
        scn.MoveCardsToTopOfOwnLostPile(scn.GetTopOfLSReserveDeck());
        scn.MoveCardsToTopOfOwnLostPile(scn.GetTopOfLSReserveDeck());
        scn.MoveCardsToTopOfOwnLostPile(scn.GetTopOfLSReserveDeck());
        assertEquals(4,scn.GetLSLostPileCount());

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(seethree)); // eligible for persona replacement
        assertFalse(scn.LSDeployAvailable(seethree));

        scn.LSPlayCard(seethree);
        assertTrue(scn.LSHasCardChoiceAvailable(c3p0)); //persona replacement target
        scn.LSChooseCard(c3p0);
        scn.PassAllResponses(); //retrieval responses

        assertTrue(scn.CardsAtLocation(jp_ac,seethree));
        assertFalse(scn.CardsAtLocation(jp_ac,c3p0));
        assertEquals(3,scn.GetLSUsedPileCount()); //Test1: successfully retrieved 3
        assertEquals(2,scn.GetLSLostPileCount()); //4 - 3 + c3p0
        assertTrue(c3p0.getZone() == Zone.LOST_PILE); //Test2: placed in lost pile after retrieval happened
    }

    @Test
    public void SeeThreepioRetrieves3ForceOncePerGame() {
        //Test1: persona replace multiple times and confirm only the first causes retrieval
        //      replacement sequence: c3p0 -> seethree -> c3p0_2 -> seethree_2
        var scn = GetScenario();

        var seethree = scn.GetLSCard("seethree");
        var seethree_2 = scn.GetLSCard("seethree_2");
        var c3p0 = scn.GetLSCard("c3p0");
        var c3p0_2 = scn.GetLSCard("c3p0_2");
        var jp_ac = scn.GetLSCard("jp_ac");

        scn.StartGame();

        scn.MoveLocationToTable(jp_ac);
        scn.MoveCardsToLocation(jp_ac,c3p0);

        scn.MoveCardsToLSHand(seethree, seethree_2, c3p0_2);

        scn.MoveCardsToTopOfOwnLostPile(scn.GetTopOfLSReserveDeck());
        scn.MoveCardsToTopOfOwnLostPile(scn.GetTopOfLSReserveDeck());
        scn.MoveCardsToTopOfOwnLostPile(scn.GetTopOfLSReserveDeck());
        assertEquals(3,scn.GetLSLostPileCount());

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(seethree)); // eligible for persona replacement
        scn.LSPlayCard(seethree);
        assertTrue(scn.LSHasCardChoiceAvailable(c3p0)); //persona replacement target
        scn.LSChooseCard(c3p0);
        scn.PassAllResponses(); //retrieval responses
        assertTrue(scn.CardsAtLocation(jp_ac,seethree));
        assertFalse(scn.CardsAtLocation(jp_ac,c3p0));
        assertEquals(3,scn.GetLSUsedPileCount()); //retrieved 3
        assertEquals(1,scn.GetLSLostPileCount()); //c3p0

        scn.SkipToDSTurn();
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(c3p0_2)); // eligible for persona replacement
        scn.LSPlayCard(c3p0_2);
        assertTrue(scn.LSHasCardChoiceAvailable(seethree)); //persona replacement target
        scn.LSChooseCard(seethree);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(jp_ac,c3p0_2));
        assertFalse(scn.CardsAtLocation(jp_ac,seethree));
        assertEquals(2,scn.GetLSLostPileCount()); //c3p0 + seethree

        scn.SkipToDSTurn();
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfOwnLostPile(scn.GetTopOfLSReserveDeck());
        assertEquals(3,scn.GetLSLostPileCount());
        assertTrue(scn.LSCardPlayAvailable(seethree_2)); // eligible for persona replacement
        scn.LSPlayCard(seethree_2);
        assertTrue(scn.LSHasCardChoiceAvailable(c3p0_2)); //persona replacement target
        scn.LSChooseCard(c3p0_2);
        scn.PassAllResponses();

        assertEquals(0,scn.GetDSUsedPileCount()); //Test1: no retrieved cards
        assertEquals(4,scn.GetLSLostPileCount()); //c3p0 + seethree + 1 + c3p0_2
    }

    //shows this issue is fixed https://github.com/PlayersCommittee/gemp-swccg-public/issues/364
    @Test
    public void SeeThreepioUndercoverRetrieves3Force() {
        //Test1: put C-3P0 undercover (via A Gift) and verify persona replace retrieves 3 force
        var scn = GetScenario();

        var seethree = scn.GetLSCard("seethree");
        var c3p0 = scn.GetLSCard("c3p0");
        var jp = scn.GetLSCard("jp");
        var jp_ac = scn.GetLSCard("jp_ac");
        var agift = scn.GetLSCard("agift");

        scn.StartGame();

        scn.MoveLocationToTable(jp);
        scn.MoveLocationToTable(jp_ac);
        scn.MoveCardsToLocation(jp,c3p0);

        scn.MoveCardsToLSHand(seethree,agift);

            // set up A Gift to make C-3P0 undercover
        scn.SkipToLSTurn(Phase.MOVE);
        assertTrue(scn.GetLSForcePileCount() >= 1); //enough to move
        scn.LSUseCardAction(c3p0); //move
        assertTrue(scn.LSHasCardChoiceAvailable(jp_ac));
        scn.LSChooseCard(jp_ac);
        scn.DSPass(); //Use 1 Force - Optional responses
        scn.LSPass();
        scn.DSPass(); //MOVING_USING_LANDSPEED - Optional responses
        scn.LSPass();
        scn.DSPass(); //MOVED_USING_LANDSPEED - Optional responses
        assertTrue(scn.LSCardPlayAvailable(agift));
        scn.LSPlayCard(agift);
        assertTrue(scn.LSHasCardChoiceAvailable(c3p0)); //target for a gift to deploy on
        scn.LSChooseCard(c3p0);
        scn.PassAllResponses();
        assertTrue(c3p0.isUndercover());
        assertTrue(scn.AwaitingDSMovePhaseActions());
        scn.SkipToDSTurn();

            //same setup as SeeThreepioRetrieves3Force above but with C-3P0 undercover via A Gift
        scn.MoveCardsToTopOfOwnLostPile(scn.GetTopOfLSReserveDeck());
        scn.MoveCardsToTopOfOwnLostPile(scn.GetTopOfLSReserveDeck());
        scn.MoveCardsToTopOfOwnLostPile(scn.GetTopOfLSReserveDeck());
        scn.MoveCardsToTopOfOwnLostPile(scn.GetTopOfLSReserveDeck());
        assertEquals(4,scn.GetLSLostPileCount());

        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardPlayAvailable(seethree)); // eligible for persona replacement
        assertFalse(scn.LSDeployAvailable(seethree));

        scn.LSPlayCard(seethree);
        assertTrue(scn.LSHasCardChoiceAvailable(c3p0)); //persona replacement target
        scn.LSChooseCard(c3p0);

        scn.PassAllResponses(); //replaced persona, retrieval responses
        assertTrue(scn.AwaitingDSDeployPhaseActions());

        assertTrue(scn.CardsAtLocation(jp_ac,seethree));
        assertFalse(scn.CardsAtLocation(jp_ac,c3p0));
        assertTrue(seethree.isUndercover());
        assertEquals(3,scn.GetLSUsedPileCount()); //Test1: successfully retrieved 3
        assertEquals(2,scn.GetLSLostPileCount()); //4 - 3 + c3p0
    }

    //other tests ideas to add:
    //SeeThreepioDeploysOnlyToJP
    //  verify deploys to JP sites
    //  verify cannot deploy to non-JP sites
    //SeeThreepioAddsOneBattleDestiny
    //  verify with other droid and a rebel
    //  verify doesn't work with other droid and non-rebel character
    //  verify doesn't work with non-droid character and a rebel
    //SeeThreepioR2DeploysFree
    //  with seethree inside enclosed vehicle
    //  with seethree at site
    //  with seethree on starship at system
    //SeeThreepioR2MovesFree

}
