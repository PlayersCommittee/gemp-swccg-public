package com.gempukku.swccgo.cards.set200.light;

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

public class Card_200_042_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("slug", "200_42");
                    put("ittl", "9_4");
                    put("xwing", "1_146");
                    put("db", "1_129");
                }},
                new HashMap<>() {{
                    put("tie", "9_175");
                    put("cannon", "7_324");
                }},
                15,
                15,
                StartingSetup.LSStartingLocation("1_127"),
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void KlorslugVStatsAndKeywordsAreCorrect() {
        /**
         * Title: K'lor'slug
         * Uniqueness: Unique
         * Side: Light
         * Type: Effect
         * Destiny: 3
         * Icons: Virtual Set 0, Effect
         * Keywords: Dejarik
         * Game Text: Deploy on table. If your character, starship, or vehicle in battle is about to be lost
         *         before the damage segment, it is instead lost at end of battle (if forfeited, forfeit for 0).
         *         [Immune to Alter]
         * Set: Set 0
         * Rarity: V
         */
        var scn = GetScenario();
        var card = scn.GetLSCard("slug").getBlueprint();

        assertEquals("K'lor'slug", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEJARIK);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VIRTUAL_SET_0);
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.SET_0, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void KlorslugDoesNotMakeStarfighterLostAgainAfterIllTakeTheLeaderRelocatesIt() {
        var scn = GetScenario();

        var slug = scn.GetLSCard("slug");
        var ittl = scn.GetLSCard("ittl");
        var xwing = scn.GetLSCard("xwing");
        var db = scn.GetLSCard("db");
        var tatooine = scn.GetLSStartingLocation();

        var tie = scn.GetDSCard("tie");
        var cannon = scn.GetDSCard("cannon");

        scn.StartGame();
        scn.MoveLocationToTable(db);
        scn.MoveCardsToLSSideOfTable(slug, ittl);
        scn.MoveCardsToLocation(tatooine, xwing, tie);
        scn.AttachCardsTo(tie, cannon);

        scn.LSActivateForceCheat(5);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareDSDestiny(5);
        scn.DSInitiateBattle(tatooine);
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        scn.DSUseCardAction(cannon, "Fire");
        scn.DSChooseCard(xwing);
        assertTrue(scn.DSDecisionAvailable("Choose number for X"));
        scn.PlayerDecided(scn.DS, "3");
        scn.PassAllResponses();

        scn.SkipToDamageSegment(false);
        if (scn.AwaitingLSBattleDamagePayment()) {
            scn.LSPayRemainingBattleDamageFromReserveDeck();
        }
        scn.PassResponses("ABOUT_TO_BE_LOST");
        assertTrue(scn.LSCardActionAvailable(ittl, "Relocate"));
        scn.LSUseCardAction(ittl, "Relocate");
        if (scn.LSHasCardChoiceAvailable(db)) {
            scn.LSChooseCard(db);
        }
        if (scn.LSHasCardChoiceAvailable(xwing)) {
            scn.LSChooseCard(xwing);
        }
        if (scn.LSHasCardChoiceAvailable(db)) {
            scn.LSChooseCard(db);
        }
        scn.PassAllResponses();

        assertEquals(Zone.AT_LOCATION, xwing.getZone());
        assertEquals(db, xwing.getAtLocation());
        assertFalse(scn.DSDecisionAvailable("Make"));
        assertFalse(scn.LSDecisionAvailable("Make"));
    }
}
