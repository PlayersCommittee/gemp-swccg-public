package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
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
                    put("luke", "1_019"); // Luke Skywalker — ability 4
                    put("luke_jedi", "9_024"); // Luke Skywalker, Jedi Knight — destiny 6
                    put("grenade", "3_073"); // Concussion Grenade
                    put("biggs", "1_003"); // Biggs — destiny 2 warrior
                }},
                new HashMap<>() {{
                    put("vader", "1_168"); // Darth Vader — ability 6, destiny 1
                }},
                10,
                10,
                StartingSetup.LSStartingLocation("1_128"), // Tatooine: Cantina
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void CantinaBrawlEscortOnlyLostCaptiveMayRally() {
        // Cantina Brawl destinies match Vader ability (6) only — Luke (ability 4) Escape/Rallys.
        var scn = GetScenario();

        var brawl = scn.GetLSCard("brawl");
        var luke = scn.GetLSCard("luke");
        var cantina = scn.GetLSStartingLocation();
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveCardsToHand(brawl);
        scn.MoveCardsToLocation(cantina, vader, luke);
        scn.CaptureCardWith(vader, luke);

        assertTrue(luke.isCaptive());
                scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSActivateForceCheat(2);
        scn.PrepareLSDestiny(6);
        scn.PrepareDSDestiny(1);

        assertTrue(scn.LSPlayLostInterruptAvailable(brawl));
        scn.LSPlayLostInterrupt(brawl);
        scn.PassCardAndForceUseResponses();
        // destinies resolve; Vader is the only matching ability character to lose
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, vader);
        assertTrue(scn.LSReleaseDecisionAvailable());
        scn.LSChooseRally();
        scn.PassAllResponses();

        assertFalse(luke.isCaptive());
        assertAtLocation(cantina, luke);
    }

    @Test
    public void CantinaBrawlEscortAndCaptiveBothLostNoUsedEscape() {
        // Destinies 6 and 4 match Vader ability and Luke ability — captive lost with escort, no Used Escape.
        var scn = GetScenario();

        var brawl = scn.GetLSCard("brawl");
        var luke = scn.GetLSCard("luke");
        var cantina = scn.GetLSStartingLocation();
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveCardsToHand(brawl);
        scn.MoveCardsToLocation(cantina, vader, luke);
        scn.CaptureCardWith(vader, luke);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSActivateForceCheat(2);
        scn.PrepareLSDestiny(6);
        scn.PrepareDSDestiny(4);

        scn.LSPlayLostInterrupt(brawl);
        scn.PassCardAndForceUseResponses();
        // Prefer escort first so the pull-in path is exercised
        if (scn.LSDecisionAvailable("Choose card to be lost")) {
            scn.LSChooseCard(vader);
        }
        scn.PassAllResponses();

        assertFalse(scn.LSReleaseDecisionAvailable());
        assertInZone(Zone.LOST_PILE, vader);
        assertInZone(Zone.LOST_PILE, luke);
        assertFalse(luke.isCaptive());
    }

    @Test
    public void ConcussionGrenadeMatchingEscortDestinyOnlyReleasesCaptive() {
        // Grenade destiny 1 matches Vader (destiny 1) only — Luke Jedi (destiny 6) Escape/Rallys.
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke_jedi");
        var grenade = scn.GetLSCard("grenade");
        var biggs = scn.GetLSCard("biggs");
        var cantina = scn.GetLSStartingLocation();
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveCardsToLocation(cantina, vader, luke, biggs);
        scn.AttachCardsTo(biggs, grenade);
        scn.CaptureCardWith(vader, luke);

        assertTrue(luke.isCaptive());

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(1);

        assertTrue(scn.LSCardActionAvailable(grenade));
        scn.LSUseCardAction(grenade);
        scn.LSChooseCard(cantina);
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, vader);
        assertTrue(scn.LSReleaseDecisionAvailable());
        scn.LSChooseEscape();
        scn.PassAllResponses();

        assertFalse(luke.isCaptive());
        assertInZone(Zone.USED_PILE, luke);
    }
}