package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_6_170_Tests {
	protected VirtualTableScenario GetScenario() {
		return new VirtualTableScenario(
				new HashMap<>()
				{{
				}},
				new HashMap<>()
				{{
                    put("greatpit", "6_170"); //Tatooine: Great Pit Of Carkoon
                    put("sarlacc", "107_005");
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
    public void TatooineGreatPitOfCarkoonStatsAndKeywordsAreCorrect() {
        /**
         * Title: Great Pit Of Carkoon
         * Uniqueness: Unique
         * Side: Dark
         * Type: Location
         * Subtype: Site
         * Destiny: 0
         * Icons: Jabba's Palace, Exterior, Planet
         * Game Text: Dark: During your control phase, may cause Sarlacc to immediately attack one captive present.
         *          Light:
         * Dark Force Icons: 1
         * Dark Force Icons: 1
         * Set: Jabba's Palace
         * Rarity: U
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("greatpit").getBlueprint();

        assertEquals(Title.Great_Pit_Of_Carkoon, card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.JABBAS_PALACE);
            add(Icon.PLANET);
            add(Icon.EXTERIOR_SITE);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
        }});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.PIT);
        }});
        assertEquals(ExpansionSet.JABBAS_PALACE, card.getExpansionSet());
        assertEquals(Rarity.U, card.getRarity());
    }


	@Test
	public void TatooineGreatPitOfCarkoonSarlaccDeploysHere() {
        //test1: pit is a valid target to deploy sarlacc to
        //test2: sarlacc successfully deploys to pit and stays on table (within habitat)
		var scn = GetScenario();

        var greatpit = scn.GetDSCard("greatpit");
        var sarlacc = scn.GetDSCard("sarlacc");

		scn.StartGame();

        scn.MoveLocationToTable(greatpit);
        scn.MoveCardsToDSHand(sarlacc);

        scn.DSActivateForceCheat(1);

        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.GetDSForcePileCount() >= 4);
        scn.DSDeployCard(sarlacc);
        scn.DSChooseCard(greatpit); //test1
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.CardsAtLocation(greatpit, sarlacc)); //test2
	}

    @Test
    public void TatooineGreatPitOfCarkoonSarlaccCanAttackCaptiveDuringControlPhase() {
        //test1: can use game text to initiate an attack during control phase
        //test2: can target captive
        //test3: cannot target opponent's non-captive character
        //test4: cannot target own non-captive character
        //test5: attack completes (sarlacc eats captive)

        var scn = GetScenario();

        var trooper1 = scn.GetLSFiller(1);
        var trooper2 = scn.GetLSFiller(2);

        var stormtrooper = scn.GetDSFiller(1);
        var greatpit = scn.GetDSCard("greatpit");
        var sarlacc = scn.GetDSCard("sarlacc");

        scn.StartGame();

        scn.MoveLocationToTable(greatpit);
        scn.MoveCardsToLocation(greatpit, sarlacc, trooper1, trooper2, stormtrooper);

        scn.CaptureCardWith(stormtrooper, trooper1);

        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(scn.CardsAtLocation(greatpit, sarlacc, trooper2, stormtrooper));
        assertFalse(scn.CardsAtLocation(greatpit, trooper1)); //attached as captive
        assertTrue(scn.DSCardActionAvailable(greatpit, "attack captive")); //test1
        scn.DSUseCardAction(greatpit, "attack captive");

        assertTrue(scn.DSDecisionAvailable("Choose captive"));
        assertTrue(scn.DSHasCardChoiceAvailable(trooper1)); //test2
        assertFalse(scn.DSHasCardChoiceAvailable(trooper2)); //test3
        assertFalse(scn.DSHasCardChoiceAvailable(stormtrooper)); //test4
        scn.DSChooseCard(trooper1);

        scn.PassAllResponses();
        scn.DSPass();
        scn.LSPass();
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.CardsAtLocation(greatpit, sarlacc, trooper2, stormtrooper));
        assertEquals(Zone.OUT_OF_PLAY, trooper1.getZone()); //test5: (targets Sarlacc eats go out of play)
    }

    @Test
    public void TatooineGreatPitOfCarkoonSarlaccCanAttackCaptiveOncePerControlPhase() {
        //test1: cannot use game text twice during control phase (even with valid targets)

        var scn = GetScenario();

        var trooper1 = scn.GetLSFiller(1);
        var trooper2 = scn.GetLSFiller(2);

        var stormtrooper = scn.GetDSFiller(1);
        var greatpit = scn.GetDSCard("greatpit");
        var sarlacc = scn.GetDSCard("sarlacc");

        scn.StartGame();

        scn.MoveLocationToTable(greatpit);
        scn.MoveCardsToLocation(greatpit, sarlacc, trooper1, trooper2, stormtrooper);

        scn.CaptureCardWith(stormtrooper, trooper1);
        scn.CaptureCardWith(stormtrooper, trooper2);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSUseCardAction(greatpit, "attack captive");

        assertTrue(scn.DSDecisionAvailable("Choose captive"));
        assertTrue(scn.DSHasCardChoiceAvailable(trooper1));
        assertTrue(scn.DSHasCardChoiceAvailable(trooper2));
        scn.DSChooseCard(trooper1);

        scn.PassAllResponses();
        scn.DSPass();
        scn.LSPass();
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSControlPhaseActions());
        scn.LSPass();

        assertFalse(scn.DSCardActionAvailable(greatpit, "attack captive")); //test1
    }

    @Test
    public void TatooineGreatPitOfCarkoonSarlaccCanAttackDuringBattlePhaseAfterUsingAttackInControlPhase() {
        //shows https://github.com/PlayersCommittee/gemp-swccg-public/issues/302 resolved
        //attacks outside of battle phase do not count toward required attacks during battle phase

        //test1: can attack with sarlacc in battle phase after attacking with pit's control phase gametext

        var scn = GetScenario();

        var trooper1 = scn.GetLSFiller(1);
        var trooper2 = scn.GetLSFiller(2);

        var stormtrooper = scn.GetDSFiller(1);
        var greatpit = scn.GetDSCard("greatpit");
        var sarlacc = scn.GetDSCard("sarlacc");

        scn.StartGame();

        scn.MoveLocationToTable(greatpit);
        scn.MoveCardsToLocation(greatpit, sarlacc, trooper1, trooper2, stormtrooper);

        scn.CaptureCardWith(stormtrooper, trooper1);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSUseCardAction(greatpit, "attack captive");
        scn.DSChooseCard(trooper1);

        scn.PassAllResponses();
        scn.DSPass();
        scn.LSPass();
        scn.PassAllResponses();

        assertEquals(Zone.OUT_OF_PLAY, trooper1.getZone());

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCardActionAvailable(sarlacc, "attack"));
    }


}
