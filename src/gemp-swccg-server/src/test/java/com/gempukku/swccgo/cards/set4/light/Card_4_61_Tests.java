package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
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

public class Card_4_61_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("crazy", "4_61");
                    put("corvette", "1_140");
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
        assertEquals(ExpansionSet.DAGOBAH, card.getExpansionSet());
        assertEquals(Rarity.C, card.getRarity());
    }

    @Test
    public void TheydBeCrazyAddsTwoToAsteroidDestinyAgainstTargetTest() {
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

        // Give LS a turn so Force is available, then play during DS control (asteroid destiny timing)
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPass();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPass();

        assertTrue(scn.CardsAtLocation(asteroid, corvette, tie));
        assertTrue(scn.LSCardPlayAvailable(crazy));
        scn.LSPlayCard(crazy);
        assertTrue(scn.LSHasCardChoiceAvailable(tie));
        assertTrue(scn.LSHasCardChoiceAvailable(corvette));
        scn.LSChooseCard(tie);
        scn.PassAllResponses();
        scn.DSPass();

        scn.PrepareLSDestiny(2);
        assertTrue(scn.LSCardActionAvailable(tie, "asteroid"));
        scn.LSUseCardAction(tie, "asteroid");
        scn.PassAllResponses();

        assertEquals(Zone.TOP_OF_LOST_PILE, tie.getZone());
    }

    @Test
    public void TheydBeCrazyNotPlayableWithoutAsteroidOrBlownAwayLocationTest() {
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
    public void TheydBeCrazyWithoutModifierLowDestinyFailsTest() {
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