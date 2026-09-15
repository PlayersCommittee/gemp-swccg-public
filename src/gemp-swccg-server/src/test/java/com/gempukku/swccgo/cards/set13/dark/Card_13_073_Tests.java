package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.timing.Action;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Let Them Make The First Move (#942): once during control phase, target opponent's character
 * present with your Dark Jedi at an interior Theed Palace site; target is lost.
 */
public class Card_13_073_Tests {
    private static final StartingSetup LetThemMakeTheFirstMove = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("obj", "13_73");
                put("core", "13_77");
                put("generator", "13_76");
                put("hatred", "13_65");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            if (scn.DSDecisionAvailable("Choose starting objective") || scn.DSDecisionAvailable("Choose your starting")) {
                scn.DSChooseCard(scn.GetDSCard("obj"));
            }
            if (scn.DSDecisionAvailable("Choose Theed Palace Generator Core")) {
                scn.DSChooseCard(scn.GetDSCard("core"));
            }
            if (scn.DSDecisionAvailable("Choose Theed Palace Generator to deploy")
                    || scn.DSDecisionAvailable("Choose Theed Palace Generator")) {
                scn.DSChooseCard(scn.GetDSCard("generator"));
            }
            if (scn.DSDecisionAvailable("Choose Deep Hatred")) {
                scn.DSChooseCard(scn.GetDSCard("hatred"));
            }
            if (scn.DSDecisionAvailable("On which side")) {
                scn.DSChoose("Left");
            }
        }
    };

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("maul", "11_54");
                }},
                40,
                40,
                StartingSetup.DefaultLSGroundLocation,
                LetThemMakeTheFirstMove,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void LetThemMakeTheFirstMoveStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("obj").getBlueprint();

        assertEquals(Title.Let_Them_Make_The_First_Move, card.getTitle());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REFLECTIONS_III);
            add(Icon.OBJECTIVE);
            add(Icon.EPISODE_I);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        assertEquals(ExpansionSet.REFLECTIONS_III, card.getExpansionSet());
        assertEquals(Rarity.PM, card.getRarity());
    }

    @Test
    public void LetThemMakeTheFirstMoveMayMakeCharacterLostDuringControlPhase() {
        var scn = GetScenario();
        var obj = scn.GetDSCard("obj");
        var generator = scn.GetDSCard("generator");
        var maul = scn.GetDSCard("maul");
        var rebel = scn.GetLSFiller(1);

        scn.StartGame();
        scn.MoveCardsToLocation(generator, maul, rebel);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertEquals(Phase.CONTROL, scn.gameState().getCurrentPhase());
        List<Action> actions = obj.getBlueprint().getTopLevelActions(scn.DS, scn.game(), obj);
        boolean offered = false;
        if (actions != null) {
            for (Action action : actions) {
                String text = action.getText() == null ? "" : action.getText().toLowerCase();
                if (text.contains("target character") || text.contains("lost")) {
                    offered = true;
                    break;
                }
            }
        }
        assertTrue("expected Target character during DS control; decision="
                        + (scn.GetCurrentDecision() == null ? "null" : scn.GetCurrentDecision().getText())
                        + " actions=" + actions,
                offered);
    }
}
