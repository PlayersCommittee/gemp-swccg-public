package com.gempukku.swccgo.cards.set4.light;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class Card_4_65_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("squeeze", "4_65");
                    put("xwing", "1_146");
                    put("kessel", "1_126");
                    put("asteroid", "4_081");
                }},
                new HashMap<>() {{
                    put("tie1", "1_304");
                    put("tie2", "9_175");
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
    public void TightSqueezeStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("squeeze").getBlueprint();

        assertEquals("Tight Squeeze", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.DAGOBAH);
        }});
        assertEquals(ExpansionSet.DAGOBAH, card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
    }

    @Test
    public void TightSqueezeNotPlayableWithOnlyOneOpposingStarfighter() {
        var scn = GetScenario();

        var squeeze = scn.GetLSCard("squeeze");
        var xwing = scn.GetLSCard("xwing");
        var kessel = scn.GetLSCard("kessel");
        var asteroid = scn.GetLSCard("asteroid");
        var tie1 = scn.GetDSCard("tie1");

        scn.StartGame();
        scn.MoveCardsToLSHand(squeeze);
        scn.MoveLocationToTable(kessel);
        scn.MoveLocationToTable(asteroid);
        scn.MoveCardsToLocation(asteroid, xwing, tie1);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(asteroid);
        scn.PassAllResponses();

        assertFalse(scn.LSCardPlayAvailable(squeeze));
    }
}
