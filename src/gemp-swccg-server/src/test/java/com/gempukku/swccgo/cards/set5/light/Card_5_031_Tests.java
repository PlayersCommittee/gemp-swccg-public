package com.gempukku.swccgo.cards.set5.light;

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

public class Card_5_031_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("ambush", "5_31");
                }},
                new HashMap<>() {{
                    put("comlink", "1_201");
                    put("baniss", "1_209");
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
    public void AmbushStatsAndKeywordsAreCorrect() {
        /**
         * Title: Ambush
         * Uniqueness: Unique
         * Side: Light
         * Type: Interrupt
         * Subtype: Lost
         * Destiny: 3
         * Icons: Cloud City, Interrupt
         * Game Text: During your turn, target a site where your total power is more than double opponent's total
         *         power. Unless opponent has a Dark Jedi or character weapon there, place each opponent character,
         *         vehicle and starship there (and cards on them) in owner's Used Pile.
         * Lore: 'Well done. Hold them in the security tower, and keep it quiet. Move.'
         * Set: Cloud City
         * Rarity: R
         */

        var scn = GetScenario();

        var card = scn.GetLSCard("ambush").getBlueprint();

        assertEquals("Ambush", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        assertEquals(ExpansionSet.CLOUD_CITY, card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
    }

    @Test
    public void AmbushPlacesAttachedDeviceInUsedPile() {
        var scn = GetScenario();

        var ambush = scn.GetLSCard("ambush");
        var trooper1 = scn.GetLSFiller(1);
        var trooper2 = scn.GetLSFiller(2);
        var trooper3 = scn.GetLSFiller(3);

        var stormtrooper = scn.GetDSFiller(1);
        var comlink = scn.GetDSCard("comlink");

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(ambush);
        scn.MoveCardsToLocation(site, trooper1, trooper2, trooper3, stormtrooper);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.AttachCardsTo(stormtrooper, comlink);

        assertTrue(scn.LSCardPlayAvailable(ambush));
        scn.LSPlayCard(ambush);
        if (scn.LSDecisionAvailable("Choose site")) {
            scn.LSChooseCard(site);
        }
        scn.PassAllResponses();

        assertTrue(scn.LSDecisionAvailable("place on Used Pile") || scn.DSDecisionAvailable("place on Used Pile"));
        if (scn.LSDecisionAvailable("place on Used Pile")) {
            assertTrue(scn.LSHasCardChoiceAvailable(stormtrooper));
            assertTrue(scn.LSHasCardChoiceAvailable(comlink));
            scn.LSChooseCard(comlink);
        } else {
            assertTrue(scn.DSHasCardChoiceAvailable(stormtrooper));
            assertTrue(scn.DSHasCardChoiceAvailable(comlink));
            scn.DSChooseCard(comlink);
        }
        scn.PassAllResponses();

        assertTrue(comlink.getZone() == Zone.USED_PILE || comlink.getZone() == Zone.TOP_OF_USED_PILE);
        assertTrue(stormtrooper.getZone() == Zone.USED_PILE || stormtrooper.getZone() == Zone.TOP_OF_USED_PILE);
    }

    @Test
    public void AmbushPlacesAttachedEffectInUsedPileNotLostPile() {
        var scn = GetScenario();

        var ambush = scn.GetLSCard("ambush");
        var trooper1 = scn.GetLSFiller(1);
        var trooper2 = scn.GetLSFiller(2);
        var trooper3 = scn.GetLSFiller(3);

        var stormtrooper = scn.GetDSFiller(1);
        var baniss = scn.GetDSCard("baniss");

        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(ambush);
        scn.MoveCardsToLocation(site, trooper1, trooper2, trooper3, stormtrooper);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.AttachCardsTo(stormtrooper, baniss);

        assertTrue(scn.LSCardPlayAvailable(ambush));
        scn.LSPlayCard(ambush);
        if (scn.LSDecisionAvailable("Choose site")) {
            scn.LSChooseCard(site);
        }
        scn.PassAllResponses();

        assertTrue(scn.LSDecisionAvailable("place on Used Pile") || scn.DSDecisionAvailable("place on Used Pile"));
        if (scn.LSDecisionAvailable("place on Used Pile")) {
            assertTrue(scn.LSHasCardChoiceAvailable(stormtrooper));
            assertTrue(scn.LSHasCardChoiceAvailable(baniss));
            scn.LSChooseCard(baniss);
        } else {
            assertTrue(scn.DSHasCardChoiceAvailable(stormtrooper));
            assertTrue(scn.DSHasCardChoiceAvailable(baniss));
            scn.DSChooseCard(baniss);
        }
        scn.PassAllResponses();

        assertTrue(baniss.getZone() == Zone.USED_PILE || baniss.getZone() == Zone.TOP_OF_USED_PILE);
        assertFalse(baniss.getZone() == Zone.LOST_PILE || baniss.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue(stormtrooper.getZone() == Zone.USED_PILE || stormtrooper.getZone() == Zone.TOP_OF_USED_PILE);
    }
}
