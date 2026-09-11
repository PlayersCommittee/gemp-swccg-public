package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
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

    private void breakU3POCoverWithQAM(VirtualTableScenario scn) {
        var qam = scn.GetLSCard("qam");
        var u3po = scn.GetDSCard("u3po");
        var ohSwitchOff = scn.GetDSCard("ohSwitchOff");
        var site = scn.GetLSStartingLocation();

        scn.MoveCardsToLSHand(qam);
        scn.MoveCardsToDSHand(u3po, ohSwitchOff);

        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(u3po);
        scn.DSChooseCard(site);
        scn.PassAllResponses();

        assertTrue(u3po.isUndercover());
        assertEquals(scn.DS, u3po.getOwner());

        scn.SkipToLSTurn(Phase.MOVE);
        assertTrue(scn.LSCardPlayAvailable(qam, "Break a spy's cover"));
        scn.LSPlayCard(qam, "Break a spy's cover");
        assertTrue(scn.LSHasCardChoiceAvailable(u3po));
        scn.LSChooseCard(u3po);
        scn.PassAllResponses();

        assertFalse(u3po.isUndercover());
    }

    /**
     * After LS chooses to steal, advance until Oh, Switch Off can respond (ABOUT_TO_BE_STOLEN).
     */
    private void advanceToOhSwitchOffResponse(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl ohSwitchOff) {
        for (int i = 0; i < 20; i++) {
            var ds = scn.DSGetDecision();
            if (ds != null && scn.DSCardPlayAvailable(ohSwitchOff)) {
                return;
            }
            var ls = scn.LSGetDecision();
            if (ls != null) {
                String lower = ls.getText() == null ? "" : ls.getText().toLowerCase();
                if (scn.LSDecisionAvailable("steal") || lower.contains("do you want to")) {
                    scn.LSChooseYes();
                    continue;
                }
                if (lower.contains("optional") || lower.contains("about") || lower.contains("cover") || lower.contains("response")) {
                    scn.LSPass();
                    continue;
                }
            }
            if (ds != null) {
                String lower = ds.getText() == null ? "" : ds.getText().toLowerCase();
                if (lower.contains("optional") || lower.contains("about") || lower.contains("cover") || lower.contains("response")) {
                    // Do not pass the Oh Switch Off window
                    if (scn.DSCardPlayAvailable(ohSwitchOff)) {
                        return;
                    }
                    scn.DSPass();
                    continue;
                }
            }
            break;
        }
    }

    @Test
    public void U3POStatsAndKeywordsAreCorrect() {
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
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SPY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.DROID);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.PROTOCOL);
        }});
        assertEquals(ExpansionSet.A_NEW_HOPE, card.getExpansionSet());
        assertEquals(Rarity.R1, card.getRarity());
    }

    @Test
    public void OhSwitchOffStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("ohSwitchOff").getBlueprint();

        assertEquals("Oh, Switch Off", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.HOTH, card.getExpansionSet());
        assertEquals(Rarity.C2, card.getRarity());
    }

    @Test
    public void OhSwitchOffCanCancelStealOfU3POAfterCoverBroken() {
        // Repro (#50): LS breaks U-3PO cover (Quite A Mercenary (V)), chooses steal;
        // Oh, Switch Off should light up and keep U-3PO as a DS droid.
        var scn = GetScenario();
        var u3po = scn.GetDSCard("u3po");
        var ohSwitchOff = scn.GetDSCard("ohSwitchOff");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        breakU3POCoverWithQAM(scn);

        assertTrue(scn.LSDecisionAvailable("steal"));
        scn.LSChooseYes();
        advanceToOhSwitchOffResponse(scn, ohSwitchOff);

        assertTrue("Oh, Switch Off should be playable to cancel the steal", scn.DSCardPlayAvailable(ohSwitchOff));
        scn.DSPlayCard(ohSwitchOff);
        scn.PassAllResponses();

        assertEquals(scn.DS, u3po.getOwner());
        assertTrue(scn.CardsAtLocation(site, u3po));
    }

    @Test
    public void LightSideMayStealU3POWhenCoverBrokenIfOhSwitchOffNotPlayed() {
        var scn = GetScenario();
        var u3po = scn.GetDSCard("u3po");
        var ohSwitchOff = scn.GetDSCard("ohSwitchOff");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        breakU3POCoverWithQAM(scn);

        assertTrue(scn.LSDecisionAvailable("steal"));
        scn.LSChooseYes();
        advanceToOhSwitchOffResponse(scn, ohSwitchOff);

        assertTrue("Oh, Switch Off should still be available before DS declines", scn.DSCardPlayAvailable(ohSwitchOff));
        scn.PassAllResponses();

        assertEquals(scn.LS, u3po.getOwner());
        assertTrue(scn.CardsAtLocation(site, u3po));
    }
}