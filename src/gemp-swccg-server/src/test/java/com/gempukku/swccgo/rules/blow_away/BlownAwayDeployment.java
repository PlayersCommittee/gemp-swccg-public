package com.gempukku.swccgo.rules.blow_away;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlownAwayDeployment {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("deactivate","8_043"); //Deactivate The Shield Generator
                    put("bunker","8_070"); //Endor: Bunker
                    put("ls_bunker","8_070"); //Endor: Bunker (2nd copy)
                    put("endor","8_068"); //Endor (system)
                }},
                new HashMap<>()
                {{
                    put("ds_bunker","8_160"); //Endor: Bunker
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

    //AR Appendix C: Blown Away
    //Certain cards will cause locations to be 'blown away.'
    //...From this point on, the location cannot be deployed or
    //converted again.

    @Test
    public void BlownAwaySiteCannotBeDeployed() {
        //confirm site that was 'blown away' cannot be deployed (by either player)
        var scn = GetScenario();

        var trooper = scn.GetLSFiller(1);
        var deactivate = scn.GetLSCard("deactivate");
        var bunker = scn.GetLSCard("bunker");
        var ls_bunker = scn.GetLSCard("ls_bunker");
        var endor = scn.GetLSCard("endor");

        var ds_bunker = scn.GetDSCard("ds_bunker");

        scn.StartGame();

        scn.MoveLocationToTable(bunker);
        scn.MoveCardsToLocation(bunker, trooper);
        scn.AttachCardsTo(bunker,deactivate);

        scn.MoveCardsToLSHand(ls_bunker, endor);
        scn.MoveCardsToDSHand(ds_bunker);

        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(ds_bunker));

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(7); //draw 2 destiny > 12 to blow bunker
        scn.PrepareLSDestiny(6);
        scn.LSUseCardAction(deactivate);
        scn.PassAllResponses();

            //opponent loses 8 force
        scn.DSPayForceLossFromForcePile();
        scn.DSPayForceLossFromForcePile();
        scn.DSPayForceLossFromForcePile();
        scn.DSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertTrue(bunker.isBlownAway());

        scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.LSDeployAvailable(ls_bunker));
        assertTrue(scn.LSDeployAvailable(endor));

        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSDeployAvailable(ds_bunker));
    }
}
