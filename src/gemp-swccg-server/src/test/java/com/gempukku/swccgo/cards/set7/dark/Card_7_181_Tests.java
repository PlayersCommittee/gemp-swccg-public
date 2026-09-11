package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * VHD tests for Special Edition 7_181 IM4-099 (Eyeemmfour).
 */
public class Card_7_181_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("wolfman", "1_30");
                    put("rebel", "1_28");
                }},
                new HashMap<>() {{
                    put("im4", "7_181");
                    put("t1", "1_194");
                    put("t2", "1_194");
                    put("t3", "1_194");
                    put("t4", "1_194");
                    put("t5", "1_194");
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
    public void EyeemmfourStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("im4").getBlueprint();

        assertEquals("IM4-099 (Eyeemmfour)", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(0, card.getPower(), scn.epsilon);
        assertEquals(0, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getManeuver(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.PATROL);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SPECIAL_EDITION);
            add(Icon.DROID);
        }});
        assertEquals(ExpansionSet.SPECIAL_EDITION, card.getExpansionSet());
        assertEquals(Rarity.F, card.getRarity());
    }

    @Test
    public void EyeemmfourMayDeployTroopersAsReactToOpponentForceDrainAtSameSite() {
        var scn = GetScenario();
        var wolfman = scn.GetLSCard("wolfman");
        var im4 = scn.GetDSCard("im4");
        var t1 = scn.GetDSCard("t1");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, wolfman, im4);
        scn.MoveCardsToDSHand(t1);
        scn.DSActivateForceCheat(2);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(site));
        scn.LSForceDrainAt(site);

        assertTrue(scn.DSCardActionAvailable(im4, "Deploy a trooper"));
        scn.DSUseCardAction(im4, "Deploy a trooper");
        if (scn.DSDecisionAvailable("Choose card") || scn.DSDecisionAvailable("Choose trooper")) {
            scn.DSChooseCard(t1);
        }
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(site, wolfman, im4, t1));
    }

    @Test
    public void EyeemmfourMayNotDeployAsReactOutsideForceDrain() {
        var scn = GetScenario();
        var wolfman = scn.GetLSCard("wolfman");
        var im4 = scn.GetDSCard("im4");
        var t1 = scn.GetDSCard("t1");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, wolfman, im4);
        scn.MoveCardsToDSHand(t1);
        scn.DSActivateForceCheat(2);

        // Deploy phase is not a Force drain — react deploy must be unavailable.
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(im4, "Deploy a trooper"));
    }


    @Test
    public void EyeemmfourRebelsDeployPlusTwoToSameSiteWhenPresentWithTrooper() {
        var scn = GetScenario();
        var rebel = scn.GetLSCard("rebel");
        var im4 = scn.GetDSCard("im4");
        var storm = scn.GetDSFiller(1);
        var site = scn.GetLSStartingLocation();
        var other = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, im4, storm);
        scn.MoveCardsToLSHand(rebel);

        float base = rebel.getBlueprint().getDeployCost();
        float atSite = scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), null, rebel, site, false, null, false, 0, null, false);
        float atOther = scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), null, rebel, other, false, null, false, 0, null, false);

        assertEquals(base + 2, atSite, scn.epsilon);
        assertEquals(base, atOther, scn.epsilon);
    }

    @Test
    public void EyeemmfourPassivesRequirePresentWithYourTrooper() {
        var scn = GetScenario();
        var rebel = scn.GetLSCard("rebel");
        var im4 = scn.GetDSCard("im4");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        // IM4 alone — Rebel deploy +2 off
        scn.MoveCardsToLocation(site, im4);
        scn.MoveCardsToLSHand(rebel);

        float base = rebel.getBlueprint().getDeployCost();
        float atSite = scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), null, rebel, site, false, null, false, 0, null, false);
        assertEquals(base, atSite, scn.epsilon);
    }

    @Test
    public void EyeemmfourPresentWithTrooperEnablesReactDenialPassives() {
        // MayNotReactTo/From share PresentWithCondition(your trooper) with Rebel deploy +2.
        var scn = GetScenario();
        var rebel = scn.GetLSCard("rebel");
        var im4 = scn.GetDSCard("im4");
        var storm = scn.GetDSFiller(1);
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, im4); // alone: +2 off
        float base = rebel.getBlueprint().getDeployCost();
        float alone = scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), null, rebel, site, false, null, false, 0, null, false);
        assertEquals(base, alone, scn.epsilon);

        scn.MoveCardsToLocation(site, storm); // now present with trooper
        float withTrooper = scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), null, rebel, site, false, null, false, 0, null, false);
        assertEquals(base + 2, withTrooper, scn.epsilon);
    }
}
