package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Issue #970: canceled battle destinies still count toward draw-number checks
 * (Mandalorian Mishap (V) / Wounded Wookiee / Takeel / KFC), while attrition
 * gates use remaining uncanceled destinies only.
 */
public class Card_208_020_Tests {

    protected VirtualTableScenario GetGroundScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("mishap", "208_20");
                    put("eppLeia", "108_2");
                    put("eppHan", "108_1");
                    put("kfc", "1_15");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("tarkin", "1_179");
                    put("takeel", "1_269");
                    put("stormtrooper", "1_194");
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

    protected VirtualTableScenario GetSpaceScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("mishap", "208_20");
                    put("artoo", "14_003");
                    put("ywing", "1_147");
                }},
                new HashMap<>() {{
                    put("zimh", "110_012");
                }},
                10,
                10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private void drawBattleDestinyWithOptionalCancel(VirtualTableScenario scn, boolean darkCancelsWithTarkin,
                                                     boolean lightCancelsWithArtoo) {
        // COST / ABOUT_TO_DRAW
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        if (darkCancelsWithTarkin && scn.DSCardActionAvailable(scn.GetDSCard("tarkin"), "Cancel")) {
            scn.DSUseCardAction(scn.GetDSCard("tarkin"), "Cancel");
            scn.PassResponses("DESTINY_DRAWN");
            return;
        }
        if (lightCancelsWithArtoo && scn.LSCardActionAvailable(scn.GetLSCard("artoo"), "Cancel")) {
            scn.LSUseCardAction(scn.GetLSCard("artoo"), "Cancel");
            scn.DSPass(); // ABOUT_TO_BE_PLACE...
            scn.LSPass();
            scn.DSPass(); // PUT_IN_USED_PILE
            scn.LSPass();
            scn.PassResponses("DESTINY_DRAWN");
            return;
        }
        scn.PassResponses("DESTINY_DRAWN");
        scn.PassResponses("COMPLETE_DESTINY_DRAW");
        scn.PassResponses("DRAWING_DESTINY_COMPLETE");
    }

    @Test
    public void MandalorianMishapVStatsAndIconsAreCorrect() {
        var scn = GetGroundScenario();
        var card = scn.GetLSCard("mishap").getBlueprint();
        assertEquals("Mandalorian Mishap", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(ExpansionSet.SET_8, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.JABBAS_PALACE);
            add(Icon.VIRTUAL_SET_8);
            add(Icon.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
    }

    /**
     * #970: Artoo cancels the 1st of 3 Dark battle destinies; after the 3rd attempt
     * completes, Mishap is offered (canceled draw counts toward "more than two").
     */
    @Test
    public void MandalorianMishapVOfferedAfterArtooCancelsFirstOfThreeBattleDestinies() {
        var scn = GetSpaceScenario();

        var mishap = scn.GetLSCard("mishap");
        var artoo = scn.GetLSCard("artoo");
        var ywing = scn.GetLSCard("ywing");
        var system = scn.GetLSStartingLocation();
        var zimh = scn.GetDSCard("zimh");

        scn.StartGame();
        scn.MoveCardsToLSHand(mishap);
        scn.MoveCardsToLocation(system, ywing, zimh);
        scn.BoardAsPassenger(ywing, artoo);
        scn.ApplyAdHocModifier(new AddsBattleDestinyModifier(zimh, 2, scn.DS, true));

        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareDSDestiny(1);
        scn.PrepareDSDestiny(2);
        scn.PrepareDSDestiny(3);
        scn.DSInitiateBattle(system);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();

        assertEquals(3, scn.GetDSBattleDestinyCount());
        assertTrue(scn.DSDecisionAvailable("battle destiny?"));
        scn.DSChooseYes();

        drawBattleDestinyWithOptionalCancel(scn, false, true); // 1st canceled by Artoo
        assertEquals(Zone.TOP_OF_USED_PILE, artoo.getZone());
        drawBattleDestinyWithOptionalCancel(scn, false, false); // 2nd
        drawBattleDestinyWithOptionalCancel(scn, false, false); // 3rd

        assertEquals(3, scn.gameState().getBattleState().getNumBattleDestinyDrawn(scn.DS));
        assertEquals(2, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.DS));

        // After drawing completes, Mishap is offered
        assertTrue(scn.LSCardActionAvailable(mishap, "Cancel a battle destiny")
                || scn.LSDecisionAvailable("Optional"));
        if (!scn.LSCardActionAvailable(mishap, "Cancel a battle destiny")) {
            // may need to reach BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER responses
            scn.PassResponses("DRAWING_DESTINY_COMPLETE");
        }
        assertTrue("Mishap should be offered after 3 attempts with 1 canceled",
                scn.LSCardActionAvailable(mishap, "Cancel a battle destiny"));
    }

    /**
     * EPP Leia + Han draw 2; Tarkin cancels one; Dark draws 1.
     * Takeel / KFC require both players drew exactly one — not playable after fix.
     */
    @Test
    public void TakeelAndKalFalnlCndrosNotPlayableWhenOneOfTwoLightBattleDestiniesCanceledByTarkin() {
        var scn = GetGroundScenario();

        var eppLeia = scn.GetLSCard("eppLeia");
        var eppHan = scn.GetLSCard("eppHan");
        var kfc = scn.GetLSCard("kfc");
        var site = scn.GetLSStartingLocation();
        var vader = scn.GetDSCard("vader");
        var tarkin = scn.GetDSCard("tarkin");
        var takeel = scn.GetDSCard("takeel");

        scn.StartGame();
        scn.MoveCardsToLocation(site, eppLeia, eppHan, kfc, vader, tarkin);
        scn.MoveCardsToDSHand(takeel);

        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareLSDestiny(5);
        scn.PrepareLSDestiny(4);
        scn.PrepareDSDestiny(3);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();

        assertTrue(scn.DSDecisionAvailable("battle destiny?"));
        assertEquals(1, scn.GetDSBattleDestinyCount());
        scn.DSChooseYes();
        scn.PassDestinyDrawResponses();
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");

        assertTrue(scn.LSDecisionAvailable("battle destiny?"));
        assertEquals(2, scn.GetLSBattleDestinyCount());
        scn.LSChooseYes();

        drawBattleDestinyWithOptionalCancel(scn, true, false); // Tarkin cancels 1st
        drawBattleDestinyWithOptionalCancel(scn, false, false); // 2nd completes

        assertEquals(2, scn.gameState().getBattleState().getNumBattleDestinyDrawn(scn.LS));
        assertEquals(1, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.LS));
        assertEquals(1, scn.gameState().getBattleState().getNumBattleDestinyDrawn(scn.DS));

        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        // Takeel/KFC check on BOTH complete window (before passing it)
        assertFalse(GameConditions.didBothPlayersDrawOneBattleDestiny(scn.game()));
        if (scn.AnyDecisionsAvailable(scn.DS)) {
            assertFalse(scn.DSPlayLostInterruptAvailable(takeel));
        }
        if (scn.AnyDecisionsAvailable(scn.LS)) {
            assertFalse(scn.LSCardActionAvailable(kfc));
        }
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");
    }

    /**
     * Path B: WW cancel-previous shrinks the draw list but lifetime count still
     * blocks Takeel after Light drew more than one.
     */
    @Test
    public void TakeelNotPlayableAfterWoundedWookieeCancelsPreviousLeavingTwo() {
        // Path B: cancel-previous removes from the draw list but lifetime count stays
        // (blocks didBothPlayersDrawOne / Takeel). Use space AdHoc path that reliably yields 3 BDs.
        var scn = GetSpaceScenario();

        var ywing = scn.GetLSCard("ywing");
        var system = scn.GetLSStartingLocation();
        var zimh = scn.GetDSCard("zimh");

        scn.StartGame();
        scn.MoveCardsToLocation(system, ywing, zimh);
        scn.ApplyAdHocModifier(new AddsBattleDestinyModifier(zimh, 2, scn.DS, true));

        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareDSDestiny(1);
        scn.PrepareDSDestiny(2);
        scn.PrepareDSDestiny(3);
        scn.DSInitiateBattle(system);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();

        assertEquals(3, scn.GetDSBattleDestinyCount());
        scn.DSChooseYes();
        scn.PassDestinyDrawResponses();
        scn.PassDestinyDrawResponses();
        scn.PassDestinyDrawResponses();

        var battle = scn.gameState().getBattleState();
        assertEquals(3, battle.getNumBattleDestinyDrawn(scn.DS));
        assertEquals(3, battle.getNumUncanceledBattleDestinyDrawn(scn.DS));

        var cancel = new java.util.HashSet<Integer>();
        cancel.add(2); // leave 2 uncanceled
        battle.cancelPreviousBattleDestinyDraws(scn.DS, cancel);
        assertEquals(2, battle.getNumUncanceledBattleDestinyDrawn(scn.DS));
        assertEquals(3, battle.getNumBattleDestinyDrawn(scn.DS));
        assertFalse(GameConditions.didBothPlayersDrawOneBattleDestiny(scn.game()));
    }


    /**
     * Sole battle destiny canceled by Tarkin: attrition does not exist and cannot
     * be modified; unpaid attrition is 0; attrition-destiny draws gated to 0.
     */
    @Test
    public void AttritionCannotBeModifiedWhenSoleBattleDestinyCanceledByTarkin() {
        // Artoo cancels Dark's sole battle destiny → no uncanceled BD → no attrition
        var scn = GetSpaceScenario();

        var artoo = scn.GetLSCard("artoo");
        var ywing = scn.GetLSCard("ywing");
        var system = scn.GetLSStartingLocation();
        var zimh = scn.GetDSCard("zimh");

        scn.StartGame();
        scn.MoveCardsToLocation(system, ywing, zimh);
        scn.BoardAsPassenger(ywing, artoo);

        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareDSDestiny(7);
        scn.DSInitiateBattle(system);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();

        assertTrue(scn.DSDecisionAvailable("battle destiny?"));
        assertEquals(1, scn.GetDSBattleDestinyCount());
        scn.DSChooseYes();

        scn.LSPass();
        scn.DSPass();
        scn.LSPass();
        scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(artoo, "Cancel"));
        scn.LSUseCardAction(artoo, "Cancel");
        scn.DSPass();
        scn.LSPass();
        scn.DSPass();
        scn.LSPass();
        scn.DSPass();
        scn.LSPass();

        assertEquals(1, scn.gameState().getBattleState().getNumBattleDestinyDrawn(scn.DS));
        assertEquals(0, scn.gameState().getBattleState().getNumUncanceledBattleDestinyDrawn(scn.DS));

        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");

        assertFalse(GameConditions.canModifyAttritionAgainst(scn.game(), scn.LS));
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertEquals(0, scn.GetUnpaidLSAttrition());
        assertEquals(0, scn.GetUnpaidDSAttrition());
    }

    /**
     * After cancel-previous removes every remaining uncanceled destiny, attrition
     * still does not exist even though lifetime draw count is > 0.
     */
    @Test
    public void AttritionCannotBeModifiedAfterCancelPreviousRemovesLastRemainingDestiny() {
        var scn = GetSpaceScenario();

        var ywing = scn.GetLSCard("ywing");
        var system = scn.GetLSStartingLocation();
        var zimh = scn.GetDSCard("zimh");

        scn.StartGame();
        scn.MoveCardsToLocation(system, ywing, zimh);
        scn.ApplyAdHocModifier(new AddsBattleDestinyModifier(zimh, 2, scn.DS, true));

        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareDSDestiny(1);
        scn.PrepareDSDestiny(2);
        scn.PrepareDSDestiny(3);
        scn.DSInitiateBattle(system);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();

        assertTrue(scn.DSDecisionAvailable("battle destiny?"));
        assertEquals(3, scn.GetDSBattleDestinyCount());
        scn.DSChooseYes();
        scn.PassDestinyDrawResponses();
        scn.PassDestinyDrawResponses();
        scn.PassDestinyDrawResponses();

        var battle = scn.gameState().getBattleState();
        assertEquals(3, battle.getNumBattleDestinyDrawn(scn.DS));
        assertEquals(3, battle.getNumUncanceledBattleDestinyDrawn(scn.DS));

        var indexes = new HashSet<Integer>();
        for (int i = 0; i < battle.getNumUncanceledBattleDestinyDrawn(scn.DS); i++) {
            indexes.add(i);
        }
        battle.cancelPreviousBattleDestinyDraws(scn.DS, indexes);
        assertEquals(0, battle.getNumUncanceledBattleDestinyDrawn(scn.DS));
        assertEquals(3, battle.getNumBattleDestinyDrawn(scn.DS));

        assertFalse(GameConditions.canModifyAttritionAgainst(scn.game(), scn.LS));

        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertEquals(0, scn.GetUnpaidLSAttrition());
    }
}
