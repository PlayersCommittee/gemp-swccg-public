package com.gempukku.swccgo.cards.set204.dark;

import com.gempukku.swccgo.common.CardSubtype;
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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_204_049_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("imi", "200_053");
                }},
                new HashMap<>() {{
                    put("freeze", "204_049");
                }},
                15,
                15,
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
    public void ForceFreezeStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("freeze").getBlueprint();
        assertEquals("Force Freeze", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VIRTUAL_SET_4);
            add(Icon.INTERRUPT);
            add(Icon.EPISODE_VII);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        assertEquals(ExpansionSet.SET_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ForceFreezeAfterImpressiveMostImpressiveTakesFirstTwoWeaponsActions() {
        var scn = GetScenario();
        var freeze = scn.GetDSCard("freeze");
        var imi = scn.GetLSCard("imi");
        startDsBattleWithInterruptsInHand(scn, freeze, imi);

        playInterruptDuringBattleInitiated(scn, imi, true);
        playInterruptDuringBattleInitiated(scn, freeze, false);
        passRemainingBattleInitiatedResponses(scn);

        assertTrue("Force Freeze played after Impressive, Most Impressive must give Dark the first weapons action",
                scn.AwaitingDSWeaponsSegmentActions());
        assertFalse(scn.AwaitingLSWeaponsSegmentActions());
        scn.DSPass();
        assertTrue("Force Freeze when Dark initiated must also give Dark the second weapons action",
                scn.AwaitingDSWeaponsSegmentActions());
    }

    @Test
    public void ImpressiveMostImpressiveAloneTakesFirstWeaponsAction() {
        var scn = GetScenario();
        var freeze = scn.GetDSCard("freeze");
        var imi = scn.GetLSCard("imi");
        startDsBattleWithInterruptsInHand(scn, freeze, imi);

        playInterruptDuringBattleInitiated(scn, imi, true);
        passRemainingBattleInitiatedResponses(scn);

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.AwaitingDSWeaponsSegmentActions());
    }

    private void startDsBattleWithInterruptsInHand(VirtualTableScenario scn, PhysicalCardImpl freeze, PhysicalCardImpl imi) {
        var site = scn.GetDSStartingLocation();
        var ls = scn.GetLSFiller(1);
        var ds = scn.GetDSFiller(1);

        scn.StartGame();
        scn.MoveCardsToLocation(site, ls, ds);
        scn.MoveCardsToLSHand(imi);
        scn.MoveCardsToDSHand(freeze);
        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle(site));
        scn.DSUseCardAction(site, "Initiate battle");
        scn.PassForceUseResponses();
    }

    private void playInterruptDuringBattleInitiated(VirtualTableScenario scn, PhysicalCardImpl card, boolean light) {
        for (int i = 0; i < 12; i++) {
            if (light && actionAvailable(scn, true, "Take first weapons")) {
                scn.LSPlayCard(card);
                scn.PassCardAndForceUseResponses();
                return;
            }
            if (!light && actionAvailable(scn, false, "Take first")) {
                scn.DSPlayCard(card);
                scn.PassCardAndForceUseResponses();
                return;
            }
            var decision = scn.GetCurrentDecision();
            if (decision == null || !decision.getText().toLowerCase().contains("optional")) {
                assertTrue("Expected to play " + card.getBlueprint().getTitle() + " during battle initiated", false);
                return;
            }
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertTrue("Expected to play " + card.getBlueprint().getTitle() + " during battle initiated", false);
    }

    private void passRemainingBattleInitiatedResponses(VirtualTableScenario scn) {
        for (int i = 0; i < 12; i++) {
            if (scn.AwaitingDSWeaponsSegmentActions() || scn.AwaitingLSWeaponsSegmentActions()) {
                return;
            }
            var decision = scn.GetCurrentDecision();
            if (decision == null || !decision.getText().toLowerCase().contains("optional")) {
                return;
            }
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
    }

    private boolean actionAvailable(VirtualTableScenario scn, boolean light, String text) {
        var actions = light ? scn.GetLSAvailableActions() : scn.GetDSAvailableActions();
        String lower = text.toLowerCase();
        return actions.stream().anyMatch(action -> action.toLowerCase().contains(lower));
    }
}
