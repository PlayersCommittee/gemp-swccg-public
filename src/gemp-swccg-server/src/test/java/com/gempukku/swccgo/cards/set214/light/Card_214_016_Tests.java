package com.gempukku.swccgo.cards.set214.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class Card_214_016_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("beaumont", "214_16");
                }},
                new HashMap<>() {{
                    put("alert", "208_38");
                    put("khurgee", "2_83");
                    put("thunderflare", "219_22");
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
    public void BeaumontKinStatsAndKeywordsAreCorrect() {
        /**
         * Title: Beaumont Kin
         * Uniqueness: Unique
         * Side: Light
         * Type: Resistance
         * Destiny: 3
         * Deploy: 2
         * Power: 3
         * Ability: 2
         * Forfeit: 4
         * Icons: Warrior, Virtual Set 14, Episode VII, Resistance
         * Game Text: Deploy cost of opponent's characters may not be modified at same and related locations.
         * Set: Set 14
         * Rarity: V
         */
        var scn = GetScenario();
        var card = scn.GetLSCard("beaumont").getBlueprint();

        assertEquals("Beaumont Kin", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.RESISTANCE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WARRIOR);
            add(Icon.VIRTUAL_SET_14);
            add(Icon.EPISODE_VII);
            add(Icon.RESISTANCE);
        }});
        assertEquals(ExpansionSet.SET_14, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BeaumontKinPreventsAlertMyStarDestroyerFromReducingCharacterDeployCost() {
        var scn = GetScenario();

        var beaumont = scn.GetLSCard("beaumont");
        var alert = scn.GetDSCard("alert");
        var khurgee = scn.GetDSCard("khurgee");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, beaumont);
        scn.MoveCardsToDSSideOfTable(alert);

        float withBeaumont = scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), alert, khurgee, site, false, null, false, -1, null, false);
        assertEquals(3, withBeaumont, scn.epsilon);

        scn.MoveOutOfPlay(beaumont);

        float withoutBeaumont = scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), alert, khurgee, site, false, null, false, -1, null, false);
        assertEquals(2, withoutBeaumont, scn.epsilon);
    }

    @Test
    public void BeaumontKinPreventsAlertMyStarDestroyerFromReducingSimultaneousPilotCost() {
        var scn = GetScenario();

        var beaumont = scn.GetLSCard("beaumont");
        var alert = scn.GetDSCard("alert");
        var khurgee = scn.GetDSCard("khurgee");
        var thunderflare = scn.GetDSCard("thunderflare");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, beaumont);
        scn.MoveCardsToDSSideOfTable(alert);

        float withBeaumont = scn.game().getModifiersQuerying().getSimultaneousDeployCost(
                scn.gameState(), alert, thunderflare, false, -1, khurgee, false, -1, site, null, false);
        assertEquals(9, withBeaumont, scn.epsilon);

        scn.MoveOutOfPlay(beaumont);

        float withoutBeaumont = scn.game().getModifiersQuerying().getSimultaneousDeployCost(
                scn.gameState(), alert, thunderflare, false, -1, khurgee, false, -1, site, null, false);
        assertEquals(8, withoutBeaumont, scn.epsilon);
    }
}
