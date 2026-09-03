package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_3_099_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("han", "1_011"); // Premiere Han Solo (smuggler)
                    put("farm", "1_132"); // Tatooine: Lars' Moisture Farm (exterior planet site)
                }},
                new HashMap<>() {{
                    put("death-mark", "3_099"); // Death Mark
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

    // Put Han at the Farm, then deploy Death Mark there targeting him.
    private void deployDeathMarkTargetingHan(VirtualTableScenario scn) {
        var han = scn.GetLSCard("han");
        var farm = scn.GetLSCard("farm");
        var deathMark = scn.GetDSCard("death-mark");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(farm, han);
        scn.MoveCardsToDSHand(deathMark);
        scn.SkipToPhase(Phase.DEPLOY);

        scn.DSDeployCard(deathMark);
        scn.DSChooseCard(farm);
        scn.DSChooseCard(han);
        scn.PassAllResponses();
    }

    @Test
    public void DeathMark_3_099_CardInfoListsHanAsTarget() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var deathMark = scn.GetDSCard("death-mark");

        deployDeathMarkTargetingHan(scn);

        assertTrue(deathMark.getTargetedCards(scn.gameState()).containsValue(han));
        assertEquals("Han Solo", deathMark.getTargetedCards(scn.gameState()).values().iterator().next().getTitle());
    }

    @Test
    public void HanSolo_CardInfoListsDeathMarkAsTargetedBy_3_099() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var deathMark = scn.GetDSCard("death-mark");

        deployDeathMarkTargetingHan(scn);

        List<PhysicalCard> targetedBy = GameUtils.getCardsTargeting(scn.gameState(), han);
        assertEquals(1, targetedBy.size());
        assertTrue(targetedBy.contains(deathMark));
        assertEquals("Death Mark", targetedBy.get(0).getTitle());
    }

    @Test
    public void HanSolo_CardInfoHasNoTargetedByWhenNoUtinni() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var farm = scn.GetLSCard("farm");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(farm, han);
        scn.SkipToPhase(Phase.DEPLOY);

        assertTrue(GameUtils.getCardsTargeting(scn.gameState(), han).isEmpty());
    }
}
