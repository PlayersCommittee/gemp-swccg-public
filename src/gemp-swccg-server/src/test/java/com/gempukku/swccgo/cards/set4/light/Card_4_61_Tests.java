package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_4_61_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("crazy", "4_61");
                    put("corvette", "1_140");
                    put("xwing", "1_146");
                    put("cannon", "7_162");
                    put("kessel", "1_126");
                    put("asteroid", "4_081");
                }},
                new HashMap<>() {{
                    put("tie", "1_304");
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

    private boolean AddTwoAvailable(VirtualTableScenario scn, PhysicalCardImpl crazy) {
        return scn.LSGetDecision() != null && scn.LSCardActionAvailable(crazy, "Add 2");
    }

    private void PassUntilAddTwoAvailable(VirtualTableScenario scn, PhysicalCardImpl crazy) {
        for (int i = 0; i < 20; i++) {
            if (AddTwoAvailable(scn, crazy)) {
                return;
            }
            if (scn.DSGetDecision() != null
                    && scn.DSGetDecision().getText() != null
                    && scn.DSGetDecision().getText().toLowerCase().contains("optional response")) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null
                    && scn.LSGetDecision().getText() != null
                    && scn.LSGetDecision().getText().toLowerCase().contains("optional response")) {
                scn.LSPass();
                continue;
            }
            return;
        }
    }

    private void PlayTheydBeCrazyOnTieDuringDsControl(VirtualTableScenario scn) {
        var crazy = scn.GetLSCard("crazy");
        var corvette = scn.GetLSCard("corvette");
        var kessel = scn.GetLSCard("kessel");
        var asteroid = scn.GetLSCard("asteroid");
        var tie = scn.GetDSCard("tie");

        scn.MoveCardsToLSHand(crazy);
        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(asteroid);
        scn.MoveCardsToLocation(asteroid, corvette, tie);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPass();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPass();

        assertTrue(scn.LSCardPlayAvailable(crazy));
        scn.LSPlayCard(crazy);
        assertTrue(scn.LSHasCardChoiceAvailable(tie));
        scn.LSChooseCard(tie);
        scn.PassAllResponses();
        scn.DSPass();
    }

    @Test
    public void TheydBeCrazyToFollowUsStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("crazy").getBlueprint();

        assertEquals("They'd Be Crazy To Follow Us", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.DAGOBAH);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        assertEquals(ExpansionSet.DAGOBAH, card.getExpansionSet());
        assertEquals(Rarity.C, card.getRarity());
    }

    @Test
    public void TheydBeCrazyToFollowUsMayAddTwoToAsteroidDestinyAgainstTarget() {
        var scn = GetScenario();
        var crazy = scn.GetLSCard("crazy");
        var asteroid = scn.GetLSCard("asteroid");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        PlayTheydBeCrazyOnTieDuringDsControl(scn);

        scn.PrepareLSDestiny(2);
        assertTrue(scn.LSCardActionAvailable(tie, "asteroid"));
        scn.LSUseCardAction(tie, "asteroid");
        PassUntilAddTwoAvailable(scn, crazy);
        assertTrue(AddTwoAvailable(scn, crazy));
        scn.LSUseCardAction(crazy, "Add 2");
        scn.PassAllResponses();

        assertEquals(Zone.TOP_OF_LOST_PILE, tie.getZone());
        assertFalse(scn.CardsAtLocation(asteroid, tie));
    }

    @Test
    public void TheydBeCrazyToFollowUsAsteroidDestinyAddTwoIsOptional() {
        var scn = GetScenario();
        var crazy = scn.GetLSCard("crazy");
        var asteroid = scn.GetLSCard("asteroid");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        PlayTheydBeCrazyOnTieDuringDsControl(scn);

        scn.PrepareLSDestiny(2);
        scn.LSUseCardAction(tie, "asteroid");
        PassUntilAddTwoAvailable(scn, crazy);
        assertTrue(AddTwoAvailable(scn, crazy));
        scn.LSPass();
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(asteroid, tie));
        assertEquals(0, scn.GetDSLostPileCount());
    }

    @Test
    public void TheydBeCrazyToFollowUsMayAddTwoToWeaponDestinyTargetingManeuver() {
        var scn = GetScenario();
        var crazy = scn.GetLSCard("crazy");
        var xwing = scn.GetLSCard("xwing");
        var cannon = scn.GetLSCard("cannon");
        var kessel = scn.GetLSCard("kessel");
        var asteroid = scn.GetLSCard("asteroid");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();

        scn.MoveCardsToLSHand(crazy);
        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(asteroid);
        scn.MoveCardsToLocation(asteroid, xwing, tie);
        scn.AttachCardsTo(xwing, cannon);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSActivateForceCheat(3);
        assertTrue(scn.LSCardPlayAvailable(crazy));
        scn.LSPlayCard(crazy);
        scn.LSChooseCard(tie);
        scn.PassAllResponses();

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.LSCanInitiateBattle());
        scn.LSInitiateBattle(asteroid);
        scn.PassBattleStartResponses();

        scn.PrepareLSDestiny(2);
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(cannon));
        scn.LSUseCardAction(cannon);
        scn.LSChooseCard(tie);
        if (scn.LSDecisionAvailable("Choose number for X")) {
            scn.LSDecided(0);
        }
        PassUntilAddTwoAvailable(scn, crazy);
        assertTrue(AddTwoAvailable(scn, crazy));
        scn.LSUseCardAction(crazy, "Add 2");
        scn.PassAllResponses();

        assertTrue(tie.isHit());
    }

    @Test
    public void TheydBeCrazyToFollowUsNotPlayableWithoutAsteroidOrBlownAwayLocation() {
        var scn = GetScenario();

        var crazy = scn.GetLSCard("crazy");
        var corvette = scn.GetLSCard("corvette");
        var kessel = scn.GetLSCard("kessel");
        var asteroid = scn.GetLSCard("asteroid");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();

        scn.MoveCardsToLSHand(crazy);
        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(asteroid);
        scn.MoveCardsToLocation(kessel, corvette, tie);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPass();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPass();

        assertFalse(scn.LSCardPlayAvailable(crazy));
    }

    @Test
    public void TheydBeCrazyToFollowUsAsteroidDestinyTwoDoesNotDestroyTieWithoutTheInterrupt() {
        var scn = GetScenario();

        var crazy = scn.GetLSCard("crazy");
        var corvette = scn.GetLSCard("corvette");
        var kessel = scn.GetLSCard("kessel");
        var asteroid = scn.GetLSCard("asteroid");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();

        scn.MoveCardsToLSHand(crazy);
        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(asteroid);
        scn.MoveCardsToLocation(asteroid, corvette, tie);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        scn.PrepareLSDestiny(2);
        scn.LSUseCardAction(tie, "asteroid");
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(asteroid, tie));
        assertEquals(0, scn.GetDSLostPileCount());
    }
}
