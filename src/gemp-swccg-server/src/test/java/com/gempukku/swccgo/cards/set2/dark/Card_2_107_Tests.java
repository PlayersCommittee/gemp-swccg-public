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
                    put("luke", "1_019");
                    put("caller", "1_034");
                    put("bolt", "1_038");
                }},
                new HashMap<>()
                {{
                    put("u3po", "2_107"); // U-3PO (Yoo-Threepio)
                    put("ohSwitchOff", "3_130"); // Oh, Switch Off
                    put("eg6", "1_175"); // EG-6 (Eegee-Six)
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
        // Repro (#50): LS breaks U-3PO cover (Quite A Mercenary (V)), chooses to steal;
        // U-3PO is targeted to be stolen, so Oh, Switch Off can cancel and keep U-3PO as a DS droid.
        var scn = GetScenario();
        var u3po = scn.GetDSCard("u3po");
        var ohSwitchOff = scn.GetDSCard("ohSwitchOff");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        breakU3POCoverWithQAM(scn);

        assertTrue(scn.LSDecisionAvailable("steal"));
        scn.LSChooseYes();

        assertTrue(scn.DSDecisionAvailable("Optional responses"));
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

        assertTrue("Oh, Switch Off should be available before DS declines", scn.DSCardPlayAvailable(ohSwitchOff));
        scn.PassAllResponses();

        assertEquals(scn.LS, u3po.getOwner());
        assertTrue(scn.CardsAtLocation(site, u3po));
    }

    @Test
    public void OhSwitchOffCancelsCallerStealOfDsDroidWithRestrainingBolt() {
        // VHD: LS Caller + Restraining Bolt steal of a DS droid should still be cancelable.
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var caller = scn.GetLSCard("caller");
        var bolt = scn.GetLSCard("bolt");
        var eg6 = scn.GetDSCard("eg6");
        var ohSwitchOff = scn.GetDSCard("ohSwitchOff");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, eg6);
        scn.AttachCardsTo(luke, caller);
        scn.AttachCardsTo(eg6, bolt);
        scn.MoveCardsToDSHand(ohSwitchOff);

        scn.SkipToLSTurn(Phase.CONTROL);
        for (int i = 0; i < 12 && !scn.AwaitingLSControlPhaseActions(); i++) {
            if (scn.DSAnyDecisionsAvailable() && !scn.LSAnyDecisionsAvailable()) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue(scn.LSCardActionAvailable(caller, "Steal"));
        scn.LSUseCardAction(caller, "Steal");
        if (scn.LSHasCardChoiceAvailable(eg6)) {
            scn.LSChooseCard(eg6);
        }
        // Caller steal is a targeting Effect (before-action). Do not PassAllResponses —
        // that skips the Oh, Switch Off window.
        for (int i = 0; i < 12 && !scn.DSCardPlayAvailable(ohSwitchOff); i++) {
            if (scn.LSAnyDecisionsAvailable() && !scn.DSAnyDecisionsAvailable()) {
                scn.LSPass();
                continue;
            }
            break;
        }
        assertTrue("Oh, Switch Off should cancel Caller steal; ds=" + scn.GetDSAvailableActions()
                + " ls=" + scn.GetLSAvailableActions(), scn.DSCardPlayAvailable(ohSwitchOff));
        scn.DSPlayCard(ohSwitchOff);
        scn.PassAllResponses();

        assertEquals(scn.DS, eg6.getOwner());
        assertTrue(scn.CardsAtLocation(site, eg6));
    }

}
