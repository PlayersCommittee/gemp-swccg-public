package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_6_128_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("alien", "1_012"); // Jawa - alien ability typically low
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

        // Free to Velken's controlled site
        float freeCost = scn.game().getModifiersQuerying().getDeployCost(
                scn.game().getGameState(), seeker, seeker, site, false, null, false, 0, null, false);
        assertEquals(0f, freeCost, scn.epsilon);

        // Still costs 1 when deploying to a different site Velken does not make free
        float otherCost = scn.game().getModifiersQuerying().getDeployCost(
                scn.game().getGameState(), seeker, seeker, lsSite, false, null, false, 0, null, false);
        assertEquals(1f, otherCost, scn.epsilon);
    }

    @Test
    public void VelkenTezeriAllowsSeekersToMoveForFreeWhenAtControlledSite() {
        var scn = GetScenario();

        var velken = scn.GetDSCard("velken");
        var seeker = scn.GetDSCard("hanSeeker");
        var farm = scn.GetLSCard("farm");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(site, velken);
        scn.MoveCardsToLocation(farm, seeker);

        float cost = scn.game().getModifiersQuerying().getMoveUsingLandspeedCost(
                scn.game().getGameState(), seeker, farm, site, false, 0);
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
        scn.MoveCardsToLocation(site, velken, seeker, alien);

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
        scn.MoveCardsToLocation(site, velken, seeker, alien);

        assertTrue(scn.game().getModifiersQuerying().hasFlagActive(
                scn.game().getGameState(), ModifierFlag.SEEKERS_MAY_IGNORE_TARGETS, scn.DS));

        scn.MoveCardsToTopOfDSLostPile(velken);
        assertFalse(scn.IsCardActive(velken));
        assertFalse(scn.game().getModifiersQuerying().hasFlagActive(
                scn.game().getGameState(), ModifierFlag.SEEKERS_MAY_IGNORE_TARGETS, scn.DS));
    }
}
