package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1_125_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("trashCompactor", "1_125"); // Death Star: Trash Compactor
                    put("han", "1_011"); // Han Solo
                    put("xwing", "1_146"); // X-wing
                }},
                new HashMap<>() {{
                    put("deathStar", "2_143"); // Death Star system
                }},
                20,
                20,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void TrashCompactorStatsAndKeywordsAreCorrect() {
        /**
         * Title: Death Star: Trash Compactor
         * Uniqueness: Unique
         * Side: Light
         * Type: Location
         * Subtype: Site
         * Icons: Interior site, Mobile
         * Game Text: You may deploy here without presence. If you control, Force drain +1 here.
         * Set: Premiere
         * Rarity: U1
         */
        var scn = GetScenario();
        var card = scn.GetLSCard("trashCompactor").getBlueprint();
        assertEquals("Death Star: Trash Compactor", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(ExpansionSet.PREMIERE, card.getExpansionSet());
        assertEquals(Rarity.U1, card.getRarity());
        assertTrue(card.hasIcon(Icon.INTERIOR_SITE));
        assertTrue(card.hasIcon(Icon.MOBILE));
    }

    @Test
    public void TrashCompactorStillAllowsHanDeployToTrashCompactorSite() {
        // Site-only ignore presence still works at Trash Compactor; not at Death Star system
        var scn = GetScenario();

        var trashCompactor = scn.GetLSCard("trashCompactor");
        var han = scn.GetLSCard("han");
        var deathStar = scn.GetDSCard("deathStar");

        scn.StartGame();
        scn.MoveLocationToTable(deathStar);
        scn.MoveLocationToTable(trashCompactor);
        scn.MoveCardsToLSHand(han);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSActivateForceCheat(5);

        assertTrue(scn.LSDeployAvailable(han));
        scn.LSDeployCard(han);
        assertTrue(scn.LSHasCardChoiceAvailable(trashCompactor));
        assertFalse(scn.LSHasCardChoiceAvailable(deathStar));
    }

    @Test
    public void TrashCompactorDoesNotAllowPermanentPilotXwingDeployToDeathStarSystem() {
        // #736 / same root #47: permanent-pilot ships stay correctly blocked at Death Star system
        var scn = GetScenario();

        var trashCompactor = scn.GetLSCard("trashCompactor");
        var xwing = scn.GetLSCard("xwing");
        var deathStar = scn.GetDSCard("deathStar");
        var lsSystem = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(deathStar);
        scn.MoveLocationToTable(trashCompactor);
        scn.MoveCardsToLSHand(xwing);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSActivateForceCheat(5);

        assertTrue(scn.LSDeployAvailable(xwing));
        scn.LSDeployCard(xwing);

        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        assertTrue(scn.LSHasCardChoiceAvailable(lsSystem));
        assertFalse(scn.LSHasCardChoiceAvailable(deathStar));
    }
}
