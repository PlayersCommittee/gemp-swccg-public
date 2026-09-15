package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * VHD coverage for all-cards escort loss releasing captives not in the lost set (#981 / #645 / #325).
 */
public class Card_1_073_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("brawl", "1_073"); // Cantina Brawl
                    put("luke", "1_019"); // Luke Skywalker ? ability 4
                    put("luke_jedi", "9_024"); // Luke Skywalker, Jedi Knight ? destiny 6
                    put("grenade", "3_073"); // Concussion Grenade
                    put("biggs", "1_003"); // Biggs ? destiny 2 warrior
                    put("cantina", "1_128"); // Tatooine: Cantina
                }},
                new HashMap<>() {{
                    put("vader", "1_168"); // Darth Vader ? ability 6, destiny 1
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

    private void playCantinaBrawl(VirtualTableScenario scn, PhysicalCardImpl brawl, PhysicalCardImpl cantina) {
        assertTrue(scn.LSCardPlayAvailable(brawl));
        scn.LSPlayCard(brawl);
        if (scn.LSDecisionAvailable("Choose Cantina")) {
            scn.LSChooseCard(cantina);
        }
        scn.PassCardAndForceUseResponses();
        scn.PassAllResponses();
    }

    private void chooseLostPileOrderPreferring(VirtualTableScenario scn, PhysicalCardImpl preferFirst) {
        while (scn.LSDecisionAvailable("Choose card to be lost")) {
            if (preferFirst != null && scn.LSHasCardChoiceAvailable(preferFirst)) {
                scn.LSChooseCard(preferFirst);
            } else {
                // Any remaining choice ? preferFirst already taken
                break;
            }
            scn.PassAllResponses();
            preferFirst = null;
        }
        while (scn.DSDecisionAvailable("Choose card to be lost")) {
            if (preferFirst != null && scn.DSHasCardChoiceAvailable(preferFirst)) {
                scn.DSChooseCard(preferFirst);
            } else {
                break;
            }
            scn.PassAllResponses();
            preferFirst = null;
        }
    }

    private void releaseCaptiveWith(VirtualTableScenario scn, PhysicalCardImpl captive, boolean rally) {
        if (scn.LSDecisionAvailable("Choose character to release")) {
            scn.LSChooseCard(captive);
        } else if (scn.DSDecisionAvailable("Choose character to release")) {
            scn.DSChooseCard(captive);
        }
        boolean releaseReady = scn.LSReleaseDecisionAvailable() || scn.DSDecisionAvailable("Choose release option for ");
        if (!releaseReady) {
            var dec = scn.GetCurrentDecision();
            String msg = dec == null ? "no decision" : dec.getText();
            throw new AssertionError("Expected Escape/Rally prompt, got: " + msg + "; captive=" + captive.isCaptive() + " zone=" + captive.getZone());
        }
        if (scn.LSReleaseDecisionAvailable()) {
            if (rally) { scn.LSChooseRally(); } else { scn.LSChooseEscape(); }
        } else {
            if (rally) { scn.DSChoose("Rally"); } else { scn.DSChoose("Escape"); }
        }
        scn.PassAllResponses();
    }

    @Test
    public void CantinaBrawlEscortOnlyLostCaptiveMayRally() {
        // Cantina Brawl destinies match Vader ability (6) only ? Luke (ability 4) Escape/Rallys.
        var scn = GetScenario();

        var brawl = scn.GetLSCard("brawl");
        var luke = scn.GetLSCard("luke");
        var cantina = scn.GetLSCard("cantina");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToHand(brawl);
        scn.MoveCardsToLocation(cantina, vader, luke);
        scn.CaptureCardWith(vader, luke);

        assertTrue(luke.isCaptive());

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSActivateForceCheat(2);
        scn.PrepareLSDestiny(6);
        scn.PrepareDSDestiny(1);

        playCantinaBrawl(scn, brawl, cantina);
        chooseLostPileOrderPreferring(scn, vader);
        // Release happens before escort is placed in Lost Pile
        releaseCaptiveWith(scn, luke, true);

        assertInZone(Zone.LOST_PILE, vader);
        assertFalse(luke.isCaptive());
        assertAtLocation(cantina, luke);
    }

    @Test
    public void CantinaBrawlEscortAndCaptiveBothLostNoUsedEscape() {
        // Destinies 6 and 4 match Vader ability and Luke ability ? captive lost with escort, no Used Escape.
        var scn = GetScenario();

        var brawl = scn.GetLSCard("brawl");
        var luke = scn.GetLSCard("luke");
        var cantina = scn.GetLSCard("cantina");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToHand(brawl);
        scn.MoveCardsToLocation(cantina, vader, luke);
        scn.CaptureCardWith(vader, luke);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSActivateForceCheat(2);
        scn.PrepareLSDestiny(6);
        scn.PrepareDSDestiny(4);

        playCantinaBrawl(scn, brawl, cantina);
        chooseLostPileOrderPreferring(scn, vader);

        assertFalse(scn.LSDecisionAvailable("Choose character to release"));
        assertFalse(scn.LSReleaseDecisionAvailable());
        assertInZone(Zone.LOST_PILE, vader);
        assertInZone(Zone.LOST_PILE, luke);
        assertFalse(luke.isCaptive());
    }

    @Test
    public void ConcussionGrenadeMatchingEscortDestinyOnlyReleasesCaptive() {
        // Grenade destiny 1 matches Vader (destiny 1) only ? Luke Jedi (destiny 6) Escape/Rallys.
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke_jedi");
        var grenade = scn.GetLSCard("grenade");
        var biggs = scn.GetLSCard("biggs");
        var cantina = scn.GetLSCard("cantina");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, vader, luke, biggs);
        scn.AttachCardsTo(biggs, grenade);
        scn.CaptureCardWith(vader, luke);

        assertTrue(luke.isCaptive());

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSActivateForceCheat(1);
        scn.PrepareLSDestiny(1);

        scn.LSInitiateBattle(cantina);
        scn.PassBattleStartResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(grenade, "Fire"));
        scn.LSUseCardAction(grenade, "Fire");
        scn.LSChooseCard(cantina);
        scn.PassAllResponses();
        chooseLostPileOrderPreferring(scn, vader);
        // Release happens before escort is placed in Lost Pile
        releaseCaptiveWith(scn, luke, false);

        assertInZone(Zone.LOST_PILE, vader);
        assertFalse(luke.isCaptive());
        assertInZone(Zone.USED_PILE, luke);
    }
}
