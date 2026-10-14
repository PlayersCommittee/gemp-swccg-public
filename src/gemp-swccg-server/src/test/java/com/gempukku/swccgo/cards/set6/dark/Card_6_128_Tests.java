package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_6_128_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("alien", "1_012");
                    put("alien2", "1_012");
                    put("farm", "1_132");
                }},
                new HashMap<>()
                {{
                    put("velken", "6_128");
                    put("hanSeeker", "1_316");
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
    public void VelkenTezeriStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("velken").getBlueprint();

        assertEquals("Velken Tezeri", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(2, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            //null
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.JABBAS_PALACE);
            add(Icon.WARRIOR);
        }});
        assertEquals(ExpansionSet.JABBAS_PALACE, card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
    }

    @Test
    public void VelkenTezeriAllowsSeekersToDeployFreeAtControlledSite() {
        var scn = GetScenario();

        var velken = scn.GetDSCard("velken");
        var seeker = scn.GetDSCard("hanSeeker");
        var site = scn.GetDSStartingLocation();
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, velken);
        scn.MoveCardsToDSHand(seeker);

        float freeCost = scn.game().getModifiersQuerying().getDeployCost(
                scn.game().getGameState(), seeker, seeker, site, false, null, false, 0, null, false);
        assertEquals(0f, freeCost, scn.epsilon);

        float otherCost = scn.game().getModifiersQuerying().getDeployCost(
                scn.game().getGameState(), seeker, seeker, lsSite, false, null, false, 0, null, false);
        assertEquals(1f, otherCost, scn.epsilon);
    }

    @Test
    public void VelkenTezeriAllowsSeekersToMoveForFreeWhileInPlay() {
        var scn = GetScenario();

        var velken = scn.GetDSCard("velken");
        var seeker = scn.GetDSCard("hanSeeker");
        var alien = scn.GetLSCard("alien");
        var farm = scn.GetLSCard("farm");
        var lsSite = scn.GetLSStartingLocation();
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        // LS controls the site Velken occupies; move-for-free is not tied to that control clause
        scn.MoveCardsToLocation(lsSite, velken, alien);
        scn.MoveCardsToLocation(farm, seeker);

        float cost = scn.game().getModifiersQuerying().getMoveUsingLandspeedCost(
                scn.game().getGameState(), seeker, farm, dsSite, false, 0);
        assertEquals(0f, cost, scn.epsilon);
    }

    @Test
    public void VelkenTezeriAllowsSeekerToIgnoreTargets() {
        var scn = GetScenario();

        var velken = scn.GetDSCard("velken");
        var seeker = scn.GetDSCard("hanSeeker");
        var alien = scn.GetLSCard("alien");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        // Keep Velken (alien ability < 3) off the seeker site so Han Seeker does not auto-target him
        scn.MoveCardsToLocation(site, velken);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), seeker, alien);

        assertTrue(scn.game().getModifiersQuerying().hasFlagActive(
                scn.game().getGameState(), ModifierFlag.SEEKERS_MAY_IGNORE_TARGETS, scn.DS));
        assertFalse(scn.game().getModifiersQuerying().hasFlagActive(
                scn.game().getGameState(), ModifierFlag.SEEKERS_MAY_IGNORE_TARGETS, scn.LS));
    }

    @Test
    public void VelkenTezeriLeavesTableStopsSeekerIgnoring() {
        var scn = GetScenario();

        var velken = scn.GetDSCard("velken");
        var seeker = scn.GetDSCard("hanSeeker");
        var alien = scn.GetLSCard("alien");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, velken);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), seeker, alien);

        assertTrue(scn.game().getModifiersQuerying().hasFlagActive(
                scn.game().getGameState(), ModifierFlag.SEEKERS_MAY_IGNORE_TARGETS, scn.DS));

        scn.MoveCardsToTopOfDSLostPile(velken);
        assertFalse(scn.IsCardActive(velken));
        assertFalse(scn.game().getModifiersQuerying().hasFlagActive(
                scn.game().getGameState(), ModifierFlag.SEEKERS_MAY_IGNORE_TARGETS, scn.DS));
    }





    private boolean TakeIfAvailable(VirtualTableScenario scn, String player, String needle) {
        if (!scn.AnyDecisionsAvailable(player)) {
            return false;
        }
        java.util.List<String> actions = scn.GetADParamAsList(player, "actionText");
        if (actions != null && actions.stream().anyMatch(
                a -> a != null && a.toLowerCase().contains(needle.toLowerCase()))) {
            scn.ChooseAction(player, needle);
            return true;
        }
        return false;
    }

    /** Pass control so START_OF_DEPLOY required responses offer Ignore all, then take it. */
    private void IgnoreAllViaTableChanged(VirtualTableScenario scn) {
        boolean taken = false;
        for (int i = 0; i < 40 && !taken; i++) {
            if (TakeIfAvailable(scn, scn.DS, "Ignore all")) {
                taken = true;
                break;
            }
            var decision = scn.GetCurrentDecision();
            if (decision == null || decision.getText() == null) {
                break;
            }
            String lower = decision.getText().toLowerCase();
            if (lower.contains("optional")) {
                scn.PassResponses("optional");
            } else if (lower.contains("required")) {
                break;
            } else if (lower.contains("action")) {
                String decider = scn.GetDecidingPlayer();
                if (decider != null) {
                    scn.PlayerPass(decider);
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        assertTrue("Ignore all potential targets should have been offered", taken);
        if (scn.GetCurrentDecision() != null && scn.GetCurrentDecision().getText() != null
                && scn.GetCurrentDecision().getText().toLowerCase().contains("optional")) {
            scn.PassResponses("optional");
        }
    }

    @Test
    public void VelkenSeekerIgnoreAllThenStopIgnoringImmediatelyMakesTargetLost() {
        var scn = GetScenario();

        var velken = scn.GetDSCard("velken");
        var seeker = scn.GetDSCard("hanSeeker");
        var alien = scn.GetLSCard("alien");
        var farm = scn.GetLSCard("farm");
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(dsSite, velken);
        scn.MoveCardsToLocation(farm, seeker);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToLocation(farm, alien);

        IgnoreAllViaTableChanged(scn);
        assertTrue(scn.DSCardActionAvailable(seeker, "Stop ignoring potential targets")
                || scn.DSActionAvailable("Stop ignoring potential targets"));

        scn.DSUseCardAction(seeker, "Stop ignoring potential targets");
        assertTrue("Stop ignoring must immediately require choosing a target",
                scn.DSHasCardChoiceAvailable(alien)
                        || (scn.GetCurrentDecision() != null && scn.GetCurrentDecision().getText() != null
                        && scn.GetCurrentDecision().getText().toLowerCase().contains("make lost")));
        if (scn.DSHasCardChoiceAvailable(alien)) {
            scn.DSChooseCard(alien);
        }
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, alien);
        assertInZone(Zone.LOST_PILE, seeker);
    }

    @Test
    public void VelkenSeekerIgnoreListClearsWhenSeekerRelocatedAwayFromTarget() {
        var scn = GetScenario();

        var velken = scn.GetDSCard("velken");
        var seeker = scn.GetDSCard("hanSeeker");
        var alien = scn.GetLSCard("alien");
        var farm = scn.GetLSCard("farm");
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(dsSite, velken);
        scn.MoveCardsToLocation(farm, seeker);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToLocation(farm, alien);

        IgnoreAllViaTableChanged(scn);
        // Han Seeker would target Velken (alien ability < 3) at dsSite; leave seeker at an empty site
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), seeker);
        scn.SkipToPhase(Phase.MOVE);
        assertFalse(scn.DSCardActionAvailable(seeker, "Stop ignoring potential targets"));
        assertFalse(scn.DSActionAvailable("Stop ignoring potential targets"));
    }

    @Test
    public void VelkenSeekerNewTargetNotCoveredByExistingIgnoreList() {
        var scn = GetScenario();

        var velken = scn.GetDSCard("velken");
        var seeker = scn.GetDSCard("hanSeeker");
        var alien = scn.GetLSCard("alien");
        var alien2 = scn.GetLSCard("alien2");
        var farm = scn.GetLSCard("farm");
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(dsSite, velken);
        scn.MoveCardsToLocation(farm, seeker);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToLocation(farm, alien);

        IgnoreAllViaTableChanged(scn);
        scn.MoveCardsToLocation(farm, alien2);

        boolean sawNewTarget = false;
        for (int i = 0; i < 40 && !sawNewTarget; i++) {
            java.util.List<String> actions = scn.AnyDecisionsAvailable(scn.DS)
                    ? scn.GetADParamAsList(scn.DS, "actionText") : null;
            if (actions != null && actions.stream().anyMatch(a -> a != null
                    && (a.toLowerCase().contains("ignore all")
                    || a.toLowerCase().contains("make a character lost")))) {
                sawNewTarget = true;
                break;
            }
            if (scn.AnyDecisionsAvailable(scn.DS) && scn.DSHasCardChoiceAvailable(alien2)) {
                sawNewTarget = true;
                break;
            }
            var decision = scn.GetCurrentDecision();
            if (decision == null || decision.getText() == null) {
                break;
            }
            String lower = decision.getText().toLowerCase();
            if (lower.contains("optional")) {
                scn.PassResponses("optional");
            } else if (lower.contains("required")) {
                break;
            } else if (lower.contains("action")) {
                String decider = scn.GetDecidingPlayer();
                if (decider != null) {
                    scn.PlayerPass(decider);
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        assertTrue("A new eligible target must be offered after Ignore all", sawNewTarget);
        assertAtLocation(farm, alien);
    }

}
