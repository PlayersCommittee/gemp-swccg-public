package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_6_039_Tests {
    @Test
    public void ShasaTielChoosesOpponentForcePileOrderDuringOpponentsTurn() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{ put("shasa", "6_039"); }}, new HashMap<>(), 20, 20,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        var shasa = scn.GetLSCard("shasa");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), shasa);
        scn.SkipToDSTurn(Phase.CONTROL);
        for (var card : new java.util.ArrayList<>(scn.gameState().getCardPile(scn.DS, Zone.FORCE_PILE)))
            scn.MoveCardsToDSHand((com.gempukku.swccgo.game.PhysicalCardImpl) card);
        var first = scn.GetDSDestiny(1);
        var second = scn.GetDSDestiny(2);
        scn.MoveCardsToTopOfDSForcePile(first, second);
        scn.LSActivateForceCheat(3);
        scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(shasa, "Examine and reorder"));
        scn.LSUseCardAction(shasa, "Examine and reorder");
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Choose next card to put on"));
        assertFalse(scn.DSDecisionAvailable("Choose next card to put on"));
        scn.LSChooseCard(first);
        scn.PassAllResponses();
        assertEquals(second, scn.gameState().getCardPile(scn.DS, Zone.FORCE_PILE).get(0));
    }
}
