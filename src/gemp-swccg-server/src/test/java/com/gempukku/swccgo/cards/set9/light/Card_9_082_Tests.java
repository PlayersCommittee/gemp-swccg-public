package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Issue #939: firing X-wing Laser Cannons from Red Squadron 4 should offer
 * "X=3 using Red Squadron 4 (use 2 Force)" so 2 Force sets X=3, not X=2.
 */
public class Card_9_082_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rs4", "9_82");
                    put("hobbie", "3_5");
                    put("xwlc", "7_162");
                }},
                new HashMap<>() {{
                    put("tie", "1_304");
                }},
                10,
                10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void RedSquadron4MayUseTwoForceToMakeXwingLaserCannonX3() {
        var scn = GetScenario();
        var rs4 = scn.GetLSCard("rs4");
        var hobbie = scn.GetLSCard("hobbie");
        var xwlc = scn.GetLSCard("xwlc");
        var tie = scn.GetDSCard("tie");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, rs4, tie);
        scn.BoardAsPilot(rs4, hobbie);
        scn.AttachCardsTo(rs4, xwlc);
        scn.EnsureLSForcePile(2);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(system);
        scn.PassBattleStartResponses();
        scn.PassAllResponses();

        assertTrue(scn.LSCardActionAvailable(xwlc, "Fire"));
        scn.PrepareLSDestiny(1);
        scn.LSUseCardAction(xwlc, "Fire");
        scn.LSChooseCard(tie);
        assertTrue(scn.LSDecisionAvailable("Choose X for this firing"));
        var xChoices = scn.LSGetADParamAsList("results");
        int special = -1;
        for (int i = 0; i < xChoices.size(); i++) {
            if (xChoices.get(i).contains("using Red Squadron 4")) {
                special = i;
                break;
            }
        }
        assertTrue("Missing X=3 using Red Squadron 4; choices=" + xChoices, special >= 0);
        int forceBefore = scn.GetLSForcePileCount();
        scn.LSDecided(special);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();

        assertEquals("Red Squadron 4 option uses 2 Force", forceBefore - 2, scn.GetLSForcePileCount());
        assertTrue("X=3 must lose the TIE (destiny 1 + X 3 > maneuver 3)",
                tie.getZone() == Zone.LOST_PILE || tie.getZone() == Zone.TOP_OF_LOST_PILE);
    }
}
