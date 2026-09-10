package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.InitiateAttackNonCreatureAction;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Yaggle Gakkle (3_142). Issue #97.
 * Ferocity bonus is card-local (AddUntilEndOfGameModifierEffect + FerocityModifier);
 * no shared attack-react / CalculateFerocity hook widening.
 */
public class Card_3_142_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tauntaun", "3_70");
                    put("tauntaun2", "3_70");
                    put("luke", "1_19");
                    put("saber", "3_71");
                    put("disarming", "3_33");
                    put("sense", "1_109");
                    put("worrt", "6_48");
                    put("northRidge", "3_62");
                    put("farm", "1_132");
                }},
                new HashMap<>() {{
                    put("yaggle", "3_142");
                    put("yaggle2", "3_142");
                    put("wampa", "3_93");
                    put("wampa2", "3_93");
                    put("bantha", "1_307");
                    put("ronto", "7_316");
                    put("wampaCave", "3_150");
                    put("skull", "7_264");
                    put("stopMotion", "3_135");
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

    protected VirtualTableScenario GetTatooineScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("worrt", "6_48");
                    put("farm", "1_132");
                }},
                new HashMap<>() {{
                    put("yaggle", "3_142");
                    put("bantha", "1_307");
                    put("ronto", "7_316");
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

    private void goToDSControl(VirtualTableScenario scn) {
        scn.DSActivateMaxForceAndPass();
        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions());
    }

    private void playYaggle(VirtualTableScenario scn, PhysicalCardImpl yaggle,
                            PhysicalCardImpl vehicle, PhysicalCardImpl creature) {
        assertTrue(scn.DSCardPlayAvailable(yaggle));
        scn.DSPlayCard(yaggle);
        assertTrue(scn.DSHasCardChoiceAvailable(vehicle));
        scn.DSChooseCard(vehicle);
        assertTrue(scn.DSHasCardChoiceAvailable(creature));
        scn.DSChooseCard(creature);
        // Only pass card-play responses so later windows (destiny, jump-off) stay intact
        scn.PassCardPlayResponses();
        scn.PassForceUseResponses();
    }

    @Test
    public void YaggleGakkleStatsAndKeywordsAreCorrect() {
        /**
         * Title: Yaggle Gakkle
         * Uniqueness: Unrestricted
         * Side: Dark
         * Type: Interrupt
         * Subtype: Used
         * Destiny: 6
         * Icons: Interrupt, Hoth
         * Game Text: Target a creature vehicle at same site as a creature. If ferocity > target's maneuver + landspeed,
         *      creature vehicle is eaten, cumulatively adding 2 to creature's ferocity.
         * Lore: Steady. Hey! Steady girl. Hey, what's the matter? You smell something?
         * Set: Hoth
         * Rarity: R2
         */

        var scn = GetScenario();
        var card = scn.GetDSCard("yaggle").getBlueprint();

        assertEquals(Title.Yaggle_Gakkle, card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.HOTH);
        }});
        assertEquals(ExpansionSet.HOTH, card.getExpansionSet());
        assertEquals(Rarity.R2, card.getRarity());
        assertTrue(card.getGameText().contains("cumulatively adding 2"));
        assertTrue(card.getLore().contains("You smell something"));
    }

    @Test
    public void YaggleGakkleRequiresSameSitePlayRestriction() {
        var scn = GetScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var tauntaun = scn.GetLSCard("tauntaun");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");
        var ridge = scn.GetLSCard("northRidge");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle);
        scn.MoveLocationToTable(cave);
        scn.MoveLocationToTable(ridge);
        goToDSControl(scn);
        scn.MoveCardsToLocation(ridge, tauntaun);
        scn.MoveCardsToLocation(cave, wampa);
        goToDSControl(scn);
        assertFalse("Separated vehicle/creature must not allow play", scn.DSCardPlayAvailable(yaggle));
        // Positive same-site eligibility covered by eat/cumulative tests.
    }

    @Test
    public void YaggleGakkleCanTargetSelfOrOpponentCreatureAndVehicle() {
        var scn = GetScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var tauntaun = scn.GetLSCard("tauntaun");
        var bantha = scn.GetDSCard("bantha");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, tauntaun, bantha, wampa);
        goToDSControl(scn);
        assertTrue(scn.DSCardPlayAvailable(yaggle));
        scn.DSPlayCard(yaggle);
        assertTrue("LS creature vehicle targetable", scn.DSHasCardChoiceAvailable(tauntaun));
        assertTrue("DS creature vehicle targetable", scn.DSHasCardChoiceAvailable(bantha));
        scn.DSChooseCard(bantha);
        scn.DSChooseCard(wampa);
        scn.PassAllResponses();
    }

    @Test
    public void YaggleGakkleMultipleChoicesAmongVehiclesAndCreatures() {
        var scn = GetScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var tauntaun = scn.GetLSCard("tauntaun");
        var tauntaun2 = scn.GetLSCard("tauntaun2");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, tauntaun, tauntaun2, wampa);
        goToDSControl(scn);
        scn.DSPlayCard(yaggle);
        assertTrue(scn.DSHasCardChoiceAvailable(tauntaun));
        assertTrue(scn.DSHasCardChoiceAvailable(tauntaun2));
        scn.DSChooseCard(tauntaun2);
        scn.DSChooseCard(wampa);
        scn.PassAllResponses();
    }

    @Test
    public void YaggleGakkleRejectsCreatureVehicleWithoutManeuver() {
        var scn = GetScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var ronto = scn.GetDSCard("ronto");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, ronto, wampa);
        goToDSControl(scn);
        assertFalse(scn.DSCardPlayAvailable(yaggle));
    }

    @Test
    public void YaggleGakkleFailsWhenFerocityEqualsManeuverPlusLandspeed() {
        // Tauntaun man2+ls2=4; Wampa 3+destiny1 => 4 equal => fail
        var scn = GetScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var tauntaun = scn.GetLSCard("tauntaun");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, tauntaun, wampa);
        goToDSControl(scn);
        scn.PrepareDSDestiny(1);
        playYaggle(scn, yaggle, tauntaun, wampa);
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();

        assertEquals(Zone.AT_LOCATION, tauntaun.getZone());
        assertEquals(3f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, 0f), scn.epsilon);
    }

    @Test
    public void YaggleGakkleEatsWhenFerocityGreaterAndAddsCumulativeFerocity() {
        // Tauntaun threshold 4; Wampa destiny 2 => ferocity 5 > 4
        var scn = GetScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var tauntaun = scn.GetLSCard("tauntaun");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, tauntaun, wampa);
        goToDSControl(scn);
        scn.PrepareDSDestiny(2);
        playYaggle(scn, yaggle, tauntaun, wampa);
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();

        assertEquals(Zone.TOP_OF_LOST_PILE, tauntaun.getZone());
        assertEquals(5f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, 0f), scn.epsilon);
    }

    @Test
    public void YaggleGakkleCumulativeIncreasesStackAcrossMultipleCopies() {
        var scn = GetScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var yaggle2 = scn.GetDSCard("yaggle2");
        var tauntaun = scn.GetLSCard("tauntaun");
        var tauntaun2 = scn.GetLSCard("tauntaun2");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle, yaggle2);
        scn.MoveLocationToTable(cave);
        // One vehicle at a time so auto creature attacks during turn skip cannot remove the second target
        scn.MoveCardsToLocation(cave, tauntaun, wampa);
        goToDSControl(scn);
        scn.PrepareDSDestiny(2);
        playYaggle(scn, yaggle, tauntaun, wampa);
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();
        assertEquals(Zone.TOP_OF_LOST_PILE, tauntaun.getZone());
        assertEquals(5f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, 0f), scn.epsilon);

        // Next DS turn: place second vehicle before CONTROL decision is built
        scn.SkipToDSTurn();
        scn.MoveCardsToLocation(cave, tauntaun2);
        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions());
        scn.PrepareDSDestiny(2);
        playYaggle(scn, yaggle2, tauntaun2, wampa);
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();
        assertEquals(Zone.TOP_OF_LOST_PILE, tauntaun2.getZone());
        assertEquals(7f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, 0f), scn.epsilon);
    }

    @Test
    public void YaggleGakkleFerocityBonusEndsWhenCreatureIsLost() {
        var scn = GetScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var tauntaun = scn.GetLSCard("tauntaun");
        var wampa = scn.GetDSCard("wampa");
        var wampa2 = scn.GetDSCard("wampa2");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, tauntaun, wampa);
        goToDSControl(scn);
        scn.PrepareDSDestiny(2);
        playYaggle(scn, yaggle, tauntaun, wampa);
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();
        assertEquals(5f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, 0f), scn.epsilon);

        scn.MoveCardsToTopOfOwnLostPile(wampa);
        assertEquals(Zone.TOP_OF_LOST_PILE, wampa.getZone());
        // Replacement copy must not inherit the cumulative eat bonus
        scn.MoveCardsToLocation(cave, wampa2);
        assertEquals(3f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa2, 0f), scn.epsilon);
        assertTrue(scn.game().getModifiersQuerying()
                .getModifiersAffectingCard(scn.gameState(), ModifierType.FEROCITY, wampa2).isEmpty());
    }

    @Test
    public void YaggleGakkleRidingCharacterSurvivesImmediateDisembark() {
        var scn = GetScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var tauntaun = scn.GetLSCard("tauntaun");
        var luke = scn.GetLSCard("luke");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, tauntaun, wampa, luke);
        scn.BoardAsPassenger(tauntaun, luke);
        assertTrue(scn.IsAboardAsPassenger(tauntaun, luke));
        goToDSControl(scn);
        scn.PrepareDSDestiny(2);
        playYaggle(scn, yaggle, tauntaun, wampa);
        scn.PassDestinyDrawResponses();

        // EatenResult optional window first (no actions); then About-to-be-lost Jump off
        if (scn.LSDecisionAvailable("EATEN") || scn.DSDecisionAvailable("EATEN")) {
            scn.PassResponses("EATEN");
        }
        assertTrue(scn.LSDecisionAvailable("Optional") || scn.LSDecisionAvailable("Jump") || scn.LSDecisionAvailable("ABOUT_TO"));
        scn.LSChooseAction("Jump off");
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("Lost Pile") || scn.DSDecisionAvailable("Lost Pile")) {
            scn.PassResponses("Lost Pile");
        }
        scn.PassAllResponses();

        assertEquals(Zone.TOP_OF_LOST_PILE, tauntaun.getZone());
        assertEquals(Zone.AT_LOCATION, luke.getZone());
        assertEquals(cave, luke.getAtLocation());
    }

    @Test
    public void YaggleGakkleModifiedFerocityAppliesForAttackDefenseCalculation() {
        var scn = GetScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var tauntaun = scn.GetLSCard("tauntaun");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, tauntaun, wampa);
        goToDSControl(scn);
        scn.PrepareDSDestiny(2);
        playYaggle(scn, yaggle, tauntaun, wampa);
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();

        // 3 base + destiny 1 + cumulative 2 = 6
        assertEquals(6f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, 1f), scn.epsilon);
    }

    @Test
    public void YaggleGakkleEqualThresholdOnFixedFerocityCreatureFails() {
        // Bantha man2+ls1=3; Worrt ferocity 3 => equal => fail
        var scn = GetTatooineScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var bantha = scn.GetDSCard("bantha");
        var worrt = scn.GetLSCard("worrt");
        var farm = scn.GetLSCard("farm");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle);
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(farm, bantha, worrt);
        goToDSControl(scn);
        goToDSControl(scn);
        assertTrue(scn.DSCardPlayAvailable(yaggle));
        playYaggle(scn, yaggle, bantha, worrt);
        scn.PassAllResponses();

        assertEquals(Zone.AT_LOCATION, bantha.getZone());
        assertEquals(3f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), worrt, null), scn.epsilon);
    }

    @Test
    public void YaggleGakkleTauntaunSkullCanUploadFromReserveDeck() {
        var scn = GetScenario();
        var skull = scn.GetDSCard("skull");
        var yaggle = scn.GetDSCard("yaggle");
        var stopMotion = scn.GetDSCard("stopMotion");

        scn.StartGame();
        scn.MoveCardsToDSHand(skull);
        goToDSControl(scn);
        scn.MoveCardsToTopOfReserveDeck(scn.DS, yaggle, stopMotion);

        assertTrue(scn.DSCardPlayAvailable(skull));
        scn.DSPlayCard(skull, "Take card into hand from Reserve Deck");
        scn.PassAllResponses();
        if (scn.DSHasCardChoiceAvailable(yaggle)) {
            scn.DSChooseCard(yaggle);
            scn.PassAllResponses();
        }
        assertEquals(Zone.HAND, yaggle.getZone());
    }


    private PlayCardAction playAction(VirtualTableScenario scn, PhysicalCardImpl card) {
        return card.getBlueprint().getPlayCardAction(
                scn.LS, scn.game(), card, card, false, 0, null, null, null, null, null, false, 0, Filters.any, null);
    }

    private void playDisarmingOnto(VirtualTableScenario scn, PhysicalCardImpl disarming, PhysicalCardImpl target) {
        scn.PassAllResponses();
        var deploy = playAction(scn, disarming);
        if (deploy != null && scn.AwaitingLSDeployPhaseActions()) {
            try {
                scn.carryOutEffectInPhaseActionByPlayer(scn.LS, deploy);
                if (scn.LSGetDecision() != null && scn.LSHasCardChoicesAvailable(target)) {
                    scn.LSChooseCard(target);
                }
                scn.PassAllResponses();
            } catch (RuntimeException ignored) {
            }
        }
        if (disarming.getAttachedTo() == null) {
            scn.AttachCardsTo(target, disarming);
        }
        assertSame(target, disarming.getAttachedTo());
    }

    @Test
    public void YaggleGakkleMultipleCopiesPlayableSameControlPhase() {
        // Doc: can play multiple copies in one turn (non-unique)
        var scn = GetScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var yaggle2 = scn.GetDSCard("yaggle2");
        var tauntaun = scn.GetLSCard("tauntaun");
        var tauntaun2 = scn.GetLSCard("tauntaun2");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle, yaggle2);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, tauntaun, tauntaun2, wampa);
        goToDSControl(scn);

        assertTrue("Both copies available same control phase", scn.DSCardPlayAvailable(yaggle));
        assertTrue("Both copies available same control phase", scn.DSCardPlayAvailable(yaggle2));

        scn.PrepareDSDestiny(2);
        playYaggle(scn, yaggle, tauntaun, wampa);
        scn.PassDestinyDrawResponses();
        if (scn.LSDecisionAvailable("EATEN") || scn.DSDecisionAvailable("EATEN")) {
            scn.PassResponses("EATEN");
        }
        // Drain response windows without skipping the rest of the control phase
        for (int i = 0; i < 30 && !scn.AwaitingDSControlPhaseActions(); i++) {
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
            } else if (scn.DSGetDecision() != null && !scn.AwaitingDSControlPhaseActions()) {
                // Avoid DSPass when control actions are already up
                String text = scn.DSGetDecision().getText() != null ? scn.DSGetDecision().getText() : "";
                if (text.toLowerCase().contains("control") && text.toLowerCase().contains("action")) {
                    break;
                }
                scn.DSPass();
            } else {
                break;
            }
        }
        assertEquals(Zone.TOP_OF_LOST_PILE, tauntaun.getZone());
        assertEquals(5f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, 0f), scn.epsilon);

        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertTrue(scn.DSCardPlayAvailable(yaggle2));
        scn.PrepareDSDestiny(2);
        playYaggle(scn, yaggle2, tauntaun2, wampa);
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();
        assertEquals(Zone.TOP_OF_LOST_PILE, tauntaun2.getZone());
        assertEquals(7f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, 0f), scn.epsilon);
    }

    @Test
    public void YaggleGakkleEatsOwnCreatureVehicleWithOwnCreature() {
        // DS interrupt + DS creature + DS creature vehicle ownership path
        var scn = GetScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var bantha = scn.GetDSCard("bantha");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, bantha, wampa);
        goToDSControl(scn);
        // Bantha man2+ls1=3; Wampa destiny 1 => ferocity 4 > 3
        scn.PrepareDSDestiny(1);
        playYaggle(scn, yaggle, bantha, wampa);
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();

        assertEquals(Zone.TOP_OF_LOST_PILE, bantha.getZone());
        assertEquals(5f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, 0f), scn.epsilon);
    }

    @Test
    public void YaggleGakkleFailsWithLSCreatureWhenFerocityNotGreater() {
        // DS interrupt, LS creature (Worrt ferocity 3) vs Bantha threshold 3 => equal fail
        var scn = GetTatooineScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var bantha = scn.GetDSCard("bantha");
        var worrt = scn.GetLSCard("worrt");
        var farm = scn.GetLSCard("farm");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle);
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(farm, bantha, worrt);
        goToDSControl(scn);
        goToDSControl(scn);
        playYaggle(scn, yaggle, bantha, worrt);
        scn.PassAllResponses();

        assertEquals(Zone.AT_LOCATION, bantha.getZone());
        assertEquals(3f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), worrt, null), scn.epsilon);
    }

    @Test
    public void YaggleGakkleDisarmingCreatureSubtractionCanPreventEat() {
        // Shared DrawFerocityDestinyEffect: ferocity destinies then Disarming subtraction
        // Wampa destiny 2 => 5; LS subtract 1 => 4; Tauntaun threshold 4 => equal fail
        var scn = GetScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var tauntaun = scn.GetLSCard("tauntaun");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var disarming = scn.GetLSCard("disarming");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle);
        scn.MoveCardsToLSHand(disarming);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, luke, tauntaun);
        scn.AttachCardsTo(luke, saber);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToLocation(cave, wampa);
        playDisarmingOnto(scn, disarming, wampa);

        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions());
        scn.PrepareDSDestiny(2);
        scn.PrepareLSDestiny(1);
        playYaggle(scn, yaggle, tauntaun, wampa);
        scn.PassDestinyDrawResponses();
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();

        assertEquals(Zone.AT_LOCATION, tauntaun.getZone());
        assertEquals(3f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, 0f), scn.epsilon);
    }

    @Test
    public void YaggleGakkleDisarmingCreatureSubtractionStillAllowsEatAndStacksPlus2() {
        // Wampa destiny 3 => 6; LS subtract 1 => 5; Tauntaun threshold 4 => eat; cumulative +2 remains
        var scn = GetScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var tauntaun = scn.GetLSCard("tauntaun");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var disarming = scn.GetLSCard("disarming");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle);
        scn.MoveCardsToLSHand(disarming);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, luke, tauntaun);
        scn.AttachCardsTo(luke, saber);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToLocation(cave, wampa);
        playDisarmingOnto(scn, disarming, wampa);

        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions());
        scn.PrepareDSDestiny(3);
        scn.PrepareLSDestiny(1);
        playYaggle(scn, yaggle, tauntaun, wampa);
        scn.PassDestinyDrawResponses();
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();

        assertEquals(Zone.TOP_OF_LOST_PILE, tauntaun.getZone());
        // Card-local +2 ferocity stacks with Disarming still attached for later CalculateFerocity draws
        assertEquals(5f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, 0f), scn.epsilon);
        assertEquals(1, scn.game().getModifiersQuerying()
                .getModifiersAffectingCard(scn.gameState(), ModifierType.SUBTRACT_DESTINY_FROM_FEROCITY, wampa).size());
        assertFalse(scn.game().getModifiersQuerying()
                .getModifiersAffectingCard(scn.gameState(), ModifierType.FEROCITY, wampa).isEmpty());
    }

    @Test
    public void YaggleGakkleZeroOrNegativeEffectiveFerocityFailsThreshold() {
        // Heavy Disarming subtraction drives effective ferocity at-or-below threshold
        // Wampa destiny 1 => 4; LS subtract 5 => -1; Tauntaun threshold 4 => fail
        var scn = GetScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var tauntaun = scn.GetLSCard("tauntaun");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var disarming = scn.GetLSCard("disarming");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle);
        scn.MoveCardsToLSHand(disarming);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, luke, tauntaun);
        scn.AttachCardsTo(luke, saber);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToLocation(cave, wampa);
        playDisarmingOnto(scn, disarming, wampa);

        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions());
        scn.PrepareDSDestiny(1);
        scn.PrepareLSDestiny(5);
        playYaggle(scn, yaggle, tauntaun, wampa);
        scn.PassDestinyDrawResponses();
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();

        assertEquals(Zone.AT_LOCATION, tauntaun.getZone());
        assertEquals(3f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, 0f), scn.epsilon);
    }

    @Test
    public void YaggleGakkleSenseCancelsInterruptBeforeEat() {
        var scn = GetScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var tauntaun = scn.GetLSCard("tauntaun");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");
        var luke = scn.GetLSCard("luke");
        var sense = scn.GetLSCard("sense");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle);
        scn.MoveCardsToLSHand(sense);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, tauntaun, wampa, luke);
        goToDSControl(scn);

        assertTrue(scn.DSCardPlayAvailable(yaggle));
        scn.DSPlayCard(yaggle);
        assertTrue(scn.DSHasCardChoiceAvailable(tauntaun));
        scn.DSChooseCard(tauntaun);
        assertTrue(scn.DSHasCardChoiceAvailable(wampa));
        scn.DSChooseCard(wampa);

        // Sense as cancel response while interrupt is being played
        assertTrue(scn.LSCardPlayAvailable(sense) || scn.LSDecisionAvailable("Optional") || scn.LSGetDecision() != null);
        if (scn.LSCardPlayAvailable(sense)) {
            scn.LSPlayCard(sense);
        } else {
            scn.LSChooseAction("cancel");
            if (scn.LSHasCardChoicesAvailable(sense)) {
                scn.LSChooseCard(sense);
            }
        }
        if (scn.LSHasCardChoicesAvailable(luke)) {
            scn.LSChooseCard(luke);
        }
        scn.PrepareLSDestiny(1);
        scn.PassAllResponses();
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();

        assertEquals(Zone.AT_LOCATION, tauntaun.getZone());
        assertEquals(3f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, 0f), scn.epsilon);
        assertTrue(yaggle.getZone() == Zone.HAND || yaggle.getZone() == Zone.TOP_OF_LOST_PILE
                || yaggle.getZone() == Zone.USED_PILE || yaggle.getZone() == Zone.TOP_OF_USED_PILE
                || !scn.DSCardPlayAvailable(yaggle));
    }

    @Test
    public void YaggleGakkleFerocityBonusClearedByRevertBeforeEat() {
        var scn = GetScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var tauntaun = scn.GetLSCard("tauntaun");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, tauntaun, wampa);
        goToDSControl(scn);

        scn.PrepareDSDestiny(2);
        playYaggle(scn, yaggle, tauntaun, wampa);
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();
        assertEquals(Zone.TOP_OF_LOST_PILE, tauntaun.getZone());
        assertEquals(5f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, 0f), scn.epsilon);

        // Revert to start of this DS control phase: eat/bonus after that point are removed
        scn.IssueRevert("Start of Dark Side Player's control phase #1");
        yaggle = scn.GetPostRevertCard(yaggle);
        tauntaun = scn.GetPostRevertCard(tauntaun);
        wampa = scn.GetPostRevertCard(wampa);
        cave = scn.GetPostRevertCard(cave);

        assertEquals(Zone.AT_LOCATION, tauntaun.getZone());
        assertEquals(3f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, 0f), scn.epsilon);
    }

    @Test
    public void YaggleGakkleAttackUsesCumulativeFerocityAfterEat() {
        // Timing vs attack: cumulative +2 applies when ferocity is calculated during attack
        var scn = GetScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var tauntaun = scn.GetLSCard("tauntaun");
        var luke = scn.GetLSCard("luke");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, tauntaun, wampa, luke);
        goToDSControl(scn);
        scn.PrepareDSDestiny(2);
        playYaggle(scn, yaggle, tauntaun, wampa);
        scn.PassDestinyDrawResponses();
        if (scn.LSDecisionAvailable("EATEN") || scn.DSDecisionAvailable("EATEN")) {
            scn.PassResponses("EATEN");
        }
        for (int i = 0; i < 30 && !scn.AwaitingDSControlPhaseActions(); i++) {
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
            } else if (scn.DSGetDecision() != null && !scn.AwaitingDSControlPhaseActions()) {
                scn.DSPass();
            } else {
                break;
            }
        }
        assertEquals(5f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, 0f), scn.epsilon);
        assertEquals(Zone.AT_LOCATION, luke.getZone());

        // Same DS turn battle phase avoids SkipToDSTurn auto-attack ambiguity after an eat
        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.AwaitingDSBattlePhaseActions());
        scn.DSActivateForceCheat(3);
        scn.PrepareDSDestiny(1);
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, new InitiateAttackNonCreatureAction(wampa));
        if (scn.DSGetDecision() != null && scn.DSHasCardChoicesAvailable(luke)) {
            scn.DSChooseCard(luke);
        }
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("weapons segment") || scn.LSDecisionAvailable("weapons segment")
                || scn.DSDecisionAvailable("Choose weapons") || scn.LSDecisionAvailable("Choose weapons")) {
            scn.PassWeaponsSegmentActions();
        }
        scn.PassAllResponses();
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();

        var attackState = scn.gameState().getAttackState();
        assertNotNull(attackState);
        assertTrue(attackState.isAttackStarted());
        Float total = attackState.getFerocityDestinyTotal(wampa);
        assertNotNull(total);
        float ferocity = scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, total);
        assertEquals(3f + total + 2f, ferocity, scn.epsilon);
    }

    @Test
    public void YaggleGakkleEatenStateLeavesVehicleLostAndInterruptUsed() {
        // Cancel/eaten-state bookkeeping after successful resolution
        var scn = GetScenario();
        var yaggle = scn.GetDSCard("yaggle");
        var tauntaun = scn.GetLSCard("tauntaun");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToDSHand(yaggle);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, tauntaun, wampa);
        goToDSControl(scn);
        scn.PrepareDSDestiny(2);
        playYaggle(scn, yaggle, tauntaun, wampa);
        scn.PassDestinyDrawResponses();
        if (scn.LSDecisionAvailable("EATEN") || scn.DSDecisionAvailable("EATEN")) {
            scn.PassResponses("EATEN");
        }
        scn.PassAllResponses();

        assertEquals(Zone.TOP_OF_LOST_PILE, tauntaun.getZone());
        assertTrue(yaggle.getZone() == Zone.TOP_OF_USED_PILE || yaggle.getZone() == Zone.USED_PILE);
        assertEquals(5f, scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, 0f), scn.epsilon);
    }
}
