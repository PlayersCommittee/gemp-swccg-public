package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Kal'Falnl C'ndros — may not deploy to / board starfighters or enclosed vehicles.
 * Issue #221: was allowed aboard Millennium Falcon as passenger via ship-dock transfer.
 */
public class Card_1_015_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("kalFalnlCndros", "1_015");
                    put("millenniumFalcon", "1_143");
                    put("nebulonBFrigate", "9_080");
                    put("majorHaashn", "9_025");
                }},
                new HashMap<>() {{
                }},
                20,
                20,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void KalFalnlCndrosStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("kalFalnlCndros").getBlueprint();

        assertEquals("Kal'Falnl C'ndros", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(0, card.getDeployCost(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.PILOT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.FEMALE);
        }});
        assertEquals(Species.QUORSAV, card.getSpecies());
        assertEquals(ExpansionSet.PREMIERE, card.getExpansionSet());
        assertEquals(Rarity.R1, card.getRarity());
    }

    @Test
    public void KalFalnlCndrosCanDeployAboardCapitalShip() {
        var scn = GetScenario();

        var kalFalnlCndros = scn.GetLSCard("kalFalnlCndros");
        var nebulonBFrigate = scn.GetLSCard("nebulonBFrigate");

        var system = scn.GetLSStartingLocation();
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(kalFalnlCndros);
        scn.MoveCardsToLocation(system, nebulonBFrigate);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(kalFalnlCndros));
        scn.LSDeployCard(kalFalnlCndros);
        assertTrue(scn.LSHasCardChoiceAvailable(site));
        assertTrue(scn.LSHasCardChoiceAvailable(nebulonBFrigate));
        scn.LSChooseCard(nebulonBFrigate);
        scn.LSChoose("Pilot");
        scn.PassAllResponses();

        assertTrue(scn.IsAboardAsPilot(nebulonBFrigate, kalFalnlCndros));
    }

    @Test
    public void KalFalnlCndrosMayNotDeployAboardMillenniumFalcon() {
        // Real-path deploy: may not board/pilot Falcon (#221)
        var scn = GetScenario();

        var kalFalnlCndros = scn.GetLSCard("kalFalnlCndros");
        var millenniumFalcon = scn.GetLSCard("millenniumFalcon");

        var system = scn.GetLSStartingLocation();
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(kalFalnlCndros);
        scn.MoveCardsToLocation(system, millenniumFalcon);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(kalFalnlCndros));
        scn.LSDeployCard(kalFalnlCndros);
        assertTrue(scn.LSHasCardChoiceAvailable(site));
        assertFalse(scn.LSHasCardChoiceAvailable(millenniumFalcon));
    }

    @Test
    public void KalFalnlCndrosMayNotEmbarkOnMillenniumFalcon() {
        // Real-path embark: may not board Falcon as pilot or passenger (#221)
        var scn = GetScenario();

        var kalFalnlCndros = scn.GetLSCard("kalFalnlCndros");
        var trooper = scn.GetLSFiller(1);
        var millenniumFalcon = scn.GetLSCard("millenniumFalcon");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, millenniumFalcon, kalFalnlCndros, trooper);

        scn.SkipToLSTurn(Phase.MOVE);
        assertTrue(scn.LSCardActionAvailable(trooper, "Embark"));
        assertFalse(scn.LSCardActionAvailable(kalFalnlCndros, "Embark"));
    }

    @Test
    public void KalFalnlCndrosMayNotTransferToMillenniumFalconViaShipDock() {
        // Real-path ship-dock transfer: may not board/pilot Falcon as passenger or pilot (#221)
        var scn = GetScenario();

        var kalFalnlCndros = scn.GetLSCard("kalFalnlCndros");
        var majorHaashn = scn.GetLSCard("majorHaashn");
        var millenniumFalcon = scn.GetLSCard("millenniumFalcon");
        var nebulonBFrigate = scn.GetLSCard("nebulonBFrigate");

        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, millenniumFalcon, nebulonBFrigate);
        scn.MoveCardsToLSHand(kalFalnlCndros, majorHaashn);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(majorHaashn);
        scn.LSChooseCard(nebulonBFrigate);
        scn.LSChoose("Pilot");
        scn.PassAllResponses();
        assertTrue(scn.IsAboardAsPilot(majorHaashn));

        scn.DSPass();

        scn.LSDeployCard(kalFalnlCndros);
        scn.LSChooseCard(nebulonBFrigate);
        scn.LSChoose("Pilot");
        scn.PassAllResponses();
        assertTrue(scn.IsAboardAsPilot(kalFalnlCndros));

        scn.SkipToPhase(Phase.MOVE);
        scn.LSUseCardAction(nebulonBFrigate, "dock");
        scn.LSChooseCard(millenniumFalcon);
        scn.PassAllResponses();

        assertTrue(scn.LSCardActionAvailable(majorHaashn, "Transfer"));
        assertFalse(scn.LSCardActionAvailable(kalFalnlCndros, "Transfer"));
        assertFalse(scn.IsAboardAsPassenger(millenniumFalcon, kalFalnlCndros));
        assertFalse(scn.IsAboardAsPilot(millenniumFalcon, kalFalnlCndros));
    }
}
