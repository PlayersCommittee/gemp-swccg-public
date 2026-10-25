package com.gempukku.swccgo.cards.set203.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_203_031_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("coruscant", "203_31");
                    put("xizor", "10_45");
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
    public void CoruscantVStatsAndKeywordsAreCorrect() {
        /**
         * Title: Coruscant
         * Uniqueness: Unique
         * Side: Dark
         * Type: Location
         * Subtype: System
         * Dark Force Icons: 2
         * Light Force Icons: 0
         * Icons: Virtual Set 3, Planet
         * Game Text: While you occupy with a Black Sun agent or ISB agent, gains one [Dark Force] icon and one [Light Force] icon.
         *         While you control, gains one [Light Force] icon.
         * Set: Set 3
         * Rarity: V
         */
        var scn = GetScenario();
        var card = scn.GetDSCard("coruscant").getBlueprint();

        assertEquals(Title.Coruscant, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SYSTEM, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VIRTUAL_SET_3);
            add(Icon.DARK_FORCE);
            add(Icon.PLANET);
        }});
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(0, card.getIconCount(Icon.LIGHT_FORCE));
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        assertEquals(ExpansionSet.SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CoruscantVGainsDarkAndLightIconsWhileOccupiedByBlackSunAgent() {
        var scn = GetScenario();

        var coruscant = scn.GetDSCard("coruscant");
        var xizor = scn.GetDSCard("xizor");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveLocationToTable(coruscant);
        scn.MoveCardsToLocation(coruscant, tie);

        assertEquals(2, scn.GetIconCount(coruscant, Icon.DARK_FORCE));
        assertEquals(0, scn.GetIconCount(coruscant, Icon.LIGHT_FORCE));

        scn.BoardAsPilot(tie, xizor);

        assertEquals(3, scn.GetIconCount(coruscant, Icon.DARK_FORCE));
        assertEquals(1, scn.GetIconCount(coruscant, Icon.LIGHT_FORCE));
    }
}
