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

    // Death Mark cancels if Han is already at its site, so Han stays at the Light starting location.
    private void deployDeathMarkTargetingHan(VirtualTableScenario scn) {
        var han = scn.GetLSCard("han");
        var farm = scn.GetLSCard("farm");
        var deathMark = scn.GetDSCard("death-mark");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), han);
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
        deployDeathMarkTargetingHan(scn);

        var han = scn.GetLSCard("han");
        var deathMark = scn.GetDSCard("death-mark");

        assertTrue(deathMark.getZone().isInPlay());
        assertTrue(deathMark.getTargetedCards(scn.gameState()).containsValue(han));
        assertTrue(deathMark.getTargetedCards(scn.gameState()).values().stream().anyMatch(c -> "Han Solo".equals(c.getTitle())));
    }

    @Test
    public void HanSolo_CardInfoListsDeathMarkAsTargetedBy_3_099() {
        var scn = GetScenario();
        deployDeathMarkTargetingHan(scn);

        var han = scn.GetLSCard("han");
        var deathMark = scn.GetDSCard("death-mark");

        List<PhysicalCard> targetedBy = GameUtils.getCardsTargeting(scn.gameState(), han);
        assertEquals(1, targetedBy.size());
        assertTrue(targetedBy.contains(deathMark));
        assertEquals("Death Mark", targetedBy.get(0).getTitle());
    }

    @Test
    public void HanSolo_CardInfoHasNoTargetedByWhenNoUtinni() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");

        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), han);
        scn.SkipToPhase(Phase.DEPLOY);

        assertTrue(GameUtils.getCardsTargeting(scn.gameState(), han).isEmpty());
    }
}
