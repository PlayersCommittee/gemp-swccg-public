package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_4_20_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("descent", "4_20");
                    put("luke", "1_019");
                    put("leia", "1_017");
                    put("han", "1_011");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
                    put("officer", "1_180");
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

    /**
     * AdHoc place-from-table into Used Pile. Framework carryOutEffect always decides "0",
     * which selects an existing Deploy action when any are legal; pick the newly-added index instead.
     */
    private void placeCardInUsedPile(VirtualTableScenario scn, String playerId, PhysicalCardImpl card) {
        var awaitingDecision = (CardActionSelectionDecision) scn.userFeedback().getAwaitingDecision(playerId);
        String[] before = awaitingDecision.getDecisionParameters().get("actionId");
        int idx = (before == null) ? 0 : before.length;
        var action = new TopLevelGameTextAction(card, playerId, card.getCardId());
        action.setText("Place in Used Pile (ad-hoc)");
        action.appendEffect(new PlaceCardInUsedPileFromTableEffect(action, card));
        awaitingDecision.addAction(action);
        scn.PlayerDecided(playerId, String.valueOf(idx));
        // Resolve about-to-leave-table responses so the card actually enters Used Pile
        scn.PassResponses("ABOUT_TO");
    }

    private void lsPlaceCardInUsedPile(VirtualTableScenario scn, PhysicalCardImpl card) {
        placeCardInUsedPile(scn, scn.LS, card);
    }

    private void dsPlaceCardInUsedPile(VirtualTableScenario scn, PhysicalCardImpl card) {
        placeCardInUsedPile(scn, scn.DS, card);
    }

    @Test
    public void DescentIntoTheDarkStatsAndKeywordsAreCorrect() {
        /**
         * Title: Descent Into The Dark
         * Uniqueness: Unique
         * Side: Light
         * Type: Effect
         * Subtype: Immediate
         * Destiny: 4
         * Icons: Dagobah, Effect
         * Game Text: During your turn, if either player just placed a card in a Used Pile, deploy on table.
         *         All Used Piles are immediately re-circulated. When any player places one or more cards in a
         *         Used Pile, Immediate Effect canceled.
         * Lore: Jedi training is a journey into the depths of an apprentice's subconscious, where one must learn
         *         to use the Force wisely. "A Jedi's strength flows from the Force."
         * Set: Dagobah
         * Rarity: R
         */

        var scn = GetScenario();

        var card = scn.GetLSCard("descent").getBlueprint();

        assertEquals("Descent Into The Dark", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.IMMEDIATE, card.getCardSubtype());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>() {{
        }});
        assertEquals(ExpansionSet.DAGOBAH, card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
    }

    @Test
    public void DescentIntoTheDarkDeploysDuringYourTurnWhenCardPlacedInUsedPileAndRecirculates() {
        var scn = GetScenario();

        var descent = scn.GetLSCard("descent");
        var luke = scn.GetLSCard("luke");
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, han, luke);

        scn.SkipToLSTurn(Phase.DEPLOY);

        scn.MoveCardsToLSHand(descent);
        scn.MoveCardsToTopOfLSUsedPile(scn.GetTopOfLSReserveDeck());
        scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSReserveDeck());
        assertTrue(scn.GetLSUsedPileCount() >= 1);
        assertTrue(scn.GetDSUsedPileCount() >= 1);

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        lsPlaceCardInUsedPile(scn, han);
        // Opponent of performing player responds first to after-triggers
        scn.DSPass();
        assertTrue("Descent should be playable as response to Used Pile placement", scn.LSCardPlayAvailable(descent));
        scn.LSPlayCard(descent);
        scn.PassAllResponses();

        assertEquals(Zone.SIDE_OF_TABLE, descent.getZone());
        assertEquals(0, scn.GetLSUsedPileCount());
        assertEquals(0, scn.GetDSUsedPileCount());
    }

    @Test
    public void DescentIntoTheDarkCancelsWhenCardPlacedInUsedPile() {
        var scn = GetScenario();

        var descent = scn.GetLSCard("descent");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, han, luke, leia);

        scn.SkipToLSTurn(Phase.DEPLOY);

        scn.MoveCardsToLSHand(descent);

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        lsPlaceCardInUsedPile(scn, han);
        scn.DSPass();
        assertTrue(scn.LSCardPlayAvailable(descent));
        scn.LSPlayCard(descent);
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, descent.getZone());

        // Phase actions alternate: after LS AdHoc place, DS gets the next Deploy window
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        lsPlaceCardInUsedPile(scn, luke);
        scn.PassAllResponses();

        assertEquals(Zone.TOP_OF_LOST_PILE, descent.getZone());
    }

    @Test
    public void DescentIntoTheDarkNotPlayableDuringOpponentsTurn() {
        var scn = GetScenario();

        var descent = scn.GetLSCard("descent");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var officer = scn.GetDSCard("officer");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, officer, stormtrooper);

        scn.SkipToDSTurn(Phase.DEPLOY);

        scn.MoveCardsToLSHand(descent);

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        dsPlaceCardInUsedPile(scn, stormtrooper);
        // Force use / Used placement on opponent turn must not offer Descent
        assertFalse(scn.LSCardPlayAvailable(descent));
    }
}