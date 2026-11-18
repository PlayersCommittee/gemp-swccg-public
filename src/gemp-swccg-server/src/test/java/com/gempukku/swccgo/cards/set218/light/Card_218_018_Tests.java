package com.gempukku.swccgo.cards.set218.light;

import com.gempukku.swccgo.common.CardSubtype;
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

public class Card_218_018_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("walkway", "218_018");
                    put("slug", "200_42");
                    put("luke", "1_019");
                    put("leia", "1_017");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
    public void DeathStarIITurboliftWalkwayStatsAndKeywordsAreCorrect() {
        /**
         * Title: Death Star II: Turbolift Walkway
         * Uniqueness: Unique
         * Side: Light
         * Type: Location
         * Subtype: Site
         * Destiny: 0
         * Light Force Icons: 2
         * Dark Force Icons: 1
         * Icons: Death Star II, Virtual Set 18, Interior Site, Mobile, Scomp Link
         * Game Text: Dark: If you have more than two characters here, you must target one to be lost (cannot be prevented).
         *          Light: If you have more than one character here, you must target one to be lost (cannot be prevented).
         * Set: Set 18
         * Rarity: V
         */
        var scn = GetScenario();
        var card = scn.GetLSCard("walkway").getBlueprint();

        assertEquals("Death Star II: Turbolift Walkway", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEATH_STAR_II);
            add(Icon.VIRTUAL_SET_18);
            add(Icon.DARK_FORCE);
            add(Icon.INTERIOR_SITE);
            add(Icon.LIGHT_FORCE);
            add(Icon.MOBILE);
            add(Icon.SCOMP_LINK);
        }});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        assertEquals(ExpansionSet.SET_18, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DeathStarIITurboliftWalkwayLightMustLoseOneWhenTwoCharactersHere() {
        var scn = GetScenario();
        var walkway = scn.GetLSCard("walkway");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");

        scn.StartGame();
        scn.MoveLocationToTable(walkway);
        scn.MoveCardsToLocation(walkway, luke, leia);
        try {
            scn.SkipToLSTurn(Phase.CONTROL);
        } catch (RuntimeException ignored) {
            // Required Walkway trigger is expected to interrupt the skip.
        }

        assertTrue("Walkway must force a Light character lost; got: " + decisionText(scn),
                scn.LSDecisionAvailable("Choose a character to be lost"));
        scn.LSChooseCard(luke);
        scn.PassAllResponses();

        assertTrue("Luke must be lost; zone=" + luke.getZone(),
                luke.getZone() == Zone.LOST_PILE || luke.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue("Leia stays at Walkway; zone=" + leia.getZone(),
                leia.getZone() == Zone.AT_LOCATION);
    }

    @Test
    public void DeathStarIITurboliftWalkwayLossCannotBeDelayedByKlorslugDuringBattle() {
        var scn = GetScenario();
        var walkway = scn.GetLSCard("walkway");
        var slug = scn.GetLSCard("slug");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(walkway);
        scn.MoveCardsToLSSideOfTable(slug);
        scn.MoveCardsToLocation(walkway, leia, vader);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.MoveCardsToLocation(walkway, luke);
        assertTrue(scn.LSCanInitiateBattle(walkway));
        scn.LSUseCardAction(walkway, "Initiate battle");
        scn.PassForceUseResponses();

        assertTrue("Walkway still forces a Light character lost during battle with K'lor'slug; got: " + decisionText(scn),
                scn.LSDecisionAvailable("Choose a character to be lost"));
        scn.LSChooseCard(luke);
        scn.PassAllResponses();
        assertTrue("Walkway loss cannot be delayed by K'lor'slug; Luke zone=" + luke.getZone(),
                luke.getZone() == Zone.LOST_PILE || luke.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue("Leia remains; zone=" + leia.getZone(),
                leia.getZone() == Zone.AT_LOCATION);
        assertFalse("Walkway must not keep asking after one Light character remains",
                scn.LSDecisionAvailable("Choose a character to be lost"));
    }

    private static String decisionText(VirtualTableScenario scn) {
        var d = scn.GetCurrentDecision();
        return d == null ? "null" : d.getText();
    }
}
