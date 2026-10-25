package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

public class Card_13_045_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("woafm", "13_045");
                }},
                new HashMap<>() {{
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
    public void WeaponOfAFallenMentorStatsAndKeywordsAreCorrect() {
        /**
         * Title: Weapon Of A Fallen Mentor
         * Uniqueness: Unique
         * Side: Light
         * Type: Effect
         * Subtype: Immediate
         * Destiny: 6
         * Icons: Reflections III, Episode I
         * Game Text: If your non-[Permanent Weapon] lightsaber was just lost (or stolen) from a site, deploy on your
         *         character of ability > 4 at same site. Relocate that lightsaber to this character, who may use that
         *         lightsaber, and it may not be stolen. (Immune to Control.)
         * Set: Reflections III
         * Rarity: PM
         */

        var scn = GetScenario();

        var card = scn.GetLSCard("woafm").getBlueprint();

        assertEquals("Weapon Of A Fallen Mentor", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.IMMEDIATE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.REFLECTIONS_III);
            add(Icon.EPISODE_I);
        }});
        assertEquals(ExpansionSet.REFLECTIONS_III, card.getExpansionSet());
        assertEquals(Rarity.PM, card.getRarity());
        assertTrue(card.isImmuneToCardTitle("Control"));
    }
}
