package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_200_041_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("imbats", "200_041");
                    put("boushh", "110_001");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
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
    public void IMustBeAllowedToSpeakVStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("imbats").getBlueprint();
        assertEquals(Title.I_Must_Be_Allowed_To_Speak, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.JABBAS_PALACE);
            add(Icon.VIRTUAL_SET_0);
            add(Icon.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        assertEquals(ExpansionSet.SET_0, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void IMustBeAllowedToSpeakVOffersUsedPileSearchWhenBoushhDeploysUndercoverToTatooine() {
        var scn = GetScenario();

        var imbats = scn.GetLSCard("imbats");
        var boushh = scn.GetLSCard("boushh");
        var used = scn.GetLSFiller(1);

        prepareImbatsAndHand(scn, imbats, boushh, used);
        var tatooine = scn.GetDSStartingLocation();

        scn.LSDeployCard(boushh);
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        scn.LSChooseCard(tatooine);
        advanceToSearchWindow(scn);

        assertTrue(boushh.isUndercover());
        assertTrue("I Must Be Allowed To Speak must offer Used Pile search after deploying Boushh (Leia) undercover",
                searchAvailable(scn));
    }

    @Test
    public void IMustBeAllowedToSpeakVOffersUsedPileSearchWhenLukeDeploysToTatooine() {
        var scn = GetScenario();

        var imbats = scn.GetLSCard("imbats");
        var luke = scn.GetLSCard("luke");
        var used = scn.GetLSFiller(1);

        prepareImbatsAndHand(scn, imbats, luke, used);
        var tatooine = scn.GetDSStartingLocation();

        scn.LSDeployCard(luke);
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        scn.LSChooseCard(tatooine);
        advanceToSearchWindow(scn);

        assertTrue(searchAvailable(scn));
    }

    @Test
    public void IMustBeAllowedToSpeakVDoesNotOfferUsedPileSearchWhenBoushhDeploysOffTatooine() {
        var scn = GetScenario();

        var imbats = scn.GetLSCard("imbats");
        var boushh = scn.GetLSCard("boushh");
        var used = scn.GetLSFiller(1);
        var chasm = scn.GetLSStartingLocation();

        prepareImbatsAndHand(scn, imbats, boushh, used);

        scn.LSDeployCard(boushh);
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        scn.LSChooseCard(chasm);
        advanceToSearchWindow(scn);

        assertTrue(boushh.isUndercover());
        assertFalse(searchAvailable(scn));
    }

    private void prepareImbatsAndHand(VirtualTableScenario scn, PhysicalCardImpl imbats, PhysicalCardImpl character, PhysicalCardImpl used) {
        scn.StartGame();
        scn.MoveCardsToLSHand(character);
        scn.MoveCardsToLSSideOfTable(imbats);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSActivateForceCheat(10);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSUsedPile(used);
    }

    private boolean searchAvailable(VirtualTableScenario scn) {
        return scn.GetLSAvailableActions().stream()
                .anyMatch(action -> action.toLowerCase().contains("take card into hand from used pile"));
    }

    private void advanceToSearchWindow(VirtualTableScenario scn) {
        for (int i = 0; i < 12; i++) {
            if (searchAvailable(scn)) {
                return;
            }
            var decision = scn.GetCurrentDecision();
            if (decision == null || !decision.getText().toLowerCase().contains("optional")) {
                return;
            }
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
    }
}
