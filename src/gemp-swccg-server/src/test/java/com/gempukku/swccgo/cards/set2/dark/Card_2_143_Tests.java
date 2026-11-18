package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.timing.Action;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Death Star extra regular move may use hyperspeed outside Move phase (#954).
 */
public class Card_2_143_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tat", "1_127");
                }},
                new HashMap<>() {{
                    put("ds", "2_143");
                }},
                40,
                40,
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
    public void DeathStarStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("ds").getBlueprint();

        assertEquals(Title.Death_Star, card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SYSTEM, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.DARK_FORCE);
            add(Icon.MOBILE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        assertEquals(ExpansionSet.A_NEW_HOPE, card.getExpansionSet());
        assertEquals(Rarity.R2, card.getRarity());
    }

    @Test
    public void DeathStarExtraRegularMoveMayUseHyperspeedOutsideMovePhase() {
        var scn = GetScenario();
        var ds = scn.GetDSCard("ds");
        var tat = scn.GetLSCard("tat");

        scn.StartGame();
        scn.MoveLocationToTable(ds);
        scn.MoveLocationToTable(tat);
        scn.DSActivateForceCheat(5);
        scn.SkipToLSTurn(Phase.CONTROL);

        Action duringControl = ds.getBlueprint().getRegularMoveAction(
                scn.DS, scn.game(), ds, false, 0, false, false, Filters.any);
        assertNull(duringControl);

        Action extraRegular = ds.getBlueprint().getRegularMoveAction(
                scn.DS, scn.game(), ds, false, 0, true, false, Filters.any);
        assertNotNull(extraRegular);
    }
}
