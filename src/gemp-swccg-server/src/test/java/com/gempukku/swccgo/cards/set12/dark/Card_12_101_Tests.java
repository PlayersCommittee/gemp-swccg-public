package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.common.Phase;
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
     * Darth Maul, Young Apprentice (12_101) is unique. His different modifier types must both apply.
     * His Jedi Master power -3 uses the same until-end-of-battle path as Concentrate All Fire (9_003).
     * Maul's Double-Bladed Lightsaber (13_75) can hit the same Jedi Master twice in one battle.
     */
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("depa", "12_4");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("maul", "12_101");
                    put("saber", "13_75");
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
     * Darth Maul, Young Apprentice (12_101) is immune to attrition less than 5 and immune to Clash Of Sabers.
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

    /**
     * Maul's Double-Bladed Lightsaber (13_75) may fire twice per battle. If both hits land on the same
     * Jedi Master (Depa Billaba (12_4)), that Master is power -3 once, not -6. The until-end-of-battle
     * POWER clause is the same unique source, so the Cumulative Rule makes the second -3 conflict.
     */
    @Test
    public void DarthMaulYoungApprenticeJediMasterPowerLossIsNotCumulativeFromTwoHitsInOneBattle() {
        var scn = GetScenario();

        var maul = scn.GetDSCard("maul");
        var saber = scn.GetDSCard("saber");
        var depa = scn.GetLSCard("depa");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, maul, depa);
        scn.AttachCardsTo(maul, saber);

        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.AwaitingDSBattlePhaseActions());
        assertTrue(scn.GetDSForcePileCount() >= 1);
        assertTrue(scn.DSCanInitiateBattle());
        scn.DSInitiateBattle(site);

        // Depa Billaba (12_4) printed power 4
        assertEquals(4, scn.GetPower(depa));

        scn.PrepareDSDestiny(7);
        scn.PrepareDSDestiny(6);
        assertTrue(scn.DSCardActionAvailable(saber));
        scn.DSUseCardAction(saber);
        assertTrue(scn.DSHasCardChoiceAvailable(depa));
        scn.DSChooseCard(depa);
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("Reduce")) {
            scn.DSDecided(0);
            scn.PassAllResponses();
        }

        // First hit: printed 4 plus -3
        assertEquals(1, scn.GetPower(depa));

        scn.LSPass();

        scn.PrepareDSDestiny(5);
        scn.PrepareDSDestiny(4);
        assertTrue(scn.DSCardActionAvailable(saber));
        scn.DSUseCardAction(saber);
        assertTrue(scn.DSHasCardChoiceAvailable(depa));
        scn.DSChooseCard(depa);
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("Reduce")) {
            scn.DSDecided(0);
            scn.PassAllResponses();
        }

        // Same unique Maul POWER clause must not stack a second -3
        assertEquals(1, scn.GetPower(depa));
    }
}