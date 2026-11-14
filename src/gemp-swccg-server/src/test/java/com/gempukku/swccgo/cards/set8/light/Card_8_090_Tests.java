package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;


import org.junit.Test;


import java.util.HashMap;

import static org.junit.Assert.assertTrue;

/**
 * Tests for Endor 8_090 Explosive Charge.
 */
public class Card_8_090_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("charge", "8_90");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
    public void ExplosiveChargeLostOrderChosenByPlayerWhoseTurnItIs() {
        var scn = GetScenario();

        var charge = scn.GetLSCard("charge");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();
        var vader = scn.GetDSCard("vader");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, vader, trooper);
        scn.AttachCardsTo(site, charge);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(0);
        scn.PrepareDSDestiny(0);
        assertTrue(scn.DSCanInitiateBattle(site));
        scn.DSInitiateBattle(site);
        scn.SkipToEndOfPowerSegment(false);
        scn.PrepareLSDestiny(5);
        for (int i = 0; i < 20 && !(scn.LSGetDecision() != null && scn.LSCardActionAvailable(charge, "Draw destiny")); i++) {
            if (scn.AwaitingLSBattleDamagePayment()) scn.LSPayRemainingBattleDamageFromReserveDeck();
            else scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertTrue(scn.LSCardActionAvailable(charge, "Draw destiny"));
        scn.LSUseCardAction(charge, "Draw destiny");
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose card to be lost"));
        assertTrue(scn.DSHasCardChoiceAvailable(vader));
        assertTrue(scn.DSHasCardChoiceAvailable(trooper));
    }
}