package com.gempukku.swccgo.cards.set201.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import static org.junit.Assert.assertTrue;

public class Card_201_033_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("atho", "12_037");
                }},
                new HashMap<>() {{
                    put("adtftr1", "201_033");
                    put("adtftr2", "201_033");
                }},
                20,
                20,
                StartingSetup.LSStartingLocation("1_138"),
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void ADarkTimeForTheRebellionVStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("adtftr1").getBlueprint();

        assertEquals("A Dark Time For The Rebellion", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.VIRTUAL_SET_1);
            add(Icon.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
        assertEquals(ExpansionSet.SET_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ADarkTimeForTheRebellionVLosesOneForceWhenPlayedIfMassassiThroneRoomWasStartingLocation() {
        var scn = GetScenario();
        var adtftr = scn.GetDSCard("adtftr1");

        scn.StartGame();
        scn.MoveCardsToDSHand(adtftr);
        scn.DSActivateForceCheat(2);

        scn.SkipToDSTurn(Phase.CONTROL);
        int lostBefore = scn.GetLSLostPileCount();
        assertTrue(scn.DSCardPlayAvailable(adtftr));
        scn.DSPlayCard(adtftr, "Activate");
        resolveForceLoss(scn);
        scn.PassAllResponses();

        assertEquals(lostBefore + 1, scn.GetLSLostPileCount());
    }

    @Test
    public void ADarkTimeForTheRebellionVLosesOneForceNotTwoWhenGrabbedByATragedyHasOccurred() {
        var scn = GetScenario();
        var adtftr1 = scn.GetDSCard("adtftr1");
        var adtftr2 = scn.GetDSCard("adtftr2");
        var atho = scn.GetLSCard("atho");

        scn.StartGame();
        scn.MoveCardsToDSHand(adtftr1, adtftr2);
        scn.MoveCardsToLSSideOfTable(atho);
        scn.DSActivateForceCheat(4);
        scn.LSActivateForceCheat(2);

        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPlayCard(adtftr1, "Activate");
        resolveForceLoss(scn);
        passUntilLsActionText(scn, "Grab");
        scn.LSChooseAction("Grab");
        scn.PassAllResponses();
        assertTrue(scn.IsStackedOn(atho, adtftr1));

        int lostAfterFirst = scn.GetLSLostPileCount();
        if (!scn.DSAnyActionsAvailable()) {
            scn.SkipToDSTurn(Phase.CONTROL);
        }
        scn.DSPlayCard(adtftr2, "Activate");
        scn.PassResponses("Use ");
        assertTrue(scn.GetCurrentDecision() != null
                && scn.GetCurrentDecision().getText().toLowerCase().contains("required"));
        chooseRequiredContaining(scn, "lose");
        resolveForceLoss(scn);
        scn.PassAllResponses();

        assertEquals(lostAfterFirst + 1, scn.GetLSLostPileCount());
        assertTrue(scn.IsStackedOn(atho, adtftr2));
    }

    private static void chooseRequiredContaining(VirtualTableScenario scn, String needle) {
        var decision = scn.GetCurrentDecision();
        if (decision == null || !decision.getText().toLowerCase().contains("required")) {
            return;
        }
        String[] texts = decision.getDecisionParameters().get("actionText");
        String[] actionIds = decision.getDecisionParameters().get("actionId");
        if (texts != null && actionIds != null) {
            for (int i = 0; i < texts.length; i++) {
                if (texts[i] != null && texts[i].toLowerCase().contains(needle)) {
                    scn.PlayerDecided(scn.GetDecidingPlayer(), actionIds[i]);
                    return;
                }
            }
        }
    }

    private static void passUntilLsActionText(VirtualTableScenario scn, String text) {
        String needle = text.toLowerCase();
        for (int i = 0; i < 15; i++) {
            if (scn.GetLSAvailableActions().stream().anyMatch(a -> a.toLowerCase().contains(needle))) {
                return;
            }
            var decision = scn.GetCurrentDecision();
            if (decision == null || !decision.getText().toLowerCase().contains("optional")) {
                return;
            }
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
    }

    private static void resolveForceLoss(VirtualTableScenario scn) {
        for (int i = 0; i < 20; i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null) {
                return;
            }
            String text = decision.getText();
            if (text.contains("FORCE_LOSS_INITIATED") || text.contains("ABOUT_TO_LOSE_FORCE")) {
                scn.PlayerPass(scn.GetDecidingPlayer());
                continue;
            }
            String[] blueprintIds = decision.getDecisionParameters().get("blueprintId");
            if (blueprintIds != null && blueprintIds.length > 0
                    && (text.toLowerCase().contains("lose") || text.toLowerCase().contains("force") || text.toLowerCase().contains("choose"))) {
                scn.PlayerDecided(scn.GetDecidingPlayer(), "    0");
                continue;
            }
            String[] cardIds = decision.getDecisionParameters().get("cardId");
            if (cardIds != null && cardIds.length > 0
                    && (text.toLowerCase().contains("lose") || text.toLowerCase().contains("force") || text.toLowerCase().contains("choose"))) {
                scn.PlayerDecided(scn.GetDecidingPlayer(), cardIds[0]);
                continue;
            }
            return;
        }
    }
}
