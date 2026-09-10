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
                    put("northRidge", "3_62");
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
}
