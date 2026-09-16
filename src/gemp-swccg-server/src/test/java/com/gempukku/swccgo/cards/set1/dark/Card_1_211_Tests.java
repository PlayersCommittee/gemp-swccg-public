package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1_211_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("rack", "1_211");
                    put("saber", "214_3");
                    put("gideon", "223_18");
                    put("rifle", "1_312");
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
    public void BlasterRackStatsAndKeywordsAreCorrect() {
        /**
         * Title: Blaster Rack
         * Uniqueness: Unrestricted
         * Side: Dark
         * Type: Effect
         * Subtype: Normal
         * Destiny: 3
         * Icons: Effect
         * Game Text: Deploy on your side of table. At any time, you may transfer one of your character weapons from
         *         any site to the Blaster Rack. During your deploy phase, weapon may be transferred to your character
         *         on table for an expenditure of Force equal to the weapon's deploy cost.
         * Set: Premiere
         * Rarity: U1
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("rack").getBlueprint();

        assertEquals(Title.Blaster_Rack, card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.PREMIERE, card.getExpansionSet());
        assertEquals(Rarity.U1, card.getRarity());
    }

    @Test
    public void BlasterRackTransferDoesNotCountAsDeployingDarksaber() {
        var scn = GetScenario();

        var rack = scn.GetDSCard("rack");
        var saber = scn.GetDSCard("saber");
        var gideon = scn.GetDSCard("gideon");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSSideOfTable(rack);
        scn.MoveCardsToLocation(site, gideon);
        scn.StackCardsOn(rack, saber);
        scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSReserveDeck());

        scn.DSActivateForceCheat(4);
        scn.SkipToPhase(Phase.DEPLOY);

        assertTrue(scn.DSCardActionAvailable(rack, "Transfer stacked weapon"));
        scn.DSUseCardAction(rack, "Transfer stacked weapon");
        chooseStackedOrTargetIfOffered(scn, saber);
        chooseStackedOrTargetIfOffered(scn, gideon);

        assertFalse(scn.DSDecisionAvailable("just deployed"));
        assertFalse(scn.DSDecisionAvailable("Take a card"));

        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(gideon, saber));
        assertEquals(Zone.ATTACHED, saber.getZone());
    }

    @Test
    public void BlasterRackMayTransferUniqueWeaponSameTurnItWasDeployed() {
        var scn = GetScenario();

        var rack = scn.GetDSCard("rack");
        var saber = scn.GetDSCard("saber");
        var gideon = scn.GetDSCard("gideon");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSSideOfTable(rack);
        scn.MoveCardsToLocation(site, gideon);
        scn.MoveCardsToDSHand(saber);

        scn.DSActivateForceCheat(4);
        scn.SkipToPhase(Phase.DEPLOY);

        assertTrue(scn.DSDeployAvailable(saber));
        scn.DSDeployCard(saber);
        scn.DSChooseCard(gideon);
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("Choose Deploy action or Pass")) {
            scn.LSPass();
        }
        assertTrue(scn.IsAttachedTo(gideon, saber));
        assertTrue(scn.AwaitingDSDeployPhaseActions());

        assertTrue(scn.DSCardActionAvailable(rack, "Transfer character weapon"));
        scn.DSUseCardAction(rack, "Transfer character weapon");
        chooseStackedOrTargetIfOffered(scn, saber);
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("Choose Deploy action or Pass")) {
            scn.LSPass();
        }
        assertEquals(Zone.STACKED, saber.getZone());

        assertTrue(scn.DSCardActionAvailable(rack, "Transfer stacked weapon"));
        scn.DSUseCardAction(rack, "Transfer stacked weapon");
        chooseStackedOrTargetIfOffered(scn, saber);
        chooseStackedOrTargetIfOffered(scn, gideon);
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(gideon, saber));
    }

    @Test
    public void BlasterRackTransferPaysWeaponDeployCost() {
        var scn = GetScenario();

        var rack = scn.GetDSCard("rack");
        var rifle = scn.GetDSCard("rifle");
        var trooper = scn.GetDSFiller(1);
        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSSideOfTable(rack);
        scn.MoveCardsToLocation(site, trooper);
        scn.StackCardsOn(rack, rifle);

        scn.DSActivateForceCheat(4);
        scn.SkipToPhase(Phase.DEPLOY);
        int forceBefore = scn.GetDSForcePileCount();

        assertTrue(scn.DSCardActionAvailable(rack, "Transfer stacked weapon"));
        scn.DSUseCardAction(rack, "Transfer stacked weapon");
        chooseStackedOrTargetIfOffered(scn, rifle);
        chooseStackedOrTargetIfOffered(scn, trooper);
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(trooper, rifle));
        assertEquals(forceBefore - 2, scn.GetDSForcePileCount());
    }

    private void chooseStackedOrTargetIfOffered(VirtualTableScenario scn, PhysicalCardImpl card) {
        if (scn.DSHasCardChoiceAvailable(card)) {
            scn.DSChooseCard(card);
        }
    }
}
