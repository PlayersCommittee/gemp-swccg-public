package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
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

public class Card_6_004_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("attark", "6_004");
                    put("mottiSeeker", "2_080");
                    put("farm", "1_132");
                }},
                new HashMap<>()
                {{
                    put("pilot", "1_185");
                    put("pilot2", "1_180"); // Imperial Pilot ability 2, unrestricted
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
    public void AttarkStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("attark").getBlueprint();

        assertEquals("Attark", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
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
        }});
        assertEquals(Species.HOOVER, card.getSpecies());
        assertEquals(ExpansionSet.JABBAS_PALACE, card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
    }

    @Test
    public void AttarkAllowsSeekersToDeployFreeAtControlledSite() {
        var scn = GetScenario();

        var attark = scn.GetLSCard("attark");
        var seeker = scn.GetLSCard("mottiSeeker");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, attark);
        scn.MoveCardsToLSHand(seeker);

        float freeCost = scn.game().getModifiersQuerying().getDeployCost(
                scn.game().getGameState(), seeker, seeker, site, false, null, false, 0, null, false);
        assertEquals(0f, freeCost, scn.epsilon);

        scn.SkipToLSTurn(Phase.DEPLOY);
        int forceBefore = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardPlayAvailable(seeker));
        scn.LSPlayCard(seeker);
        assertTrue(scn.LSHasCardChoiceAvailable(site));
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertAtLocation(site, seeker);
        assertEquals(forceBefore, scn.GetLSForcePileCount());
    }

    @Test
    public void AttarkDoesNotAllowSeekerDeployFreeAtSiteNotControlled() {
        var scn = GetScenario();

        var attark = scn.GetLSCard("attark");
        var seeker = scn.GetLSCard("mottiSeeker");
        var trooper = scn.GetDSCard("trooper");
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(dsSite, attark, trooper);

        float cost = scn.game().getModifiersQuerying().getDeployCost(
                scn.game().getGameState(), seeker, seeker, dsSite, false, null, false, 0, null, false);
        assertEquals(1f, cost, scn.epsilon);
    }

    @Test
    public void AttarkAllowsSeekersToMoveForFreeWhenAtControlledSite() {
        var scn = GetScenario();

        var attark = scn.GetLSCard("attark");
        var seeker = scn.GetLSCard("mottiSeeker");
        var farm = scn.GetLSCard("farm");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(site, attark);
        scn.MoveCardsToLocation(farm, seeker);

        float cost = scn.game().getModifiersQuerying().getMoveUsingLandspeedCost(
                scn.game().getGameState(), seeker, farm, site, false, 0);
        assertEquals(0f, cost, scn.epsilon);
    }

    @Test
    public void AttarkAllowsSeekerToIgnoreTargetsOwnerScoped() {
        var scn = GetScenario();

        var attark = scn.GetLSCard("attark");
        var seeker = scn.GetLSCard("mottiSeeker");
        var pilot = scn.GetDSCard("pilot");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, attark, seeker, pilot);

        assertTrue(scn.game().getModifiersQuerying().hasFlagActive(
                scn.game().getGameState(), ModifierFlag.SEEKERS_MAY_IGNORE_TARGETS, scn.LS));
        assertFalse(scn.game().getModifiersQuerying().hasFlagActive(
                scn.game().getGameState(), ModifierFlag.SEEKERS_MAY_IGNORE_TARGETS, scn.DS));
    }

    @Test
    public void AttarkLeavesTableStopsSeekerIgnoring() {
        var scn = GetScenario();

        var attark = scn.GetLSCard("attark");
        var seeker = scn.GetLSCard("mottiSeeker");
        var pilot = scn.GetDSCard("pilot");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, attark, seeker, pilot);

        assertTrue(scn.game().getModifiersQuerying().hasFlagActive(
                scn.game().getGameState(), ModifierFlag.SEEKERS_MAY_IGNORE_TARGETS, scn.LS));

        scn.MoveCardsToTopOfLSLostPile(attark);
        assertFalse(scn.IsCardActive(attark));
        assertFalse(scn.game().getModifiersQuerying().hasFlagActive(
                scn.game().getGameState(), ModifierFlag.SEEKERS_MAY_IGNORE_TARGETS, scn.LS));
    }





    @Test
    public void AttarkSeekerStopIgnoringClearsWhenIgnoredTargetNotPresent() {
        var scn = GetScenario();

        var attark = scn.GetLSCard("attark");
        var seeker = scn.GetLSCard("mottiSeeker");
        var pilot = scn.GetDSCard("pilot");
        var farm = scn.GetLSCard("farm");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(site, attark, seeker);
        scn.MoveCardsToLocation(farm, pilot);

        java.util.Set<String> ignored = new java.util.HashSet<>();
        ignored.add(String.valueOf(pilot.getCardId()));
        seeker.setWhileInPlayData(new com.gempukku.swccgo.game.state.WhileInPlayData(ignored));

        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(seeker, "Stop ignoring potential targets"));
        assertFalse(scn.LSActionAvailable("Stop ignoring potential targets"));
    }

    @Test
    public void AttarkSeekerIgnoreListRetainedWhileTargetRemainsPresent() {
        var scn = GetScenario();

        var attark = scn.GetLSCard("attark");
        var seeker = scn.GetLSCard("mottiSeeker");
        var pilot = scn.GetDSCard("pilot");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, attark, seeker, pilot);

        java.util.Set<String> ignored = new java.util.HashSet<>();
        ignored.add(String.valueOf(pilot.getCardId()));
        seeker.setWhileInPlayData(new com.gempukku.swccgo.game.state.WhileInPlayData(ignored));

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(seeker, "Stop ignoring potential targets")
                || scn.LSActionAvailable("Stop ignoring potential targets"));
    }

    @Test
    public void AttarkSeekerIgnoreListClearsWhenSeekerRelocatedAwayFromTarget() {
        var scn = GetScenario();

        var attark = scn.GetLSCard("attark");
        var seeker = scn.GetLSCard("mottiSeeker");
        var pilot = scn.GetDSCard("pilot");
        var farm = scn.GetLSCard("farm");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(site, attark, seeker, pilot);

        java.util.Set<String> ignored = new java.util.HashSet<>();
        ignored.add(String.valueOf(pilot.getCardId()));
        seeker.setWhileInPlayData(new com.gempukku.swccgo.game.state.WhileInPlayData(ignored));

        scn.MoveCardsToLocation(farm, seeker);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(seeker, "Stop ignoring potential targets"));
        assertFalse(scn.LSActionAvailable("Stop ignoring potential targets"));
    }

    @Test
    public void AttarkSeekerNewTargetNotCoveredByExistingIgnoreList() {
        var scn = GetScenario();

        var attark = scn.GetLSCard("attark");
        var seeker = scn.GetLSCard("mottiSeeker");
        var pilot = scn.GetDSCard("pilot");
        var pilot2 = scn.GetDSCard("pilot2");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, attark, seeker, pilot);

        java.util.Set<String> ignored = new java.util.HashSet<>();
        ignored.add(String.valueOf(pilot.getCardId()));
        seeker.setWhileInPlayData(new com.gempukku.swccgo.game.state.WhileInPlayData(ignored));

        scn.MoveCardsToLocation(site, pilot2);
        assertTrue(seeker.getWhileInPlayData().getTextValues().contains(String.valueOf(pilot.getCardId())));
        assertFalse(seeker.getWhileInPlayData().getTextValues().contains(String.valueOf(pilot2.getCardId())));
    }

}
