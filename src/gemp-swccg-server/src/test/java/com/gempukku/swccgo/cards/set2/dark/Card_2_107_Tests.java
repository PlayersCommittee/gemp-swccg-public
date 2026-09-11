package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_2_107_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("qam", "210_024"); // Quite A Mercenary (V)
                }},
                new HashMap<>()
                {{
                    put("u3po", "2_107"); // U-3PO (Yoo-Threepio)
                    put("ohSwitchOff", "3_130"); // Oh, Switch Off
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
    public void U3POStatsAndKeywordsAreCorrect() {
        /**
         * Title: U-3PO (Yoo-Threepio)
         * Uniqueness: Unique
         * Side: Dark
         * Type: Character
         * Subtype: Droid
         * Destiny: 3
         * Deploy: 3
         * Power: 1
         * Forfeit: 3
         * Icons: A New Hope
         * Model Type: Protocol
         * Keywords: Spy
         * Game Text: Deploys only to a site as an Undercover spy. If Undercover at a battle, adds his power to Light Side.
         *      If U-3PO just had his 'cover broken,' Light Side may steal him.
         * Set: A New Hope
         * Rarity: R1
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("u3po").getBlueprint();

        assertEquals("U-3PO (Yoo-Threepio)", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SPY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.PROTOCOL);
        }});
        assertEquals(com.gempukku.swccgo.common.ExpansionSet.A_NEW_HOPE, card.getExpansionSet());
        assertEquals(Rarity.R1, card.getRarity());
    }

    @Test
    public void OhSwitchOffStatsAndKeywordsAreCorrect() {
        /**
         * Title: Oh, Switch Off
         * Uniqueness: Unrestricted
         * Side: Dark
         * Type: Interrupt
         * Subtype: Used
         * Destiny: 6
         * Icons: Hoth
         * Game Text: Cancel an attempt by opponent to target your droid to be stolen, 'hit' or lost.
         *      Droid is protected from all such attempts for remainder of turn.
         *      OR Switch OFF any binary droid for remainder of turn.
         * Set: Hoth
         * Rarity: C2
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("ohSwitchOff").getBlueprint();

        assertEquals("Oh, Switch Off", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
        }});
        assertEquals(com.gempukku.swccgo.common.ExpansionSet.HOTH, card.getExpansionSet());
        assertEquals(Rarity.C2, card.getRarity());
    }

    @Test
    public void OhSwitchOffCanCancelStealOfU3POAfterCoverBroken() {
        // Repro (#50): LS breaks U-3PO cover (Quite A Mercenary (V)), chooses steal;
        // Oh, Switch Off should light up and keep U-3PO as a DS droid.
        var scn = GetScenario();

        var qam = scn.GetLSCard("qam");
        var u3po = scn.GetDSCard("u3po");
        var ohSwitchOff = scn.GetDSCard("ohSwitchOff");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(qam);
        scn.MoveCardsToDSHand(u3po, ohSwitchOff);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(u3po);
        scn.DSChooseCard(site);
        scn.PassAllResponses();

        assertTrue(u3po.isUndercover());
        assertEquals(scn.DS, u3po.getOwner());

        scn.SkipToLSTurn(Phase.MOVE);
        assertTrue(scn.LSCardPlayAvailable(qam));
        scn.LSPlayCard(qam);
        // LOST: Break a spy's cover
        assertTrue(scn.LSDecisionAvailable("Choose undercover spy") || scn.LSHasCardChoiceAvailable(u3po) || scn.LSDecisionAvailable("Break"));
        if (scn.LSHasCardChoiceAvailable(u3po)) {
            scn.LSChooseCard(u3po);
        }
        scn.PassAllResponses();

        assertFalse(u3po.isUndercover());
        assertTrue(scn.LSDecisionAvailable("steal"));
        scn.LSChooseYes();

        // Steal targeting - optional responses; Oh, Switch Off should be available
        assertTrue(scn.DSCardPlayAvailable(ohSwitchOff));
        scn.DSPlayCard(ohSwitchOff);
        scn.PassAllResponses();

        assertEquals(scn.DS, u3po.getOwner());
        assertTrue(scn.CardsAtLocation(site, u3po));
    }

    @Test
    public void LightSideMayStealU3POWhenCoverBrokenIfOhSwitchOffNotPlayed() {
        var scn = GetScenario();

        var qam = scn.GetLSCard("qam");
        var u3po = scn.GetDSCard("u3po");
        var ohSwitchOff = scn.GetDSCard("ohSwitchOff");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLSHand(qam);
        scn.MoveCardsToDSHand(u3po, ohSwitchOff);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(u3po);
        scn.DSChooseCard(site);
        scn.PassAllResponses();

        assertTrue(u3po.isUndercover());

        scn.SkipToLSTurn(Phase.MOVE);
        scn.LSPlayCard(qam);
        if (scn.LSHasCardChoiceAvailable(u3po)) {
            scn.LSChooseCard(u3po);
        }
        scn.PassAllResponses();

        assertTrue(scn.LSDecisionAvailable("steal"));
        scn.LSChooseYes();

        assertTrue(scn.DSCardPlayAvailable(ohSwitchOff));
        scn.PassAllResponses();

        assertEquals(scn.LS, u3po.getOwner());
        assertTrue(scn.CardsAtLocation(site, u3po));
    }
}
