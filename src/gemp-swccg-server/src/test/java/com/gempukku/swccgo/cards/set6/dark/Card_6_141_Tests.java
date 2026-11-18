package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

public class Card_6_141_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("chewie", "200_5");
                }},
                new HashMap<>() {{
                    put("wrapped", "6_141");
                    put("boba", "5_91");
                    put("shield", "5_145");
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
    public void AllWrappedUpStatsAndKeywordsAreCorrect() {
        /**
         * Title: All Wrapped Up
         * Uniqueness: Unique
         * Side: Dark
         * Type: Effect
         * Destiny: 2
         * Icons: Jabba's Palace, Effect
         * Game Text: Deploy on your side of table. We Have A Prisoner and Oo-ta Goo-ta Solo? play for free and are
         *         immune to Sense. Also, whenever opponent forfeits a character, your bounty hunter present may
         *         capture that character. (Immune to Alter.)
         * Set: Jabba's Palace
         * Rarity: U
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("wrapped").getBlueprint();

        assertEquals("All Wrapped Up", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(2, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.JABBAS_PALACE);
            add(Icon.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        assertEquals(ExpansionSet.JABBAS_PALACE, card.getExpansionSet());
        assertEquals(Rarity.U, card.getRarity());
    }

    @Test
    public void AllWrappedUpDoesNotCaptureCaptiveForfeitedByHumanShield() {
        var scn = GetScenario();

        var chewie = scn.GetLSCard("chewie");
        var trooper = scn.GetLSFiller(1);
        var wrapped = scn.GetDSCard("wrapped");
        var boba = scn.GetDSCard("boba");
        var shield = scn.GetDSCard("shield");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToDSSideOfTable(wrapped);
        scn.MoveCardsToDSHand(shield);
        scn.MoveCardsToLocation(site, boba, chewie, trooper);
        scn.CaptureCardWith(boba, chewie);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle());
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();

        assertFalse(scn.DSCardActionAvailable(wrapped, "Capture"));

        if (scn.DSPlayLostInterruptAvailable(shield)) {
            scn.DSPlayLostInterrupt(shield);
            if (scn.DSHasCardChoiceAvailable(chewie)) {
                scn.DSChooseCard(chewie);
            }
            scn.PassAllResponses();
            assertFalse(scn.DSCardActionAvailable(wrapped, "Capture"));
            assertFalse(scn.DSDecisionAvailable("Capture"));
        }
    }
}
