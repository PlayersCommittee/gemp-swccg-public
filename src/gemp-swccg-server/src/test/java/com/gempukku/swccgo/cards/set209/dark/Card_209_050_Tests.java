package com.gempukku.swccgo.cards.set209.dark;

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
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_209_050_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("castle", "209_50");
                    put("vader", "1_168");
                    put("rots", "217_20");
                    put("dooku", "200_76");
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
    public void VadersCastleStatsAndKeywordsAreCorrect() {
        /**
         * Title: Mustafar: Vader's Castle
         * Uniqueness: Unique
         * Side: Dark
         * Type: Location
         * Subtype: Site
         * Destiny: 0
         * Dark Force Icons: 2
         * Light Force Icons: 0
         * Icons: Virtual Set 9, Exterior Site, Planet
         * Keywords: Vader's Castle Site
         * Game Text: Once per game, may [download] Vader here. During your move phase, Vader may move between here and any battleground site.
         * Set: Set 9
         * Rarity: V
         */
        var scn = GetScenario();
        var card = scn.GetDSCard("castle").getBlueprint();

        assertEquals(Title.Vaders_Castle, card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VIRTUAL_SET_9);
            add(Icon.DARK_FORCE);
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
        }});
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(0, card.getIconCount(Icon.LIGHT_FORCE));
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>() {{
            add(Keyword.VADERS_CASTLE_SITE);
        }});
        assertEquals(ExpansionSet.SET_9, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void VadersCastleDownloadsVaderFromReserveDeck() {
        var scn = GetScenario();

        var castle = scn.GetDSCard("castle");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        passStartingInterrupts(scn);
        scn.MoveLocationToTable(castle);
        scn.MoveCardsToDSHand(vader);
        scn.SkipToDSTurn(Phase.DEPLOY);
        ensureForce(scn);
        scn.MoveCardsToTopOfDSReserveDeck(vader);

        assertTrue(scn.DSCardActionAvailable(castle, "Deploy Vader from Reserve Deck"));
        scn.DSUseCardAction(castle, "Deploy Vader from Reserve Deck");
        chooseVaderFromReserveIfOffered(scn, vader);
        scn.PassAllResponses();

        assertAtLocation(castle, vader);
        assertEquals(6, scn.GetDSUsedPileCount());
    }

    @Test
    public void VadersCastleDownloadsChosenApprenticeVaderWhileRevengeOfTheSithOnTable() {
        var scn = GetScenario();

        var castle = scn.GetDSCard("castle");
        var vader = scn.GetDSCard("vader");
        var rots = scn.GetDSCard("rots");
        var dooku = scn.GetDSCard("dooku");

        scn.StartGame();
        passStartingInterrupts(scn);
        grantVaderApprentice(scn, rots, castle);
        scn.MoveCardsToDSHand(dooku, vader);
        skipToDSDeploy(scn);
        scn.MoveCardsToTopOfDSReserveDeck(vader);

        assertTrue(scn.game().getModifiersQuerying().hasKeyword(scn.gameState(), vader, Keyword.SITH_APPRENTICE));
        assertFalse(scn.DSDeployAvailable(dooku));
        assertTrue(scn.DSCardActionAvailable(castle, "Deploy Vader from Reserve Deck"));
        scn.DSUseCardAction(castle, "Deploy Vader from Reserve Deck");
        chooseVaderFromReserveIfOffered(scn, vader);
        scn.PassAllResponses();

        assertAtLocation(castle, vader);
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
        scn.SkipToDSTurn(Phase.DEPLOY);
        ensureForce(scn);
    }

    private void ensureForce(VirtualTableScenario scn) {
        int need = 8 - scn.GetDSForcePileCount();
        if (need > 0 && scn.GetDSReserveDeckCount() > need) {
            scn.DSActivateForceCheat(need);
        }
    }

    private void chooseVaderFromReserveIfOffered(VirtualTableScenario scn, PhysicalCardImpl vader) {
        if (scn.DSDecisionAvailable("Choose card from Reserve Deck")
                || scn.DSDecisionAvailable("Choose card to deploy")) {
            scn.DSChooseCard(vader);
        }
    }

    private void grantVaderApprentice(VirtualTableScenario scn, PhysicalCardImpl rots, PhysicalCardImpl castle) {
        scn.MoveCardsToDSSideOfTable(rots);
        if (scn.DSDecisionAvailable("Choose an apprentice")) {
            scn.DSChoose("Vader");
            if (scn.DSDecisionAvailable("Choose card from Reserve Deck")
                    || scn.DSDecisionAvailable("Choose card to deploy")) {
                scn.DSChooseCard(castle);
            }
            scn.PassAllResponses();
        }
        else {
            scn.game().getModifiersEnvironment().addUntilEndOfGameModifier(
                    new KeywordModifier(rots, Filters.Vader, Keyword.SITH_APPRENTICE));
        }
        if (castle.getZone() != Zone.LOCATIONS) {
            scn.MoveLocationToTable(castle);
        }
    }
}
