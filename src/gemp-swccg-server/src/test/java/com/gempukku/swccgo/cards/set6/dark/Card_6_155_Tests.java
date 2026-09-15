package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
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

/**
 * Hutt Smooch (#1066 / VHD): capture undercover spy, bounce spy with no presence/icons,
 * same title blocked, other persona blocked by engine-wide persona recording.
 */
public class Card_6_155_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("bothan", "7_5");
                }},
                new HashMap<>() {{
                    put("smooch", "6_155");
                    put("icePlains", "3_148");
                }},
                40,
                40,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private void recoverToLSDeploy(VirtualTableScenario scn) {
        for (int i = 0; i < 20 && !scn.AwaitingLSDeployPhaseActions(); i++) {
            if (scn.GetCurrentDecision() == null) {
                break;
            }
            String text = scn.GetCurrentDecision().getText().toLowerCase();
            if (text.contains("optional")) {
                scn.PassAllResponses();
            } else if (scn.LSAnyActionsAvailable() || scn.LSDecisionAvailable("Pass")) {
                scn.LSPass();
            } else if (scn.DSAnyActionsAvailable() || scn.DSDecisionAvailable("Pass")) {
                scn.DSPass();
            } else {
                break;
            }
        }
        assertTrue("Expected LS deploy phase; decision=" + (scn.GetCurrentDecision() == null ? "null" : scn.GetCurrentDecision().getText()),
                scn.AwaitingLSDeployPhaseActions());
    }

    private void playHuttSmoochBounceAfterSpyDeploy(VirtualTableScenario scn, PhysicalCardImpl spy,
                                                    PhysicalCardImpl site, PhysicalCardImpl smooch) {
        scn.LSDeployCard(spy);
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy")
                || scn.LSDecisionAvailable("Choose location where to deploy"));
        scn.LSChooseCard(site);

        for (int i = 0; i < 12 && !scn.DSCardActionAvailable(smooch) && !scn.DSActionAvailable("Return"); i++) {
            String text = scn.GetCurrentDecision() == null ? "" : scn.GetCurrentDecision().getText().toLowerCase();
            if (!text.contains("optional response")) {
                break;
            }
            if (text.contains("force")) {
                scn.PassForceUseResponses();
            } else if (scn.GetDecidingPlayer().equals(scn.LS)) {
                scn.LSPass();
            } else {
                break;
            }
        }
        assertTrue("Hutt Smooch bounce unavailable; decision=" + (scn.GetCurrentDecision() == null ? "null" : scn.GetCurrentDecision().getText())
                        + " DS actions=" + scn.GetDSAvailableActions(),
                scn.DSCardActionAvailable(smooch) || scn.DSActionAvailable("Return"));
        if (scn.DSCardActionAvailable(smooch)) {
            scn.DSUseCardAction(smooch);
        } else {
            scn.DSChooseAction("Return");
        }
        if (scn.DSHasCardChoiceAvailable(spy)) {
            scn.DSChooseCard(spy);
        }
        scn.PassAllResponses();
        recoverToLSDeploy(scn);
    }

    @Test
    public void HuttSmoochStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("smooch").getBlueprint();

        assertEquals(Title.Hutt_Smooch, card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.JABBAS_PALACE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        assertEquals(ExpansionSet.JABBAS_PALACE, card.getExpansionSet());
        assertEquals(Rarity.U, card.getRarity());
    }

    @Test
    public void HuttSmoochReturnsSpyWithNoPresenceOrIconsAndBlocksSameTitle() {
        var scn = GetScenario();
        var bothan = scn.GetLSCard("bothan");
        var smooch = scn.GetDSCard("smooch");
        var icePlains = scn.GetDSCard("icePlains");

        scn.MoveCardsToLSHand(bothan);
        scn.MoveCardsToDSHand(smooch);
        scn.StartGame();
        scn.MoveLocationToTable(icePlains);
        scn.LSActivateForceCheat(10);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(bothan));
        playHuttSmoochBounceAfterSpyDeploy(scn, bothan, icePlains, smooch);

        assertEquals(Zone.HAND, bothan.getZone());
        assertFalse(scn.LSDeployAvailable(bothan));
    }
}
