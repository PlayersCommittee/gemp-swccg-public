package com.gempukku.swccgo.cards.set222.light;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_222_023_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("irfa", "222_023");
                    put("mara", "217_40");
                    put("saber", "211_33");
                }},
                new HashMap<>() {{
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
    public void ImReadyForAnythingStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("irfa").getBlueprint();

        assertEquals("I'm Ready For Anything", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VIRTUAL_SET_22);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.SET_22, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ImReadyForAnythingLostDeploysMaraWithJediLightsaber() {
        var scn = GetScenario();

        var irfa = scn.GetLSCard("irfa");
        var mara = scn.GetLSCard("mara");
        var saber = scn.GetLSCard("saber");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(irfa, mara, saber);
        scn.LSActivateForceCheat(8);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSPlayLostInterruptAvailable(irfa));
        assertTrue(scn.LSCardActionAvailable(irfa, "a lightsaber"));
        assertFalse(scn.LSCardActionAvailable(irfa, "Anakin"));
        scn.LSPlayLostInterrupt(irfa);
        scn.PassAllResponses();
        chooseLsIfOffered(scn, mara);
        chooseLsIfOffered(scn, saber);
        if (scn.LSDecisionAvailable("Choose where to deploy")) {
            scn.LSChooseCard(site);
        }
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(mara)) {
            scn.LSChooseCard(mara);
        }
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("Choose Deploy action or Pass")) {
            scn.DSPass();
        }

        assertEquals(Zone.AT_LOCATION, mara.getZone());
        assertTrue(scn.IsAttachedTo(mara, saber));
    }

    private void chooseLsIfOffered(VirtualTableScenario scn, PhysicalCardImpl card) {
        if (scn.LSHasCardChoiceAvailable(card)) {
            scn.LSChooseCard(card);
        }
    }
}
