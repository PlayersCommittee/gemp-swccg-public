package com.gempukku.swccgo.cards;

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

/**
 * Engine/UI reverse-targeting for Card Information: GameUtils.getCardsTargeting
 * walks getTargetedCards across all cards in play (not limited to one Utinni).
 */
public class CardInfoTargetedBy_Tests {

    protected VirtualTableScenario GetHomesteadScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_019"); // Premiere Luke Skywalke
                    put("farm", "1_132"); // Tatooine: Lars' Moisture Farm
                }},
                new HashMap<>() {{
                    put("homestead", "7_226"); // Destroyed Homestead
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

    protected VirtualTableScenario GetLateralDamageScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("falcon", "1_143"); // Millennium Falcon
                    put("yavin", "1_135"); // Yavin 4 system (Falcon stays here)
                }},
                new HashMap<>() {{
                    put("lateral", "1_222"); // Lateral Damage
                    put("tatooine", "1_289"); // Tatooine system (Utinni deploys here)
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

    // Homestead cancels if Luke is already at the farm, so Luke stays at the Light starting location.
    private void deployHomesteadTargetingLuke(VirtualTableScenario scn) {
        var luke = scn.GetLSCard("luke");
        var farm = scn.GetLSCard("farm");
        var homestead = scn.GetDSCard("homestead");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), luke);
        scn.MoveCardsToDSHand(homestead);
        scn.SkipToPhase(Phase.DEPLOY);

        scn.DSDeployCard(homestead);
        scn.DSChooseCard(farm);
        scn.DSChooseCard(luke);
        scn.PassAllResponses();
    }

    // Lateral Damage cancels/triggers if Falcon is at the same system, so Falcon stays at Yavin 4.
    private void deployLateralDamageTargetingFalcon(VirtualTableScenario scn) {
        var falcon = scn.GetLSCard("falcon");
        var yavin = scn.GetLSCard("yavin");
        var tatooine = scn.GetDSCard("tatooine");
        var lateral = scn.GetDSCard("lateral");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(yavin, falcon);
        scn.MoveCardsToDSHand(lateral);
        scn.SkipToPhase(Phase.DEPLOY);

        scn.DSDeployCard(lateral);
        scn.DSChooseCard(tatooine);
        scn.DSChooseCard(falcon);
        scn.PassAllResponses();
    }

    @Test
    public void DestroyedHomestead_7_226_CardInfoListsLukeAsTarget() {
        var scn = GetHomesteadScenario();
        deployHomesteadTargetingLuke(scn);

        var luke = scn.GetLSCard("luke");
        var homestead = scn.GetDSCard("homestead");

        assertTrue(homestead.getZone().isInPlay());
        assertTrue(homestead.getTargetedCards(scn.gameState()).containsValue(luke));
        assertTrue(homestead.getTargetedCards(scn.gameState()).values().stream().anyMatch(c -> "Luke Skywalker".equals(c.getTitle())));
    }

    @Test
    public void LukeSkywalker_CardInfoListsDestroyedHomesteadAsTargetedBy_7_226() {
        var scn = GetHomesteadScenario();
        deployHomesteadTargetingLuke(scn);

        var luke = scn.GetLSCard("luke");
        var homestead = scn.GetDSCard("homestead");

        List<PhysicalCard> targetedBy = GameUtils.getCardsTargeting(scn.gameState(), luke);
        assertEquals(1, targetedBy.size());
        assertTrue(targetedBy.contains(homestead));
        assertEquals("Destroyed Homestead", targetedBy.get(0).getTitle());
    }

    @Test
    public void LateralDamage_1_222_CardInfoListsFalconAsTarget() {
        var scn = GetLateralDamageScenario();
        deployLateralDamageTargetingFalcon(scn);

        var falcon = scn.GetLSCard("falcon");
        var lateral = scn.GetDSCard("lateral");

        assertTrue(lateral.getZone().isInPlay());
        assertTrue(lateral.getTargetedCards(scn.gameState()).containsValue(falcon));
        assertTrue(lateral.getTargetedCards(scn.gameState()).values().stream().anyMatch(c -> "Millennium Falcon".equals(c.getTitle())));
    }

    @Test
    public void MillenniumFalcon_CardInfoListsLateralDamageAsTargetedBy_1_222() {
        var scn = GetLateralDamageScenario();
        deployLateralDamageTargetingFalcon(scn);

        var falcon = scn.GetLSCard("falcon");
        var lateral = scn.GetDSCard("lateral");

        List<PhysicalCard> targetedBy = GameUtils.getCardsTargeting(scn.gameState(), falcon);
        assertEquals(1, targetedBy.size());
        assertTrue(targetedBy.contains(lateral));
        assertEquals("Lateral Damage", targetedBy.get(0).getTitle());
    }

    @Test
    public void LukeSkywalker_CardInfoHasNoTargetedByWhenNoUtinni() {
        var scn = GetHomesteadScenario();
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), luke);
        scn.SkipToPhase(Phase.DEPLOY);

        assertTrue(GameUtils.getCardsTargeting(scn.gameState(), luke).isEmpty());
    }

    @Test
    public void MillenniumFalcon_CardInfoHasNoTargetedByWhenNoUtinni() {
        var scn = GetLateralDamageScenario();
        var falcon = scn.GetLSCard("falcon");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, falcon);
        scn.SkipToPhase(Phase.DEPLOY);

        assertTrue(GameUtils.getCardsTargeting(scn.gameState(), falcon).isEmpty());
    }
}
