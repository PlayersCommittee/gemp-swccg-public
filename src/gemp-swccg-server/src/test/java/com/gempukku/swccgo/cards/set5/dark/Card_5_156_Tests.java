package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Real-path coverage for #241: Surprise Effect relocate must respect legal deploy targets
 * (MayNotDeployToTarget and immunity to the Effect title).
 */
public class Card_5_156_Tests {

    protected VirtualTableScenario GetMayNotDeployScenario() throws DecisionResultInvalidException {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin_v", "211_32");
                    put("war_room", "1_139");
                }},
                new HashMap<>() {{
                    put("surprise", "5_156");
                    put("presence", "1_227");
                    put("cantina", "1_290");
                    put("marketplace", "12_176");
                }},
                10,
                10,
                StartingSetup.LSStartingLocation("211_32"),
                StartingSetup.DSStartingLocation("1_290"),
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    protected VirtualTableScenario GetRevolutionImmunityScenario() throws DecisionResultInvalidException {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("boss_nass", "14_49");
                    put("revolution", "1_62");
                }},
                new HashMap<>() {{
                    put("surprise", "5_156");
                    put("cantina", "1_290");
                    put("marketplace", "12_176");
                }},
                10,
                10,
                StartingSetup.LSStartingLocation("14_49"),
                StartingSetup.DSStartingLocation("1_290"),
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    protected VirtualTableScenario GetExpandTheEmpireImmunityScenario() throws DecisionResultInvalidException {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("turbolift", "209_27");
                }},
                new HashMap<>() {{
                    put("surprise", "5_156");
                    put("expand", "1_215");
                    put("marketplace", "12_176");
                    put("desert", "6_169");
                }},
                10,
                10,
                StartingSetup.LSStartingLocation("209_27"),
                StartingSetup.DSStartingLocation("12_176"),
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private void prepareSurpriseRelocate(VirtualTableScenario scn, PhysicalCardImpl host, PhysicalCardImpl effect,
                                         PhysicalCardImpl surprise, PhysicalCardImpl... extraLocations) throws DecisionResultInvalidException {
        scn.StartGame();
        for (PhysicalCardImpl loc : extraLocations) {
            scn.MoveLocationToTable(loc);
        }
        scn.AttachCardsTo(host, effect);
        scn.MoveCardsToHand(surprise);
        assertEquals(Zone.ATTACHED, effect.getZone());
        assertEquals(host, effect.getAttachedTo());

        // Cheat Force before turn skip so reserve deck still has cards; SkipToDSTurn also activates.
        scn.DSActivateForceCheat(6);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertTrue("Surprise should be playable with Force available", scn.DSCardActionAvailable(surprise));
        scn.DSPlayCard(surprise);
        scn.DSChooseCard(effect);
    }

    @Test
    public void SurpriseCannotRelocatePresenceOfTheForceToYavinSiteBlockedByMayNotDeployToTarget() throws DecisionResultInvalidException {
        var scn = GetMayNotDeployScenario();
        var warRoom = scn.GetLSCard("war_room");
        var cantina = scn.GetDSCard("cantina");
        var presence = scn.GetDSCard("presence");
        var surprise = scn.GetDSCard("surprise");
        var marketplace = scn.GetDSCard("marketplace");

        prepareSurpriseRelocate(scn, cantina, presence, surprise, warRoom, marketplace);

        assertFalse("Surprise must not offer Yavin War Room as relocate target", scn.DSHasCardChoiceAvailable(warRoom));
        assertTrue("Surprise must allow relocate to marketplace", scn.DSHasCardChoiceAvailable(marketplace));

        scn.DSChooseCard(marketplace);
        scn.PassAllResponses();
        assertEquals("Legal relocate must succeed", marketplace, presence.getAttachedTo());
    }

    @Test
    public void SurpriseCannotRelocateRevolutionToBossNassChambersImmuneToRevolution() throws DecisionResultInvalidException {
        var scn = GetRevolutionImmunityScenario();
        var bossNass = scn.GetLSCard("boss_nass");
        var revolution = scn.GetLSCard("revolution");
        var cantina = scn.GetDSCard("cantina");
        var surprise = scn.GetDSCard("surprise");
        var marketplace = scn.GetDSCard("marketplace");

        prepareSurpriseRelocate(scn, cantina, revolution, surprise, marketplace);

        assertFalse("Surprise must not relocate Revolution onto Boss Nass Chambers", scn.DSHasCardChoiceAvailable(bossNass));
        assertTrue("Surprise may relocate Revolution onto marketplace", scn.DSHasCardChoiceAvailable(marketplace));
    }

    @Test
    public void SurpriseCannotRelocateExpandTheEmpireToScarifTurboliftComplexImmuneToExpandTheEmpire() throws DecisionResultInvalidException {
        var scn = GetExpandTheEmpireImmunityScenario();
        var turbolift = scn.GetLSCard("turbolift");
        var marketplace = scn.GetDSCard("marketplace");
        var desert = scn.GetDSCard("desert");
        var expand = scn.GetDSCard("expand");
        var surprise = scn.GetDSCard("surprise");

        prepareSurpriseRelocate(scn, marketplace, expand, surprise, desert);

        assertFalse("Surprise must not relocate Expand The Empire onto Turbolift Complex", scn.DSHasCardChoiceAvailable(turbolift));
        assertTrue("Surprise may relocate Expand The Empire onto Tatooine Desert", scn.DSHasCardChoiceAvailable(desert));
    }
}
