package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class Card_3_155_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_019");
                }},
                new HashMap<>() {{
                    put("blizzard2", "3_155");
                    put("trooper", "1_194");
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
    public void Blizzard2PassengerSharesEnclosedVehicleImmunity() {
        var scn = GetScenario();
        var blizzard2 = scn.GetDSCard("blizzard2");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, blizzard2);
        scn.BoardAsPassenger(blizzard2, trooper);

        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), blizzard2), scn.epsilon);
        assertEquals("Passenger aboard enclosed Blizzard 2 shares the vehicle's immunity < 4",
                4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), trooper), scn.epsilon);
    }
}
