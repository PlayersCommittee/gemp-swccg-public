package com.gempukku.swccgo.rules.weapons;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Real-path coverage for may-fire-repeatedly sequencing (#935).
 * Generator Core diverting about-to-be-hit to Used Pile must not break the repeat prompt.
 */
public class RepeatedlyFireTests {
    protected VirtualTableScenario GetGeneratorCoreScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("trooper1", "1_28");
                    put("trooper2", "1_28");
                    put("obiwan", "1_21");
                    put("blasterDeflection", "6_61");
                    put("generatorCore", "13_32");
                }},
                new HashMap<>() {{
                    put("evazan", "1_172");
                    put("sawedOff", "7_320");
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
    public void DrEvazanSawedOffBlasterMayFireRepeatedlyAfterGeneratorCoreUsedPileDivert() {
        // Issue #935: first hit diverted to Used Pile must still offer repeatedly fire.
        var scn = GetGeneratorCoreScenario();

        var trooper1 = scn.GetLSCard("trooper1");
        var trooper2 = scn.GetLSCard("trooper2");
        var generatorCore = scn.GetLSCard("generatorCore");

        var evazan = scn.GetDSCard("evazan");
        var sawedOff = scn.GetDSCard("sawedOff");

        scn.StartGame();
        scn.MoveLocationToTable(generatorCore);
        scn.MoveCardsToLocation(generatorCore, evazan, trooper1, trooper2);
        scn.AttachCardsTo(evazan, sawedOff);

        scn.DSActivateForceCheat(8);
        scn.PrepareDSDestiny(7);
        scn.PrepareDSDestiny(7);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(generatorCore);

        assertTrue(scn.DSCardActionAvailable(sawedOff, "Fire"));
        scn.DSUseCardAction(sawedOff, "Fire");
        scn.DSChooseCard(trooper1);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();

        assertEquals(Zone.USED_PILE, trooper1.getZone());
        assertTrue(scn.DSDecisionAvailable("repeatedly fire"));
        scn.DSChooseYes();

        if (scn.DSDecisionAvailable("Choose target")) {
            assertTrue(scn.DSHasCardChoiceAvailable(trooper2));
            scn.DSChooseCard(trooper2);
        }
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();

        assertEquals(Zone.USED_PILE, trooper2.getZone());
    }

    @Test
    public void DrEvazanSawedOffBlasterMayFireRepeatedlyAfterMissWhenForceRemains() {
        var scn = GetGeneratorCoreScenario();

        var trooper1 = scn.GetLSCard("trooper1");
        var trooper2 = scn.GetLSCard("trooper2");
        var generatorCore = scn.GetLSCard("generatorCore");

        var evazan = scn.GetDSCard("evazan");
        var sawedOff = scn.GetDSCard("sawedOff");

        scn.StartGame();
        scn.MoveLocationToTable(generatorCore);
        scn.MoveCardsToLocation(generatorCore, evazan, trooper1, trooper2);
        scn.AttachCardsTo(evazan, sawedOff);

        scn.DSActivateForceCheat(8);
        scn.PrepareDSDestiny(0);
        scn.PrepareDSDestiny(7);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(generatorCore);

        scn.DSUseCardAction(sawedOff, "Fire");
        scn.DSChooseCard(trooper1);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();

        assertFalse(trooper1.isHit());
        assertEquals(Zone.AT_LOCATION, trooper1.getZone());
        assertTrue(scn.DSDecisionAvailable("repeatedly fire"));
        scn.DSChooseYes();

        if (scn.DSDecisionAvailable("Choose target")) {
            scn.DSChooseCard(trooper1);
        }
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();

        assertEquals(Zone.USED_PILE, trooper1.getZone());
    }

    @Test
    public void BlasterDeflectionCancelDoesNotOfferRepeatedlyFire() {
        // Cancel of the respondable weapon firing must not prompt to fire repeatedly.
        var scn = GetGeneratorCoreScenario();

        var obiwan = scn.GetLSCard("obiwan");
        var trooper2 = scn.GetLSCard("trooper2");
        var blasterDeflection = scn.GetLSCard("blasterDeflection");
        var site = scn.GetLSStartingLocation();

        var evazan = scn.GetDSCard("evazan");
        var sawedOff = scn.GetDSCard("sawedOff");

        scn.StartGame();
        scn.MoveCardsToLocation(site, evazan, obiwan, trooper2);
        scn.AttachCardsTo(evazan, sawedOff);
        scn.MoveCardsToLSHand(blasterDeflection);

        scn.DSActivateForceCheat(8);
        scn.PrepareDSDestiny(7);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);

        // Pass Obi-Wan optional battle-just-initiated trigger if offered.
        scn.PassAllResponses();

        assertTrue(scn.DSCardActionAvailable(sawedOff, "Fire"));
        scn.DSUseCardAction(sawedOff, "Fire");
        scn.DSChooseCard(obiwan);

        // Optional responses to Fire - LS cancels with Blaster Deflection USED.
        assertTrue(scn.LSCardPlayAvailable(blasterDeflection));
        scn.LSPlayCard(blasterDeflection);
        scn.PassCardPlayResponses();
        scn.PassAllResponses();

        assertFalse(scn.DSDecisionAvailable("repeatedly fire"));
        assertTrue(scn.AwaitingDSWeaponsSegmentActions() || scn.AwaitingLSWeaponsSegmentActions()
                || scn.DSDecisionAvailable("Pass") || scn.LSDecisionAvailable("Pass"));
    }
}
