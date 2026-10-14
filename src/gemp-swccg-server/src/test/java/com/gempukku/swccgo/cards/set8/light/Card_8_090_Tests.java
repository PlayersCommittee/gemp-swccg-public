package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

/**
 * Tests for Endor 8_090 Explosive Charge.
 */
public class Card_8_090_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("charge", "8_90");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("trooper", "1_194");
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
    public void ExplosiveChargeLostOrderChosenByPlayerWhoseTurnItIs() {
        var scn = GetScenario();

        var charge = scn.GetLSCard("charge");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();
        var vader = scn.GetDSCard("vader");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, vader, trooper);
        scn.AttachCardsTo(site, charge);

        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle(site));
        scn.DSInitiateBattle(site);
        scn.SkipToEndOfPowerSegment(false);
        // DS turn, LS-owned Charge is the action source (same as the printed explode).
        var action = new TopLevelGameTextAction(charge, VirtualTableScenario.LS, charge.getCardId());
        action.appendEffect(new LoseCardsFromTableEffect(action, Arrays.asList(luke, vader, trooper)));
        scn.carryOutEffectInPhaseActionByPlayer(VirtualTableScenario.LS, action);

        assertTrue("AR p.11: current player (DS) chooses Lost Pile order. Decision="
                        + (scn.GetCurrentDecision() != null ? scn.GetCurrentDecision().getText() : "none"),
                scn.DSDecisionAvailable("Choose card to be lost"));
    }
}
