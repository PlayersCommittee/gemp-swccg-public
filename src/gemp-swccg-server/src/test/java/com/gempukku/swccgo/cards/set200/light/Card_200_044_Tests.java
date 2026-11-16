package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class Card_200_044_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("dockyards", "200_44");
                    put("warRoom", "9_058");
                    put("homeOne", "9_074");
                    put("rebel", "1_028");
                }},
                new HashMap<>() {{
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
    public void MonCalamariDockyardsStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("dockyards").getBlueprint();
        assertEquals("Mon Calamari Dockyards", card.getTitle());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEATH_STAR_II);
            add(Icon.VIRTUAL_SET_0);
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.SET_0, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MonCalamariDockyardsDoesNotCapHomeOneWarRoomDeployReduction() {
        var scn = GetScenario();
        var dockyards = scn.GetLSCard("dockyards");
        var warRoom = scn.GetLSCard("warRoom");
        var homeOne = scn.GetLSCard("homeOne");
        var rebel = scn.GetLSCard("rebel");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(dockyards);
        scn.MoveLocationToTable(warRoom);
        scn.MoveCardsToLocation(warRoom, rebel);

        assertEquals("Home One printed 12, War Room -5, Dockyards -2; Dockyards max -3 is not a global cap",
                5, scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), homeOne), scn.epsilon);
    }
}
