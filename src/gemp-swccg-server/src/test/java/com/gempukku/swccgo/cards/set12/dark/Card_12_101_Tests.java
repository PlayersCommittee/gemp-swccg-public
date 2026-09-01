package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_12_101_Tests {
    /**
     * Darth Maul, Young Apprentice is unique. His different modifier types must both apply.
     * His Jedi Master power -3 uses the same until-end-of-battle path as Concentrate All Fire.
     */
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("depa", "12_4");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("maul", "12_101");
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
    public void DarthMaulYoungApprenticeStatsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("maul").getBlueprint();
        assertEquals("Darth Maul, Young Apprentice", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
    }

    /**
     * A unique card can still apply two different modifier types at once.
     * Maul is immune to attrition less than 5 and immune to Clash Of Sabers.
     */
    @Test
    public void DarthMaulYoungApprenticeAppliesTwoDifferentModifierTypesTogether() {
        var scn = GetScenario();

        var maul = scn.GetDSCard("maul");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, maul);

        float immunity = scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), maul);
        assertEquals(5, immunity, scn.epsilon);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), maul, Title.Clash_Of_Sabers));
    }
}
