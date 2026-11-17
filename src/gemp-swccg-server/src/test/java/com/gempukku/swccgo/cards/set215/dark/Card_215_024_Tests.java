package com.gempukku.swccgo.cards.set215.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_215_024_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("dannik", "215_024");
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
    public void DannikJerrikoVStatsAndKeywordsAreCorrect() {
        /**
         * Title: Dannik Jerriko (V)
         * Uniqueness: Unique
         * Side: Dark
         * Type: Character
         * Subtype: Alien
         * Destiny: 2
         * Deploy: 3
         * Power: 3
         * Ability: 3
         * Forfeit: 5
         * Icons: Warrior, A New Hope, Virtual Set 15
         * Persona: Dannik
         * Keyword: Assassin
         * Game Text: Opponent's characters just lost from here may not be removed from Lost Pile (except to be placed
         *         out of play). While present at a site and armed with a blaster, adds one battle destiny. Cards hit by
         *         Dannik are power and forfeit -2.
         * Set: Set 15
         * Rarity: V
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("dannik").getBlueprint();

        assertEquals("Dannik Jerriko", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ASSASSIN);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.DANNIK);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.WARRIOR);
            add(Icon.A_NEW_HOPE);
            add(Icon.VIRTUAL_SET_15);
        }});
        assertEquals(ExpansionSet.SET_15, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }
}
