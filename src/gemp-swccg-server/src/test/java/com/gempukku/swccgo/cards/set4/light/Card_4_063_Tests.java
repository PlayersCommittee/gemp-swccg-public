package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for This Is No Cave (4_063 / blueprint 4_63).
 * Closes #108.
 */
public class Card_4_063_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("caveInterrupt", "4_063");
                    put("falcon", "1_143");
                    put("han", "1_11");
                    put("xwing", "1_146");
                    put("bigOne", "4_082");
                    put("belly", "4_083");
                    put("asteroid", "4_081");
                    put("slug", "4_006");
                    put("transport", "3_065");
                    put("anoat", "4_079");
                }},
                new HashMap<>() {{
                    put("tie", "1_304");
                    put("barrier", "1_249");
                    put("corrosive", "4_119");
                    put("dsBigOne", "4_156");
                    put("dsBelly", "4_157");
                    put("dsSlug", "4_112");
                    put("dsAsteroid", "4_155");
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

    private void putBigOneAndBelly(VirtualTableScenario scn) {
        var bigOne = scn.GetLSCard("bigOne");
        var belly = scn.GetLSCard("belly");
        scn.MoveLocationToTable(bigOne);
        scn.MoveLocationToTable(belly);
    }

    @Test
    public void ThisIsNoCave_4_063_StatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("caveInterrupt").getBlueprint();

        assertEquals(Title.This_Is_No_Cave, card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(ExpansionSet.DAGOBAH, card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        assertTrue(card.getGameText().contains("Relocate one starfighter"));
        assertTrue(card.getGameText().contains("Cancel Corrosive Damage"));
        assertTrue(card.getLore().contains("900 meters"));
    }

    @Test
    public void ThisIsNoCave_4_063_MayPlayWhenStarfighterAtBigOneAndRelatedSiteOnTable() {
        var scn = GetScenario();
        var caveInterrupt = scn.GetLSCard("caveInterrupt");
        var falcon = scn.GetLSCard("falcon");
        var bigOne = scn.GetLSCard("bigOne");
        var belly = scn.GetLSCard("belly");

        scn.StartGame();
        scn.MoveCardsToLSHand(caveInterrupt);
        putBigOneAndBelly(scn);
        scn.MoveCardsToLocation(bigOne, falcon);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        assertTrue(scn.LSCardPlayAvailable(caveInterrupt));
        scn.LSPlayCard(caveInterrupt);
        assertTrue(scn.LSHasCardChoiceAvailable(falcon));
        scn.LSChooseCard(falcon);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(belly, falcon));
        assertTrue(caveInterrupt.getZone() == Zone.USED_PILE || scn.GetLSUsedPile().contains(caveInterrupt));
    }

    @Test
    public void ThisIsNoCave_4_063_MayPlayWhenStarfighterAtRelatedSite() {
        var scn = GetScenario();
        var caveInterrupt = scn.GetLSCard("caveInterrupt");
        var falcon = scn.GetLSCard("falcon");
        var bigOne = scn.GetLSCard("bigOne");
        var belly = scn.GetLSCard("belly");

        scn.StartGame();
        scn.MoveCardsToLSHand(caveInterrupt);
        putBigOneAndBelly(scn);
        scn.MoveCardsToLocation(belly, falcon);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        assertTrue(scn.LSCardPlayAvailable(caveInterrupt));
        scn.LSPlayCard(caveInterrupt);
        scn.LSChooseCard(falcon);
        scn.PassAllResponses();

        assertTrue(scn.CardsAtLocation(bigOne, falcon));
    }

    @Test
    public void ThisIsNoCave_4_063_MayNotPlayIfStarfighterNotAtBigOneOrRelatedSite() {
        var scn = GetScenario();
        var caveInterrupt = scn.GetLSCard("caveInterrupt");
        var falcon = scn.GetLSCard("falcon");
        var asteroid = scn.GetLSCard("asteroid");

        scn.StartGame();
        scn.MoveCardsToLSHand(caveInterrupt);
        putBigOneAndBelly(scn);
        scn.MoveLocationToTable(asteroid);
        scn.MoveCardsToLocation(asteroid, falcon);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        assertFalse(scn.LSCardPlayAvailable(caveInterrupt));
    }

    @Test
    public void ThisIsNoCave_4_063_MayNotPlayIfRelatedSiteNotOnTable() {
        var scn = GetScenario();
        var caveInterrupt = scn.GetLSCard("caveInterrupt");
        var falcon = scn.GetLSCard("falcon");
        var bigOne = scn.GetLSCard("bigOne");

        scn.StartGame();
        scn.MoveCardsToLSHand(caveInterrupt);
        scn.MoveLocationToTable(bigOne);
        scn.MoveCardsToLocation(bigOne, falcon);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        assertFalse(scn.LSCardPlayAvailable(caveInterrupt));
    }

    @Test
    public void ThisIsNoCave_4_063_MayNotTargetCapitalStarshipEvenIfMovesLikeStarfighter() {
        var scn = GetScenario();
        var caveInterrupt = scn.GetLSCard("caveInterrupt");
        var transport = scn.GetLSCard("transport");
        var bigOne = scn.GetLSCard("bigOne");

        scn.StartGame();
        scn.MoveCardsToLSHand(caveInterrupt);
        putBigOneAndBelly(scn);
        scn.MoveCardsToLocation(bigOne, transport);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        assertFalse(scn.LSCardPlayAvailable(caveInterrupt));
    }

    @Test
    public void ThisIsNoCave_4_063_MayTargetOpponentsStarfighter() {
        var scn = GetScenario();
        var caveInterrupt = scn.GetLSCard("caveInterrupt");
        var bigOne = scn.GetLSCard("bigOne");
        var belly = scn.GetLSCard("belly");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveCardsToLSHand(caveInterrupt);
        putBigOneAndBelly(scn);
        // TIE in belly taking off to Big One sector (not landing at a site)
        scn.MoveCardsToLocation(belly, tie);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        assertTrue(scn.LSCardPlayAvailable(caveInterrupt));
        scn.LSPlayCard(caveInterrupt);
        assertTrue(scn.LSHasCardChoiceAvailable(tie));
        scn.LSChooseCard(tie);
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(bigOne, tie));
    }

    @Test
    public void ThisIsNoCave_4_063_OpensMouthIfClosed() {
        var scn = GetScenario();
        var caveInterrupt = scn.GetLSCard("caveInterrupt");
        var falcon = scn.GetLSCard("falcon");
        var bigOne = scn.GetLSCard("bigOne");
        var belly = scn.GetLSCard("belly");
        var slug = scn.GetLSCard("slug");

        scn.StartGame();
        scn.MoveCardsToLSHand(caveInterrupt);
        putBigOneAndBelly(scn);
        scn.MoveCardsToLocation(bigOne, falcon, slug);
        slug.setMouthClosed(true);
        assertTrue(slug.isMouthClosed());

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        assertTrue(scn.LSCardPlayAvailable(caveInterrupt));
        scn.LSPlayCard(caveInterrupt);
        scn.LSChooseCard(falcon);
        scn.PassAllResponses();

        assertFalse("Mouth should open when closed", slug.isMouthClosed());
        assertTrue(scn.CardsAtLocation(belly, falcon));
    }

    @Test
    public void ThisIsNoCave_4_063_LeavesMouthOpenIfAlreadyOpen() {
        var scn = GetScenario();
        var caveInterrupt = scn.GetLSCard("caveInterrupt");
        var falcon = scn.GetLSCard("falcon");
        var bigOne = scn.GetLSCard("bigOne");
        var belly = scn.GetLSCard("belly");
        var slug = scn.GetLSCard("slug");

        scn.StartGame();
        scn.MoveCardsToLSHand(caveInterrupt);
        putBigOneAndBelly(scn);
        scn.MoveCardsToLocation(bigOne, falcon, slug);
        assertFalse(slug.isMouthClosed());

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        scn.LSPlayCard(caveInterrupt);
        scn.LSChooseCard(falcon);
        scn.PassAllResponses();

        assertFalse(slug.isMouthClosed());
        assertTrue(scn.CardsAtLocation(belly, falcon));
    }

    @Test
    public void ThisIsNoCave_4_063_SmugglerAboardGrantsImmunityUntilEndOfYourNextTurn() {
        var scn = GetScenario();
        var caveInterrupt = scn.GetLSCard("caveInterrupt");
        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        var bigOne = scn.GetLSCard("bigOne");

        scn.StartGame();
        scn.MoveCardsToLSHand(caveInterrupt);
        putBigOneAndBelly(scn);
        scn.MoveCardsToLocation(bigOne, falcon);
        scn.BoardAsPilot(falcon, han);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        scn.LSPlayCard(caveInterrupt);
        scn.LSChooseCard(falcon);
        scn.PassAllResponses();

        assertTrue(scn.game().getModifiersQuerying().hasAnyImmunityToAttrition(scn.gameState(), falcon));

        // Still active during your next turn
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.game().getModifiersQuerying().hasAnyImmunityToAttrition(scn.gameState(), falcon));

        // Expired after that turn ends
        scn.SkipToDSTurn(Phase.ACTIVATE);
        assertFalse(scn.game().getModifiersQuerying().hasAnyImmunityToAttrition(scn.gameState(), falcon));
    }

    @Test
    public void ThisIsNoCave_4_063_NoSmugglerAboardNoBuffs() {
        var scn = GetScenario();
        var caveInterrupt = scn.GetLSCard("caveInterrupt");
        var xwing = scn.GetLSCard("xwing");
        var bigOne = scn.GetLSCard("bigOne");

        scn.StartGame();
        scn.MoveCardsToLSHand(caveInterrupt);
        putBigOneAndBelly(scn);
        scn.MoveCardsToLocation(bigOne, xwing);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        scn.LSPlayCard(caveInterrupt);
        scn.LSChooseCard(xwing);
        scn.PassAllResponses();

        assertFalse(scn.game().getModifiersQuerying().hasAnyImmunityToAttrition(scn.gameState(), xwing));
    }

    @Test
    public void ThisIsNoCave_4_063_RelocateRestrictedToRelatedSite() {
        var scn = GetScenario();
        var caveInterrupt = scn.GetLSCard("caveInterrupt");
        var falcon = scn.GetLSCard("falcon");
        var bigOne = scn.GetLSCard("bigOne");
        var belly = scn.GetLSCard("belly");

        scn.StartGame();
        scn.MoveCardsToLSHand(caveInterrupt);
        putBigOneAndBelly(scn);
        scn.MoveCardsToLocation(bigOne, falcon);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        scn.LSPlayCard(caveInterrupt);
        scn.LSChooseCard(falcon);
        scn.PassAllResponses();

        // Must relocate to the related belly for this Big One
        assertTrue(scn.CardsAtLocation(belly, falcon));
        assertTrue(Filters.relatedSite(bigOne).accepts(scn.game(), belly));
    }

    @Test
    public void ThisIsNoCave_4_063_CancelsCorrosiveDamageOnTable() {
        var scn = GetScenario();
        var caveInterrupt = scn.GetLSCard("caveInterrupt");
        var belly = scn.GetLSCard("belly");
        var slug = scn.GetLSCard("slug");
        var bigOne = scn.GetLSCard("bigOne");
        var corrosive = scn.GetDSCard("corrosive");

        scn.StartGame();
        scn.MoveCardsToLSHand(caveInterrupt);
        putBigOneAndBelly(scn);
        scn.MoveCardsToLocation(bigOne, slug);
        scn.AttachCardsTo(belly, corrosive);

        assertEquals(belly, corrosive.getAttachedTo());
        assertTrue(Filters.Corrosive_Damage.accepts(scn.game(), corrosive));
        assertTrue(com.gempukku.swccgo.cards.GameConditions.canTargetToCancel(scn.game(), caveInterrupt, Filters.Corrosive_Damage));

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        assertTrue("Cancel Corrosive Damage should be playable", scn.LSCardPlayAvailable(caveInterrupt));
        if (scn.LSCardPlayAvailable(caveInterrupt, "Corrosive")) {
            scn.LSPlayCard(caveInterrupt, "Corrosive");
        } else {
            scn.LSPlayCard(caveInterrupt);
        }
        if (scn.LSHasCardChoiceAvailable(corrosive)) {
            scn.LSChooseCard(corrosive);
        }
        scn.PassAllResponses();

        assertTrue(corrosive.getZone() == Zone.LOST_PILE || scn.GetDSLostPile().contains(corrosive));
        assertTrue(caveInterrupt.getZone() == Zone.USED_PILE || scn.GetLSUsedPile().contains(caveInterrupt));
    }

    @Test
    public void ThisIsNoCave_4_063_MayNotCancelWhenCorrosiveDamageNotOnTable() {
        var scn = GetScenario();
        var caveInterrupt = scn.GetLSCard("caveInterrupt");

        scn.StartGame();
        scn.MoveCardsToLSHand(caveInterrupt);

        scn.SkipToPhase(Phase.CONTROL);
        scn.DSPass();

        assertFalse(scn.LSCardPlayAvailable(caveInterrupt, "Corrosive"));
        assertFalse(scn.LSCardPlayAvailable(caveInterrupt));
    }
}
