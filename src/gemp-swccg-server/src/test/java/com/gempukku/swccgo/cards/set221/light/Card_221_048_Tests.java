package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_221_048_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
                    put("assembly", "221_048"); //Assembly Area
                    put("rex", "200_003"); //(clone army)
                    put("fives", "203_002"); //(clone army, spy)
                    put("undercover", "2_040");
                    put("tatooine", "1_127");
				}},
				new HashMap<>()
				{{
				}},
				40,
				40,
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
    public void AssemblyAreaStatsAndKeywordsAreCorrect() {
        /**
         * Title: Assembly Area
         * Uniqueness: Diamond 1
         * Side: Light
         * Type: Location
         * Subtype: Site
         * Destiny: 0
         * Icons: Set 21, Exterior, Planet, Clone Army, Episode 1
         * Game Text: Light: Light: During your move phase, a pair of [Clone Army] characters may move between here and a site you occupy.
         *          Dark: Deploys only to same planet as Clone Command Center. Your droids are power +1 here.
         * Light Force Icons: 2
         * Dark Force Icons: 1
         * Set: Set 21
         * Rarity: V
         */

        var scn = GetScenario();

        var card = scn.GetLSCard("assembly").getBlueprint();

        assertEquals("Assembly Area", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.DIAMOND_1, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.CLONE_ARMY);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.VIRTUAL_SET_21);
            add(Icon.EPISODE_I);
        }});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        assertEquals(ExpansionSet.SET_21,card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    /// Getting Assembly Area on table requires deploying (to associate the diamond site with a system).
    /// However, this requires having Clone Command Center on table (another diamond site which requires an objective
    /// in play before deploying, etc).
    /// Until [Clone Army] objective setup scripts are implemented (or a cheater method to move diamond locations
    /// to table, linked to another location), workaround is to temporarily comment out the "mayNotBePartOfSystem"
    /// Clone Command Center requirement in Assembly Area and confirm tests work...

    //manually tested and passes (see above)
	@Test @Ignore
	public void AssemblyAreaPairMayMoveToOtherSiteTest() {
        //test1: site movement action available to move 2 clone army characters at this site to another site you occupy
        //test2: can select two clone army characters to move
        //test3: two clone army characters completed the move to the occupied site
        var scn = GetScenario();

		var rex = scn.GetLSCard("rex");
        var fives = scn.GetLSCard("fives");
        var assembly = scn.GetLSCard("assembly");
        var trooper = scn.GetLSFiller(1);

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(assembly, rex, fives);
        scn.MoveCardsToLocation(site, trooper);

        scn.SkipToLSTurn(Phase.DEPLOY);

        scn.LSDeployCard(assembly);
        scn.LSChoose("Left");
        scn.PassAllResponses();

        scn.MoveCardsToLocation(assembly, rex, fives);
        scn.SkipToPhase(Phase.MOVE);

        assertTrue(scn.LSCardActionAvailable(assembly, "Move from here to a site you occupy")); //test1
        scn.LSUseCardAction(assembly, "Move from here to a site you occupy");
        assertTrue(scn.LSDecisionAvailable("location to relocate to"));
        scn.LSChooseCard(site);
        assertTrue(scn.LSHasCardChoiceAvailable(rex));
        assertTrue(scn.LSHasCardChoiceAvailable(fives));
        scn.LSChooseCards(rex, fives); //test2

        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(site, trooper, rex, fives)); //test3: successfully moved
        assertTrue(scn.AwaitingDSMovePhaseActions());
    }

    //manually tested and passes
    @Test @Ignore
    public void AssemblyAreaPairWithUndercoverMayMoveToOtherSiteTest() {
        //test1: site movement action available to move 2 clone army characters (1 undercover) at this site to another site you occupy
        //test2: can select two clone army characters (1 undercover) to move
        //test3: two clone army characters (1 undercover) completed the move to the occupied site
        var scn = GetScenario();

        var rex = scn.GetLSCard("rex");
        var fives = scn.GetLSCard("fives");
        var assembly = scn.GetLSCard("assembly");
        var undercover = scn.GetLSCard("undercover");
        var trooper = scn.GetLSFiller(1);

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(assembly, rex, fives, undercover);
        scn.MoveCardsToLocation(site, trooper);

        scn.SkipToLSTurn(Phase.DEPLOY);

        scn.LSDeployCard(assembly);
        scn.LSChoose("Left");
        scn.PassAllResponses();

        scn.MoveCardsToLocation(assembly, rex, fives);

        scn.DSPass();
        scn.LSDeployCard(undercover);
        scn.LSChooseCard(fives);
        scn.PassAllResponses();

        assertTrue(fives.isUndercover());
        scn.SkipToPhase(Phase.MOVE);

        assertTrue(scn.LSCardActionAvailable(assembly, "Move from here to a site you occupy")); //test1
        scn.LSUseCardAction(assembly, "Move from here to a site you occupy");
        assertTrue(scn.LSDecisionAvailable("location to relocate to"));
        scn.LSChooseCard(site);
        assertTrue(scn.LSHasCardChoiceAvailable(rex));
        assertTrue(scn.LSHasCardChoiceAvailable(fives));
        scn.LSChooseCards(rex, fives); //test2

        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(site, trooper, rex, fives)); //test3: successfully moved
        assertTrue(scn.AwaitingDSMovePhaseActions());
    }

}
