package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_9_055_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
            new HashMap<>() {{
                put("test6", "9_55");
                put("luke", "1_19");
            }},
            new HashMap<>() {{
                put("vader", "7_175");
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
    public void YouMustConfrontVaderStatsAndKeywordsAreCorrect() {
        /**
         * Title: You Must Confront Vader
         * Uniqueness: Unrestricted
         * Side: Light
         * Type: Jedi Test
         * Destiny: 6
         * Icons: Death Star II, Jedi Test
         * Game Text: Deploy on table. Target a Skywalker who has completed Jedi Test #5. While target on Dagobah,
         *      'move phase' on Save You It Can may be treated as 'deploy phase.' Until Jedi Test completed,
         *      battles you initiate at Vader's site are canceled and opponent loses 2 Force at start of your turn
         *      unless Vader is at a battleground site. Attempt during your move phase when Vader with target
         *      (even as a non-frozen captive). Vader and target duel: Each player draws destiny. Add ability.
         *      Highest total wins. If target wins, test completed: Leave on table. Add one battle destiny
         *      in every battle. Also, target is immune to attrition < 5.
         * Set: Death Star II
         * Rarity: R
         */

        var scn = GetScenario();

        var card = scn.GetLSCard("test6").getBlueprint();

        assertEquals("You Must Confront Vader", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.JEDI_TEST);
        }});
        assertEquals(6, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEATH_STAR_II);
            add(Icon.JEDI_TEST);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>() {{
            add(Keyword.JEDI_TEST_6);
        }});
        assertEquals(ExpansionSet.DEATH_STAR_II, card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
    }

    @Test
    public void YouMustConfrontVaderMayAttemptWhenApprenticeIsWithVader() {
        var scn = GetScenario();

        var test6 = scn.GetLSCard("test6");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, luke, vader);
        scn.MoveCardsToLSSideOfTable(test6);
        test6.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, null, luke, Filters.sameCardId(luke));

        scn.SkipToLSTurn(Phase.MOVE);

        assertTrue(scn.IsCardActive(luke));
        assertTrue(scn.LSCardActionAvailable(test6, "Attempt Jedi Test"));
    }

    @Test
    public void YouMustConfrontVaderMayNotAttemptWhenApprenticeIsNotWithVader() {
        var scn = GetScenario();

        var test6 = scn.GetLSCard("test6");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();
        var site2 = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToLocation(site2, vader);
        test6.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, null, luke, Filters.sameCardId(luke));
        scn.MoveCardsToLSSideOfTable(test6);

        scn.SkipToLSTurn(Phase.MOVE);

        assertFalse(scn.LSCardActionAvailable(test6, "Attempt Jedi Test"));
    }

    @Test
    public void YouMustConfrontVaderMayAttemptWhenApprenticeIsNonFrozenCaptive() {
        var scn = GetScenario();

        var test6 = scn.GetLSCard("test6");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, luke, vader);
        scn.MoveCardsToLSSideOfTable(test6);
        test6.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, null, luke, Filters.sameCardId(luke));
        scn.CaptureCardWith(vader, luke);

        scn.SkipToLSTurn(Phase.MOVE);

        assertTrue(luke.isCaptive());
        assertFalse(scn.IsCardActive(luke));
        assertTrue(scn.LSCardActionAvailable(test6, "Attempt Jedi Test"));
    }

    @Test
    public void YouMustConfrontVaderMayNotAttemptWhenApprenticeIsFrozenCaptive() {
        var scn = GetScenario();

        var test6 = scn.GetLSCard("test6");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, luke, vader);
        scn.MoveCardsToLSSideOfTable(test6);
        test6.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, null, luke, Filters.sameCardId(luke));
        scn.CaptureCardWith(vader, luke);
        scn.FreezeCard(luke);

        scn.SkipToLSTurn(Phase.MOVE);

        assertTrue(luke.isCaptive());
        assertTrue(luke.isFrozen());
        assertFalse(scn.IsCardActive(luke));
        assertFalse(scn.LSCardActionAvailable(test6, "Attempt Jedi Test"));
    }

    @Test
    public void YouMustConfrontVaderMayNotAttemptWhenApprenticeIsMissing() {
        var scn = GetScenario();

        var test6 = scn.GetLSCard("test6");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToLSSideOfTable(test6);
        test6.setTargetedCard(TargetId.JEDI_TEST_APPRENTICE, null, luke, Filters.sameCardId(luke));
        scn.MakeCardGoMissing(luke);

        scn.SkipToLSTurn(Phase.MOVE);

        assertTrue(luke.isMissing());
        assertFalse(scn.IsCardActive(luke));
        assertFalse(scn.LSCardActionAvailable(test6, "Attempt Jedi Test"));
    }
}
