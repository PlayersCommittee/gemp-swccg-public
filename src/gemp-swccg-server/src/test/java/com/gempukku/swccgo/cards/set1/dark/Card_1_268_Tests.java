package com.gempukku.swccgo.cards.set1.dark;

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

public class Card_1_268_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("blaster", "1_152");
                }},
                new HashMap<>() {{
                    put("stun", "1_268");
                    put("stacked", "1_201");
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
    public void SetForStunStatsAndKeywordsAreCorrect() {
        /**
         * Title: Set For Stun
         * Uniqueness: Unrestricted
         * Side: Dark
         * Type: Interrupt
         * Subtype: Lost
         * Destiny: 2
         * Icons: Interrupt
         * Game Text: Use 2 Force to target one opponent's character. Draw destiny. If destiny > character's ability,
         *         character immediately returns to opponent's hand. (Also, any cards deployed on character return
         *         to owners' hands.)
         * Set: Premiere
         * Rarity: C2
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("stun").getBlueprint();

        assertEquals("Set For Stun", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(2, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        assertEquals(ExpansionSet.PREMIERE, card.getExpansionSet());
        assertEquals(Rarity.C2, card.getRarity());
    }

    @Test
    public void SetForStunReturnsDeployedOnCardsToHandNotStackedCards() {
        var scn = GetScenario();

        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard("blaster");
        var stun = scn.GetDSCard("stun");
        var stacked = scn.GetDSCard("stacked");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, luke);
        scn.AttachCardsTo(luke, blaster);
        scn.StackCardsOn(luke, stacked);
        scn.MoveCardsToDSHand(stun);

        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.PrepareDSDestiny(6);
        assertTrue(scn.DSCardPlayAvailable(stun));
        scn.DSPlayCard(stun);
        if (scn.DSDecisionAvailable("Choose character") || scn.DSHasCardChoiceAvailable(luke)) {
            scn.DSChooseCard(luke);
        }
        scn.PassCardPlayResponses();
        scn.LSPass();
        scn.DSPass();
        scn.LSPass();
        scn.DSPass();
        scn.LSPass();
        scn.DSPass();
        scn.LSPass();
        scn.DSPass();
        scn.PassAllResponses();

        for (int i = 0; i < 8; i++) {
            if (luke.getZone() == Zone.HAND
                    && blaster.getZone() == Zone.HAND
                    && (stacked.getZone() == Zone.LOST_PILE || stacked.getZone() == Zone.TOP_OF_LOST_PILE)) {
                break;
            }
            if (scn.DSHasCardChoiceAvailable(stacked) || scn.LSHasCardChoiceAvailable(stacked)) {
                if (scn.DSHasCardChoiceAvailable(stacked)) {
                    scn.DSChooseCard(stacked);
                } else {
                    scn.LSChooseCard(stacked);
                }
            } else if (scn.DSHasCardChoiceAvailable(luke)) {
                scn.DSChooseCard(luke);
            } else if (scn.LSHasCardChoiceAvailable(luke)) {
                scn.LSChooseCard(luke);
            } else if (scn.DSHasCardChoiceAvailable(blaster)) {
                scn.DSChooseCard(blaster);
            } else if (scn.LSHasCardChoiceAvailable(blaster)) {
                scn.LSChooseCard(blaster);
            } else {
                scn.PassAllResponses();
            }
        }
        scn.PassAllResponses();

        assertEquals(Zone.HAND, luke.getZone());
        assertEquals(Zone.HAND, blaster.getZone());
        assertTrue("stacked=" + stacked.getZone(),
                stacked.getZone() == Zone.LOST_PILE || stacked.getZone() == Zone.TOP_OF_LOST_PILE);
    }
}
