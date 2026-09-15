package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Issue #970: canceled battle destinies still count toward "just drew more than two".
 */
public class Card_208_020_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("mishap", "208_20");
                }},
                new HashMap<>(),
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
    public void MandalorianMishapVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("mishap").getBlueprint();
        assertEquals("Mandalorian Mishap", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide());
        assertTrue(card.getCardTypes().contains(CardType.INTERRUPT));
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(ExpansionSet.SET_8, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.hasIcon(Icon.JABBAS_PALACE));
        assertTrue(card.hasIcon(Icon.VIRTUAL_SET_8));
    }
}
