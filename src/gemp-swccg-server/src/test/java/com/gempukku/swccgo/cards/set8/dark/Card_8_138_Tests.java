package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateBattleAtLocationModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Card_8_138_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("hwga", "8_54");
                    put("trap", "5_55");
                }},
                new HashMap<>() {{
                    put("counterattack", "8_138");
                    put("vader", "1_168");
                    put("st", "1_194");
                    put("retwin", "5_93");
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

    private void SetupSiteBattle(VirtualTableScenario scn, PhysicalCardImpl... extra) {
        var site = scn.GetLSStartingLocation();
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        scn.MoveCardsToDSHand(scn.GetDSCard("counterattack"));
        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, vader);
        if (extra != null) {
            for (PhysicalCardImpl card : extra) {
                if (card != null) {
                    scn.MoveCardsToLocation(site, card);
                }
            }
        }
    }

    private void FinishBattlePayingDamage(VirtualTableScenario scn) {
        scn.SkipToDamageSegment(false);
        if (scn.AwaitingLSBattleDamagePayment()) {
            scn.LSPayRemainingBattleDamageFromReserveDeck();
        }
        if (scn.AwaitingDSBattleDamagePayment()) {
            scn.DSPayRemainingBattleDamageFromReserveDeck();
        }
        scn.PassDamageSegmentActions();
    }

    private void PlayCounterattackAndStartRebattle(VirtualTableScenario scn) {
        var counterattack = scn.GetDSCard("counterattack");
        assertTrue(scn.DSCardPlayAvailable(counterattack));
        scn.DSPlayCard(counterattack);
        scn.PassAllResponses();
        scn.PassForceUseResponses();
        scn.PassBattleStartResponses();
    }

    @Test
    public void CounterattackStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("counterattack").getBlueprint();

        assertEquals("Counterattack", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ENDOR);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.ENDOR, card.getExpansionSet());
        assertEquals(Rarity.R, card.getRarity());
    }

    @Test
    public void CounterattackPlayableWhenOpponentInitiatedBattleJustEnded() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        SetupSiteBattle(scn);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        FinishBattlePayingDamage(scn);

        assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("counterattack")));
        PlayCounterattackAndStartRebattle(scn);

        assertNotNull(scn.gameState().getBattleState());
        assertEquals(scn.DS, scn.gameState().getBattleState().getPlayerInitiatedBattle());
        assertTrue(scn.IsParticipatingInBattle(luke, vader));
        assertTrue(scn.GetDSCard("counterattack").getZone() == Zone.LOST_PILE || scn.GetDSCard("counterattack").getZone() == Zone.VOID);
    }

    @Test
    public void CounterattackNotPlayableIfYouInitiatedTheBattleThatJustEnded() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        SetupSiteBattle(scn);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        FinishBattlePayingDamage(scn);

        assertFalse(scn.DSAnyDecisionsAvailable() && scn.DSCardPlayAvailable(scn.GetDSCard("counterattack")));
    }

    @Test
    public void CounterattackNotPlayableIfOriginalBattleWasCanceled() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var trap = scn.GetLSCard("trap");
        SetupSiteBattle(scn);
        scn.MoveCardsToLSHand(trap);

        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.LSCanInitiateBattle(site));
        scn.LSUseCardAction(site, "Initiate battle");
        scn.PassForceUseResponses();
        if (scn.DSAnyDecisionsAvailable() && !scn.LSAnyDecisionsAvailable()) {
            scn.DSPass();
        }
        assertTrue(scn.LSCardPlayAvailable(trap, "Cancel battle"));
        scn.LSPlayCard(trap, "Cancel battle");
        scn.PassAllResponses();
        scn.PassForceUseResponses();

        assertFalse(scn.DSAnyDecisionsAvailable() && scn.DSCardPlayAvailable(scn.GetDSCard("counterattack")));
    }

    @Test
    public void CounterattackInitiateBattleCostIsPaid() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        SetupSiteBattle(scn);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        FinishBattlePayingDamage(scn);

        int before = scn.GetDSForcePileCount();
        assertTrue(before >= 1);
        PlayCounterattackAndStartRebattle(scn);
        assertEquals(before - 1, scn.GetDSForcePileCount());
    }

    @Test
    public void CounterattackTrooperPresentAddsOneBattleDestinyAutomatically() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var st = scn.GetDSCard("st");
        SetupSiteBattle(scn, st);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        FinishBattlePayingDamage(scn);
        PlayCounterattackAndStartRebattle(scn);

        scn.SkipToPowerSegment();
        // Vader ability 6 = 1 destiny, Counterattack trooper = +1 automatic
        assertEquals(2, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void CounterattackNoTrooperAddsNoExtraBattleDestiny() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        SetupSiteBattle(scn);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        FinishBattlePayingDamage(scn);
        PlayCounterattackAndStartRebattle(scn);

        scn.SkipToPowerSegment();
        // Vader ability 6 = 1 destiny only
        assertEquals(1, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void CounterattackLoserIgnoresBattleDamageThisBattle() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        SetupSiteBattle(scn);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        FinishBattlePayingDamage(scn);
        PlayCounterattackAndStartRebattle(scn);

        scn.SkipToDamageSegment(false);
        assertEquals(0, scn.GetUnpaidDSBattleDamage());
        assertEquals(0, scn.GetUnpaidLSBattleDamage());
    }

    @Test
    public void CounterattackMayNotHideFromThisBattle() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var retwin = scn.GetDSCard("retwin");
        SetupSiteBattle(scn, retwin);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        FinishBattlePayingDamage(scn);
        PlayCounterattackAndStartRebattle(scn);

        assertFalse(scn.DSCardActionAvailable(retwin, "Hide"));
    }

    @Test
    public void CounterattackMayNotInitiateStillBlocks() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        SetupSiteBattle(scn);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        FinishBattlePayingDamage(scn);

        scn.game().getModifiersEnvironment().addUntilEndOfTurnModifier(
                new MayNotInitiateBattleAtLocationModifier(site, site));

        assertFalse(com.gempukku.swccgo.cards.GameConditions.canInitiateBattleAtLocation(
                scn.DS, scn.game(), site, false, true, false, true));
    }
}
