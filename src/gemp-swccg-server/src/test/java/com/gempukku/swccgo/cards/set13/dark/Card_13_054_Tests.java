package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class Card_13_054_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("order", "13_54");
                    put("tie", "9_175");
                    put("system", "1_282");
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
    public void BattleOrderShieldStatsAndKeywordsAreCorrect() {
        /**
         * Title: Battle Order
         * Type: Defensive Shield
         * Game Text: Plays on table. Unless Battle Plan on table, for either player to initiate a Force drain,
         *         that player must first use 3 Force unless that player occupies a battleground site and a battleground system.
         */
        var scn = GetScenario();
        var card = scn.GetDSCard("order").getBlueprint();

        assertEquals(Title.Battle_Order, card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEFENSIVE_SHIELD);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REFLECTIONS_III);
            add(Icon.DEFENSIVE_SHIELD);
        }});
        assertEquals(ExpansionSet.REFLECTIONS_III, card.getExpansionSet());
        assertEquals(Rarity.PM, card.getRarity());
    }

    @Test
    public void BattleOrderRequiresThreeForceToDrainUnlessOccupyingBattlegroundSiteAndSystem() {
        var scn = GetScenario();

        var order = scn.GetDSCard("order");
        var trooper = scn.GetDSFiller(1);
        var tie = scn.GetDSCard("tie");
        var site = scn.GetDSStartingLocation();
        var system = scn.GetDSCard("system");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(order);
        scn.MoveCardsToLocation(site, trooper);

        float siteOnly = scn.game().getModifiersQuerying().getInitiateForceDrainCost(scn.gameState(), site, scn.DS);
        assertEquals(3, siteOnly, scn.epsilon);

        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, tie);

        float siteAndSystem = scn.game().getModifiersQuerying().getInitiateForceDrainCost(scn.gameState(), site, scn.DS);
        assertEquals(0, siteAndSystem, scn.epsilon);
    }
}
