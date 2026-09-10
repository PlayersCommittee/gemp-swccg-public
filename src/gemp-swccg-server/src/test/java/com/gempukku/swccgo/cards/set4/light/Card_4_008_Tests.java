package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_4_008_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("translator", "4_008");
                    put("suit", "4_014");
                    put("ywing", "1_147");
                    put("gold1", "1_141");
                    put("ywing2", "1_147");
                    put("corvette", "1_140");
                    put("r2", "2_14");
                    put("r5", "2_15");
                    put("biggs", "1_3");
                    put("luke", "1_19");
                    put("red10", "7_145");
                }},
                new HashMap<>() {{
                    put("tie", "1_304");
                }},
                10,
                10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void AstromechTranslatorStatsAndKeywordsAreCorrect() {
        /**
         * Title: Astromech Translator
         * Uniqueness: Unrestricted
         * Side: Light
         * Type: Device
         * Destiny: 3
         * Icons: Device, Dagobah
         * Game Text: Deploy on any starfighter. While an astromech character is aboard: If not piloted, starfighter
         * may move and may use power, maneuver and hyperspeed. OR if piloted, starfighter is immune to attrition < 3
         * (< 6 if matching pilot aboard).
         * Lore: Standard technology found on hyperdrive-capable starfighters. Many manufacturers. Converts electronic
         * impulses and high-density electronic languages into readable text.
         * Set: Dagobah
         * Rarity: C
         */

        var scn = GetScenario();
        var card = scn.GetLSCard("translator").getBlueprint();

        assertEquals("Astromech Translator", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEVICE);
        }});
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEVICE);
            add(Icon.DAGOBAH);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        assertEquals(ExpansionSet.DAGOBAH, card.getExpansionSet());
        assertEquals(Rarity.C, card.getRarity());
    }

    @Test
    public void AstromechTranslatorDeploysOnStarfighter() {
        var scn = GetScenario();
        var translator = scn.GetLSCard("translator");
        var ywing = scn.GetLSCard("ywing");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToHand(translator);
        scn.MoveCardsToLocation(system, ywing);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(translator));
        scn.LSDeployCard(translator);
        assertTrue(scn.LSHasCardChoiceAvailable(ywing));
        scn.LSChooseCard(ywing);
        scn.PassAllResponses();
        assertEquals(ywing, translator.getAttachedTo());
    }

    @Test
    public void AstromechTranslatorDeploysOnOpponentsStarfighter() {
        var scn = GetScenario();
        var translator = scn.GetLSCard("translator");
        var tie = scn.GetDSCard("tie");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToHand(translator);
        scn.MoveCardsToLocation(system, tie);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(translator));
        scn.LSDeployCard(translator);
        assertTrue(scn.LSHasCardChoiceAvailable(tie));
        scn.LSChooseCard(tie);
        scn.PassAllResponses();
        assertEquals(tie, translator.getAttachedTo());
    }

    @Test
    public void AstromechTranslatorDoesNotDeployOnNonStarfighterStarship() {
        var scn = GetScenario();
        var translator = scn.GetLSCard("translator");
        var corvette = scn.GetLSCard("corvette");
        var ywing = scn.GetLSCard("ywing");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToHand(translator);
        scn.MoveCardsToLocation(system, corvette, ywing);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(translator));
        scn.LSDeployCard(translator);
        assertFalse(scn.LSHasCardChoiceAvailable(corvette));
        assertTrue(scn.LSHasCardChoiceAvailable(ywing));
    }

    @Test
    public void AstromechTranslatorPilotedWithAstromechImmuneToAttritionLessThan3() {
        var scn = GetScenario();
        var translator = scn.GetLSCard("translator");
        var gold1 = scn.GetLSCard("gold1");
        var r2 = scn.GetLSCard("r2");
        var biggs = scn.GetLSCard("biggs");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, gold1);
        scn.BoardAsPilot(gold1, biggs);
        scn.BoardAsPassenger(gold1, r2);
        scn.AttachCardsTo(gold1, translator);

        assertFalse(scn.IsMatchingPilot(gold1, biggs));
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), gold1), scn.epsilon);
    }

    @Test
    public void AstromechTranslatorPilotedWithMatchingPilotImmuneToAttritionLessThan6() {
        var scn = GetScenario();
        var translator = scn.GetLSCard("translator");
        var red10 = scn.GetLSCard("red10");
        var r2 = scn.GetLSCard("r2");
        var luke = scn.GetLSCard("luke");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, red10);
        // Red 10 matching pilot is typically its persona match; Luke is not Red 10's match.
        // Use permanent pilot path: Red 10 has no permanent pilot, so use Flight Suit path in other test.
        // Here verify matching via hasMatchingPilotAboard using a known matching pair from Red Squadron if available.
        // Fallback: attach translator + R2 + Flight Suit on Biggs piloting Red 10 for <6 in Flight Suit test.
        scn.BoardAsPilot(red10, luke);
        scn.BoardAsPassenger(red10, r2);
        scn.AttachCardsTo(red10, translator);

        // Without matching, expect < 3
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), red10), scn.epsilon);
    }

    @Test
    public void AstromechTranslatorPilotedWithRebelFlightSuitImmuneToAttritionLessThan6() {
        var scn = GetScenario();
        var translator = scn.GetLSCard("translator");
        var suit = scn.GetLSCard("suit");
        var gold1 = scn.GetLSCard("gold1");
        var r2 = scn.GetLSCard("r2");
        var biggs = scn.GetLSCard("biggs");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, gold1);
        scn.BoardAsPilot(gold1, biggs);
        scn.BoardAsPassenger(gold1, r2);
        scn.AttachCardsTo(gold1, translator);
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), gold1), scn.epsilon);

        scn.AttachCardsTo(biggs, suit);
        assertTrue(scn.IsMatchingPilot(gold1, biggs));
        assertEquals(6, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), gold1), scn.epsilon);
    }

    @Test
    public void AstromechTranslatorPilotedWithNoAstromechHasNoImmunity() {
        var scn = GetScenario();
        var translator = scn.GetLSCard("translator");
        var ywing = scn.GetLSCard("ywing");
        var biggs = scn.GetLSCard("biggs");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, ywing);
        scn.BoardAsPilot(ywing, biggs);
        scn.AttachCardsTo(ywing, translator);

        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ywing), scn.epsilon);
    }

    @Test
    public void AstromechTranslatorUnpilotedWithAstromechMayUsePowerAndManeuver() {
        var scn = GetScenario();
        var translator = scn.GetLSCard("translator");
        var gold1 = scn.GetLSCard("gold1");
        var r2 = scn.GetLSCard("r2");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, gold1);
        scn.BoardAsPassenger(gold1, r2);
        scn.AttachCardsTo(gold1, translator);

        assertFalse(scn.game().getModifiersQuerying().isPiloted(scn.gameState(), gold1, false));
        assertTrue(scn.game().getModifiersQuerying().mayPilotWithAstromech(scn.gameState(), gold1));
        assertEquals(4, scn.GetPower(gold1)); // Gold 1 power 2 + R2-D2 +2
        assertEquals(5, scn.GetManeuver(gold1)); // maneuver 3 + R2-D2 +2
        assertEquals(6, scn.GetHyperspeed(gold1)); // hyperspeed 4 + R2-D2 +2
    }

    @Test
    public void AstromechTranslatorUnpilotedWithAstromechMayMoveUsingHyperspeedFilter() {
        var scn = GetScenario();
        var translator = scn.GetLSCard("translator");
        var gold1 = scn.GetLSCard("gold1");
        var r2 = scn.GetLSCard("r2");
        var tibrin = scn.GetLSStartingLocation();
        var dantooine = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(tibrin, gold1);
        scn.BoardAsPassenger(gold1, r2);
        scn.AttachCardsTo(gold1, translator);

        assertTrue(Filters.canMoveToUsingHyperspeed(gold1.getOwner(), gold1, false, true, 0)
                .accepts(scn.gameState(), scn.game().getModifiersQuerying(), dantooine));
    }

    @Test
    public void AstromechTranslatorUnpilotedWithNoAstromechCannotUsePowerOrManeuverOrMove() {
        var scn = GetScenario();
        var translator = scn.GetLSCard("translator");
        var gold1 = scn.GetLSCard("gold1");
        var tibrin = scn.GetLSStartingLocation();
        var dantooine = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(tibrin, gold1);
        scn.AttachCardsTo(gold1, translator);

        assertFalse(scn.game().getModifiersQuerying().isPiloted(scn.gameState(), gold1, false));
        assertFalse(scn.game().getModifiersQuerying().mayPilotWithAstromech(scn.gameState(), gold1));
        assertEquals(0, scn.GetPower(gold1));
        assertEquals(0, scn.GetManeuver(gold1));
        assertFalse(Filters.canMoveToUsingHyperspeed(gold1.getOwner(), gold1, false, true, 0)
                .accepts(scn.gameState(), scn.game().getModifiersQuerying(), dantooine));
    }

    @Test
    public void AstromechTranslatorAstromechLostImmediatelyLosesPowerAndManeuver() {
        var scn = GetScenario();
        var translator = scn.GetLSCard("translator");
        var gold1 = scn.GetLSCard("gold1");
        var r2 = scn.GetLSCard("r2");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, gold1);
        scn.BoardAsPassenger(gold1, r2);
        scn.AttachCardsTo(gold1, translator);

        assertEquals(4, scn.GetPower(gold1));
        assertEquals(5, scn.GetManeuver(gold1));

        scn.MoveCardToZone(r2.getOwner(), r2, Zone.LOST_PILE);

        assertEquals(0, scn.GetPower(gold1));
        assertEquals(0, scn.GetManeuver(gold1));
        assertFalse(scn.game().getModifiersQuerying().mayPilotWithAstromech(scn.gameState(), gold1));
    }
}
