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
                    put("luke", "1_019"); // deploy 5 - uses Force into Used
                    put("leia", "1_017"); // deploy 4 - second Force use for cancel
                    put("han", "1_011"); // presence
                }},
                new HashMap<>() {{
                    put("viper", "1_282");
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
        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(descent, luke);
        // Seed some Used Pile cards so recirculate is observable
        scn.MoveCardsToTopOfLSUsedPile(scn.GetTopOfLSReserveDeck());
        scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSReserveDeck());

        // Presence so Luke can deploy
        scn.MoveCardsToLocation(site, scn.GetLSCard("leia"));

        scn.SkipToLSTurn(Phase.DEPLOY);

        int lsUsedBeforeDeploy = scn.GetLSUsedPileCount();
        int dsUsedBeforeDeploy = scn.GetDSUsedPileCount();
        assertTrue(lsUsedBeforeDeploy >= 1);
        assertTrue(dsUsedBeforeDeploy >= 1);

        assertTrue(scn.LSDeployAvailable(luke));
        scn.LSDeployCard(luke);
        scn.LSChooseCard(site);
        // Using Force places cards in Used Pile - Descent should be offered as a response
        assertTrue(scn.LSCardPlayAvailable(descent));
        scn.LSPlayCard(descent);
        scn.PassAllResponses();

        assertEquals(Zone.SIDE_OF_TABLE, descent.getZone());
        // All Used Piles immediately re-circulated
        assertEquals(0, scn.GetLSUsedPileCount());
        assertEquals(0, scn.GetDSUsedPileCount());
    }

    @Test
    public void DescentIntoTheDarkCancelsWhenCardPlacedInUsedPile() {
        var scn = GetScenario();

        var descent = scn.GetLSCard("descent");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        var han = scn.GetLSCard("han");
        scn.MoveCardsToLSHand(descent, luke, leia);
        scn.MoveCardsToLocation(site, han);

        scn.SkipToLSTurn(Phase.DEPLOY);

        // First deploy uses Force -> play Descent
        assertTrue(scn.LSDeployAvailable(luke));
        scn.LSDeployCard(luke);
        scn.LSChooseCard(site);
        assertTrue(scn.LSCardPlayAvailable(descent));
        scn.LSPlayCard(descent);
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, descent.getZone());
        assertEquals(0, scn.GetLSUsedPileCount());

        // Second deploy uses Force again -> Descent cancels
        assertTrue(scn.LSDeployAvailable(leia));
        scn.LSDeployCard(leia);
        scn.LSChooseCard(site);
        scn.PassAllResponses();

        assertTrue(descent.getZone() == Zone.TOP_OF_LOST_PILE || descent.getZone() == Zone.LOST_PILE);
    }

    @Test
    public void DescentIntoTheDarkNotPlayableDuringOpponentsTurn() {
        var scn = GetScenario();

        var descent = scn.GetLSCard("descent");
        var viper = scn.GetDSCard("viper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(descent);
        scn.MoveCardsToDSHand(viper);

        // Get through to DS turn deploy; LS has Descent in hand but it is opponent's turn
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertTrue(scn.DSDeployAvailable(viper));
        scn.DSDeployCard(viper);
        scn.DSChooseCard(site);
        // Using Force on DS turn should NOT offer Descent to LS
        assertFalse(scn.LSCardPlayAvailable(descent));
    }
}