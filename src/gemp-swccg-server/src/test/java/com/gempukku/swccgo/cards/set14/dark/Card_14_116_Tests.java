package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collections;
import java.util.List;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_14_116_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("dfs1015", "14_116");
                    put("dfs1308", "14_117");
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

    private static String decisionText(VirtualTableScenario scn, String player) {
        AwaitingDecision ad = scn.GetAwaitingDecision(player);
        return ad == null ? "<none>" : ad.getText();
    }

    @Test
    public void DFS1015StatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("dfs1015").getBlueprint();

        assertEquals("DFS-1015", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertTrue(card.isCardType(CardType.STARSHIP));
        assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getManeuver(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertTrue(card.hasKeyword(Keyword.DFS_SQUADRON));
        assertTrue(card.hasKeyword(Keyword.NO_HYPERDRIVE));
        assertTrue(card.getModelTypes().contains(ModelType.DROID_STARFIGHTER));
        assertEquals(1, card.getIconCount(Icon.THEED_PALACE));
        assertEquals(1, card.getIconCount(Icon.EPISODE_I));
        assertEquals(1, card.getIconCount(Icon.TRADE_FEDERATION));
        assertEquals(1, card.getIconCount(Icon.PILOT));
        assertEquals(1, card.getIconCount(Icon.PRESENCE));
        assertEquals(1, card.getIconCount(Icon.STARSHIP));
        assertEquals(ExpansionSet.THEED_PALACE, card.getExpansionSet());
        assertEquals(Rarity.U, card.getRarity());
    }

    @Test
    public void DFS1015DrawTwoChooseOneOfferedWhenMaxBattleDestiniesIsOne() {
        // Real-path for #973: with max battle destinies already reduced to 1,
        // draw-2-choose-1 must still be offered (gate on choose-Y, not draw-X).
        var scn = GetScenario();

        var xwing = scn.GetLSCard("xwing");
        var dfs1015 = scn.GetDSCard("dfs1015");
        var dfs1308 = scn.GetDSCard("dfs1308");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(system, dfs1015, dfs1308, xwing);
        scn.PrepareDSDestiny(5);

        scn.ApplyAdHocModifier(new DrawsBattleDestinyIfUnableToOtherwiseModifier(dfs1015, 1));
        scn.ApplyAdHocModifier(new MayNotDrawMoreThanBattleDestinyModifier(dfs1015, 1, scn.DS));

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(system);

        scn.DSPass();
        scn.LSPass();

        scn.LSPass();
        scn.DSPass();

        assertEquals(1, scn.GetDSBattleDestinyLimit());
        assertEquals(1, scn.GetDSBattleDestinyCount());

        String current = scn.GetCurrentPlayer();
        assertTrue("Expected battle destiny prompt for " + current
                        + "; DS=[" + decisionText(scn, scn.DS) + "] LS=[" + decisionText(scn, scn.LS) + "]",
                scn.DecisionAvailable(current, "battle destiny"));
        scn.PlayerChooseYes(current);

        if (scn.LSAnyDecisionsAvailable()) scn.LSPass();
        if (scn.DSAnyDecisionsAvailable() && scn.DSDecisionAvailable("COST_TO_DRAW")) scn.DSPass();
        if (scn.LSAnyDecisionsAvailable()) scn.LSPass();

        assertTrue("DFS-1015 draw-two-choose-one should be offered when max destinies is 1; DS=["
                        + decisionText(scn, scn.DS) + "]",
                scn.DSCardActionAvailable(dfs1015, "Draw two and choose one"));

        // Accept and complete under max=1 (must not be blocked mid-draw).
        scn.DSUseCardAction(dfs1015, "Draw two and choose one");
        scn.PassAllResponses();
        assertTrue("Battle should continue after draw-2-choose-1 under max=1",
                scn.DSAnyDecisionsAvailable() || scn.LSAnyDecisionsAvailable());
    }

    @Test
    public void DrawXChooseYChooseGreaterThanMaxNotOfferedWhenMaxBattleDestiniesIsOne() {
        // choose-Y > max must not be offered (VHD: draw-10-choose-3 blocked when max is 2, etc.)
        var scn = GetScenario();

        var xwing = scn.GetLSCard("xwing");
        var dfs1015 = scn.GetDSCard("dfs1015");
        var dfs1308 = scn.GetDSCard("dfs1308");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, dfs1015, dfs1308, xwing);

        scn.ApplyAdHocModifier(new DrawsBattleDestinyIfUnableToOtherwiseModifier(dfs1015, 1));
        scn.ApplyAdHocModifier(new MayNotDrawMoreThanBattleDestinyModifier(dfs1015, 1, scn.DS));

        // Synthetic draw-X-choose-Y with chooseY=2 (> max 1), gated by canDrawDestinyAndChoose
        scn.ApplyAdHocAction(new AbstractActionProxy() {
            @Override
            public List<TriggerAction> getOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult) {
                if (playerId.equals(scn.DS)
                        && TriggerConditions.isAboutToDrawBattleDestiny(game, effectResult, playerId)
                        && GameConditions.canDrawDestinyAndChoose(game, 2)) {
                    final var action = new OptionalGameTextTriggerAction(dfs1015, dfs1015.getCardId());
                    action.setText("Draw three and choose two");
                    return Collections.singletonList(action);
                }
                return null;
            }
        });

        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(system);
        scn.DSPass();
        scn.LSPass();
        scn.LSPass();
        scn.DSPass();

        assertEquals(1, scn.GetDSBattleDestinyLimit());

        String current = scn.GetCurrentPlayer();
        assertTrue(scn.DecisionAvailable(current, "battle destiny"));
        scn.PlayerChooseYes(current);

        if (scn.LSAnyDecisionsAvailable()) scn.LSPass();
        if (scn.DSAnyDecisionsAvailable() && scn.DSDecisionAvailable("COST_TO_DRAW")) scn.DSPass();
        if (scn.LSAnyDecisionsAvailable()) scn.LSPass();

        // choose-Y=1 (DFS-1015) still offered; choose-Y=2 must not be
        assertTrue(scn.DSCardActionAvailable(dfs1015, "Draw two and choose one"));
        assertFalse("choose-Y=2 must not be offered when max battle destinies is 1; DS=["
                        + decisionText(scn, scn.DS) + "]",
                scn.DSCardActionAvailable(dfs1015, "Draw three and choose two")
                        || scn.DSActionAvailable("Draw three and choose two"));
    }
}
