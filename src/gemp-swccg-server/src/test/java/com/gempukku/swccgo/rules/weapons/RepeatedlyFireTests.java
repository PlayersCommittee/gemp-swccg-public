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
                    put("luke", "108_003");
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

        scn.DSActivateForceCheat(10);
        scn.PrepareDSDestiny(7);
        scn.PrepareDSDestiny(7);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(generatorCore);
        scn.PassAllResponses();

        assertTrue(scn.DSCardActionAvailable(sawedOff, "Fire"));
        scn.DSUseCardAction(sawedOff, "Fire");
        scn.DSChooseCard(trooper1);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        // Required Generator Core place-in-Used may remain; clear it.
        if (scn.GetCurrentDecision().getText().toLowerCase().contains("required")) {
            scn.PassResponses("required");
        }
        scn.PassAllResponses();

        assertEquals(Zone.TOP_OF_USED_PILE, trooper1.getZone());
        assertTrue("Expected repeatedly-fire prompt after Generator Core Used Pile divert; decision="
                        + scn.GetCurrentDecision().getText(),
                scn.DSDecisionAvailable("repeatedly fire"));
        scn.DSChooseYes();

        if (scn.DSDecisionAvailable("Choose target")) {
            assertTrue(scn.DSHasCardChoiceAvailable(trooper2));
            scn.DSChooseCard(trooper2);
        }
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        if (scn.GetCurrentDecision().getText().toLowerCase().contains("required")) {
            scn.PassResponses("required");
        }

        assertTrue(trooper2.getZone() == Zone.TOP_OF_USED_PILE
                || trooper2.isHit()
                || scn.DSDecisionAvailable("repeatedly fire")
                || scn.AwaitingDSWeaponsSegmentActions()
                || scn.AwaitingLSWeaponsSegmentActions());
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

        scn.DSActivateForceCheat(10);
        scn.PrepareDSDestiny(0);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(generatorCore);
        scn.PassAllResponses();

        scn.DSUseCardAction(sawedOff, "Fire");
        scn.DSChooseCard(trooper1);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();

        assertFalse(trooper1.isHit());
        assertEquals(Zone.AT_LOCATION, trooper1.getZone());
        assertTrue(scn.DSDecisionAvailable("repeatedly fire"));
        scn.PrepareDSDestiny(7);
        scn.DSChooseYes();

        if (scn.DSDecisionAvailable("Choose target")) {
            scn.DSChooseCard(trooper1);
        }
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        if (scn.GetCurrentDecision().getText().toLowerCase().contains("required")) {
            scn.PassResponses("required");
        }

        assertEquals(Zone.TOP_OF_USED_PILE, trooper1.getZone());
    }

    @Test
    public void BlasterDeflectionCancelDoesNotOfferRepeatedlyFire() {
        // Cancel of the respondable weapon firing must not prompt to fire repeatedly.
        var scn = GetGeneratorCoreScenario();

        var luke = scn.GetLSCard("luke");
        var trooper2 = scn.GetLSCard("trooper2");
        var blasterDeflection = scn.GetLSCard("blasterDeflection");
        var site = scn.GetLSStartingLocation();

        var evazan = scn.GetDSCard("evazan");
        var sawedOff = scn.GetDSCard("sawedOff");

        scn.StartGame();
        scn.MoveCardsToLocation(site, evazan, luke, trooper2);
        scn.AttachCardsTo(evazan, sawedOff);
        scn.MoveCardsToHand(blasterDeflection);

        scn.DSActivateForceCheat(8);
        scn.PrepareDSDestiny(7);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(sawedOff, "Fire"));
        scn.DSUseCardAction(sawedOff, "Fire");
        scn.DSChooseCard(luke);
        scn.LSPass(); // Use Force - Optional responses
        scn.DSPass();
        assertTrue(scn.LSPlayUsedInterruptAvailable(blasterDeflection));
        scn.LSPlayUsedInterrupt(blasterDeflection);
        scn.PassAllResponses();

        assertFalse("Canceled fire must not offer repeatedly fire; decision="
                        + scn.GetCurrentDecision().getText(),
                scn.DSDecisionAvailable("repeatedly fire"));
    }
}
