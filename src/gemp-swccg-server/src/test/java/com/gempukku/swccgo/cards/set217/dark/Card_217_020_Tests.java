package com.gempukku.swccgo.cards.set217.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_217_020_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("rots", "217_20");
                    put("vader1", "9_113");
                    put("vader2", "1_168");
                    put("dooku", "200_76");
                    put("sidious", "14_78");
                    put("castle", "209_50");
                    put("bridge", "211_20");
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
    public void RevengeOfTheSithStatsAndKeywordsAreCorrect() {
        /**
         * Title: Revenge Of The Sith
         * Uniqueness: Unrestricted
         * Side: Dark
         * Type: Epic Event
         * Destiny: 0
         * Deploy: 0
         * Icons: Episode I, Virtual Set 17, Epic Event
         * Game Text: Deploy on table (only at start of game) and choose an apprentice:
         *         Maul: Deploy Desert Landing Site.
         *         Dooku: Deploy Invisible Hand: Bridge.
         *         Vader: Deploy Vader's Castle.
         *         You may not deploy Dark Jedi except [Episode I] Sidious and the chosen apprentice.
         *         Your [Episode I] Sidious and the chosen apprentice gain [Sith].
         * Set: Set 17
         * Rarity: V
         */
        var scn = GetScenario();
        var card = scn.GetDSCard("rots").getBlueprint();

        assertEquals("Revenge Of The Sith", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(0, card.getDeployCost(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EPIC_EVENT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.VIRTUAL_SET_17);
            add(Icon.EPIC_EVENT);
        }});
        assertEquals(ExpansionSet.SET_17, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RevengeOfTheSithChosenVaderMayDeployWhileOtherDarkJediMayNot() {
        var scn = GetScenario();

        var rots = scn.GetDSCard("rots");
        var vader1 = scn.GetDSCard("vader1");
        var dooku = scn.GetDSCard("dooku");

        scn.StartGame();
        passStartingInterrupts(scn);
        scn.MoveCardsToDSHand(vader1, dooku);
        grantVaderApprentice(scn, rots);
        skipToDSDeploy(scn);

        assertTrue(scn.game().getModifiersQuerying().hasKeyword(scn.gameState(), vader1, Keyword.SITH_APPRENTICE));
        assertTrue(scn.game().getModifiersQuerying().getCardTypes(scn.gameState(), vader1).contains(CardType.SITH));
        assertTrue(scn.DSDeployAvailable(vader1));
        assertFalse(scn.DSDeployAvailable(dooku));
    }

    @Test
    public void RevengeOfTheSithChosenVaderKeepsSithAfterBeingLost() {
        var scn = GetScenario();

        var rots = scn.GetDSCard("rots");
        var vader1 = scn.GetDSCard("vader1");
        var vader2 = scn.GetDSCard("vader2");
        var dooku = scn.GetDSCard("dooku");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        passStartingInterrupts(scn);
        scn.MoveCardsToDSHand(vader1, vader2, dooku);
        grantVaderApprentice(scn, rots);
        skipToDSDeploy(scn);

        assertTrue(scn.DSDeployAvailable(vader1));

        scn.MoveCardsToLocation(site, vader1);
        assertEquals(Zone.AT_LOCATION, vader1.getZone());
        scn.MoveCardsToTopOfDSLostPile(vader1);

        assertTrue(scn.game().getModifiersQuerying().hasKeyword(scn.gameState(), vader2, Keyword.SITH_APPRENTICE));
        assertTrue(scn.game().getModifiersQuerying().getCardTypes(scn.gameState(), vader2).contains(CardType.SITH));
        assertTrue("Chosen apprentice Vader must still deploy after a previous Vader was lost",
                scn.DSDeployAvailable(vader2));
        assertFalse(scn.DSDeployAvailable(dooku));
    }

    @Test
    public void RevengeOfTheSithChosenVaderMayRedeployNextTurnAfterBeingLost() {
        var scn = GetScenario();

        var rots = scn.GetDSCard("rots");
        var vader1 = scn.GetDSCard("vader1");
        var vader2 = scn.GetDSCard("vader2");
        var dooku = scn.GetDSCard("dooku");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        passStartingInterrupts(scn);
        scn.MoveCardsToDSHand(vader1, vader2, dooku);
        grantVaderApprentice(scn, rots);
        skipToDSDeploy(scn);

        scn.MoveCardsToLocation(site, vader1);
        scn.MoveCardsToTopOfDSLostPile(vader1);
        scn.SkipToDSTurn(Phase.DEPLOY);
        ensureForce(scn);

        assertTrue(scn.game().getModifiersQuerying().hasKeyword(scn.gameState(), vader2, Keyword.SITH_APPRENTICE));
        assertTrue(scn.game().getModifiersQuerying().getCardTypes(scn.gameState(), vader2).contains(CardType.SITH));
        assertTrue("Chosen apprentice Vader must still deploy on a later turn after a previous Vader was lost",
                scn.DSDeployAvailable(vader2));
        assertFalse(scn.DSDeployAvailable(dooku));
    }

    @Test
    public void RevengeOfTheSithChosenDookuAndEpisodeISidiousAreDestinyPlusTwo() {
        var scn = GetScenario();

        var rots = scn.GetDSCard("rots");
        var dooku = scn.GetDSCard("dooku");
        var sidious = scn.GetDSCard("sidious");
        var vader = scn.GetDSCard("vader2");

        scn.StartGame();
        passStartingInterrupts(scn);
        scn.MoveCardsToDSHand(dooku, sidious, vader);
        grantDookuApprentice(scn, rots);
        skipToDSDeploy(scn);

        assertTrue(scn.game().getModifiersQuerying().hasKeyword(scn.gameState(), dooku, Keyword.SITH_APPRENTICE));
        assertTrue(scn.game().getModifiersQuerying().getCardTypes(scn.gameState(), dooku).contains(CardType.SITH));
        assertTrue(scn.game().getModifiersQuerying().getCardTypes(scn.gameState(), sidious).contains(CardType.SITH));
        assertFalse(scn.game().getModifiersQuerying().getCardTypes(scn.gameState(), vader).contains(CardType.SITH));
        assertEquals(3, scn.GetDestiny(dooku));
        assertEquals(3, scn.GetDestiny(sidious));
        assertEquals(1, scn.GetDestiny(vader));
    }

    private void passStartingInterrupts(VirtualTableScenario scn) {
        if (scn.DSDecisionAvailable("Choose starting interrupt")) {
            scn.DSPass();
        }
        if (scn.LSDecisionAvailable("Choose starting interrupt")) {
            scn.LSPass();
        }
    }

    private void skipToDSDeploy(VirtualTableScenario scn) {
        scn.SkipToDSTurn(Phase.DEPLOY);
        // Cheating Revenge Of The Sith onto table during the opponent's turn leaves the first
        // DS Deploy window with no actions; the following DS Deploy phase is live.
        scn.SkipToDSTurn(Phase.DEPLOY);
        ensureForce(scn);
    }

    private void ensureForce(VirtualTableScenario scn) {
        int need = 8 - scn.GetDSForcePileCount();
        if (need > 0 && scn.GetDSReserveDeckCount() > need) {
            scn.DSActivateForceCheat(need);
        }
    }

    private void grantVaderApprentice(VirtualTableScenario scn, PhysicalCardImpl rots) {
        scn.MoveCardsToDSSideOfTable(rots);
        if (scn.DSDecisionAvailable("Choose an apprentice")) {
            scn.DSChoose("Vader");
            if (scn.DSDecisionAvailable("Choose card from Reserve Deck")
                    || scn.DSDecisionAvailable("Choose card to deploy")) {
                scn.DSChooseCard(scn.GetDSCard("castle"));
            }
            scn.PassAllResponses();
        }
        else {
            scn.game().getModifiersEnvironment().addUntilEndOfGameModifier(
                    new KeywordModifier(rots, Filters.Vader, Keyword.SITH_APPRENTICE));
        }
    }

    private void grantDookuApprentice(VirtualTableScenario scn, PhysicalCardImpl rots) {
        scn.MoveCardsToDSSideOfTable(rots);
        if (scn.DSDecisionAvailable("Choose an apprentice")) {
            scn.DSChoose("Dooku");
            if (scn.DSDecisionAvailable("Choose card from Reserve Deck")
                    || scn.DSDecisionAvailable("Choose card to deploy")) {
                scn.DSChooseCard(scn.GetDSCard("bridge"));
            }
            scn.PassAllResponses();
        }
        else {
            scn.game().getModifiersEnvironment().addUntilEndOfGameModifier(
                    new KeywordModifier(rots, Filters.Dooku, Keyword.SITH_APPRENTICE));
        }
    }
}
