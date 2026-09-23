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
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFiredModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Set 22 222_023 I'm Ready For Anything.
 */
public class Card_222_023_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("irfa", "222_023");
                    put("luke", "1_019");
                    put("trooper2", "1_028");
                    put("cantina", "1_128");
                }},
                new HashMap<>() {{
                    put("ig88", "4_101");
                    put("pulse", "225_021");
                    put("stormtrooper", "1_194");
                    put("blaster", "1_317");
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
    public void ImReadyForAnythingAllowsIG88sPulseCannonVToFireRepeatedly() {
        var scn = GetScenario();

        var irfa = scn.GetLSCard("irfa");
        var luke = scn.GetLSCard("luke");
        var trooper2 = scn.GetLSCard("trooper2");

        var ig88 = scn.GetDSCard("ig88");
        var pulse = scn.GetDSCard("pulse");
        var cantina = scn.GetLSCard("cantina");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, luke, trooper2, ig88);
        scn.AttachCardsTo(ig88, pulse);

        scn.DSActivateForceCheat(10);
        scn.PrepareDSDestiny(7);
        scn.PrepareDSDestiny(7);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(cantina);
        scn.PassBattleStartResponses();
        scn.ApplyAdHocModifier(new MayNotBeFiredModifier(irfa,
                Card222_023.additionalWeaponsAfterOneFiredAtSite(scn.game(), pulse.getOwner(), Filters.Cantina)));
        if (scn.AwaitingLSWeaponsSegmentActions()) {
            scn.LSPass();
        }

        assertTrue(scn.DSCardActionAvailable(pulse, "Fire"));
        scn.DSUseCardAction(pulse, "Fire");
        scn.DSChooseCard(luke);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();

        assertTrue("Pulse Cannon (V) is one weapon and may fire repeatedly; decision="
                        + scn.GetCurrentDecision().getText(),
                scn.DSDecisionAvailable("repeatedly fire"));
        scn.DSChooseYes();
        if (scn.DSDecisionAvailable("Choose target")) {
            scn.DSChooseCard(trooper2);
        }
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
    }

    @Test
    public void ImReadyForAnythingBlocksASecondDifferentWeaponAfterOneFires() {
        var scn = GetScenario();

        var irfa = scn.GetLSCard("irfa");
        var luke = scn.GetLSCard("luke");

        var ig88 = scn.GetDSCard("ig88");
        var pulse = scn.GetDSCard("pulse");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var blaster = scn.GetDSCard("blaster");
        var cantina = scn.GetLSCard("cantina");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, luke, ig88, stormtrooper);
        scn.AttachCardsTo(ig88, pulse);
        scn.AttachCardsTo(stormtrooper, blaster);

        scn.DSActivateForceCheat(10);
        scn.PrepareDSDestiny(7);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(cantina);
        scn.PassBattleStartResponses();
        scn.ApplyAdHocModifier(new MayNotBeFiredModifier(irfa,
                Card222_023.additionalWeaponsAfterOneFiredAtSite(scn.game(), pulse.getOwner(), Filters.Cantina)));
        if (scn.AwaitingLSWeaponsSegmentActions()) {
            scn.LSPass();
        }

        assertTrue(scn.DSCardActionAvailable(pulse, "Fire"));
        assertTrue(scn.DSCardActionAvailable(blaster, "Fire"));
        scn.DSUseCardAction(pulse, "Fire");
        scn.DSChooseCard(luke);
        scn.PassWeaponFireWithDestinyDraw();
        if (scn.DSDecisionAvailable("repeatedly fire")) {
            scn.DSChooseNo();
        }
        boolean blasterFireOffered = scn.DSGetDecision() != null
                && scn.DSGetDecision().getDecisionParameters().get("cardId") != null
                && scn.DSCardActionAvailable(blaster, "Fire");
        assertFalse("Second weapon must not fire after Pulse Cannon; actions=" + scn.GetDSAvailableActions(),
                blasterFireOffered);
    }
}
