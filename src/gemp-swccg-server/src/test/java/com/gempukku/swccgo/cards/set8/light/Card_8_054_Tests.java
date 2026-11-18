package com.gempukku.swccgo.cards.set8.light;

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

public class Card_8_054_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("hwga", "8_54");
                    put("luke", "1_19");
                    put("c3po", "1_5");
                    put("r2", "2_14");
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
        var st = scn.GetDSCard("st");
        scn.MoveCardsToLSHand(scn.GetLSCard("hwga"));
        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, st);
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

    private void PlayHwgaAndStartRebattle(VirtualTableScenario scn) {
        var hwga = scn.GetLSCard("hwga");
        assertTrue(scn.LSCardPlayAvailable(hwga));
        scn.LSPlayCard(hwga);
        scn.PassAllResponses();
        scn.PassForceUseResponses();
        scn.PassBattleStartResponses();
    }

    @Test
    public void HereWeGoAgainStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("hwga").getBlueprint();

        assertEquals("Here We Go Again", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
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
    public void HereWeGoAgainPlayableWhenOpponentInitiatedBattleJustEnded() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var luke = scn.GetLSCard("luke");
        var st = scn.GetDSCard("st");
        SetupSiteBattle(scn);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        FinishBattlePayingDamage(scn);

        assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("hwga")));
        PlayHwgaAndStartRebattle(scn);

        assertNotNull(scn.gameState().getBattleState());
        assertEquals(scn.LS, scn.gameState().getBattleState().getPlayerInitiatedBattle());
        assertTrue(scn.IsParticipatingInBattle(luke, st));
        assertTrue(scn.GetLSCard("hwga").getZone() == Zone.LOST_PILE || scn.GetLSCard("hwga").getZone() == Zone.VOID);
    }

    @Test
    public void HereWeGoAgainNotPlayableIfYouInitiatedTheBattleThatJustEnded() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        SetupSiteBattle(scn);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        FinishBattlePayingDamage(scn);

        assertFalse(scn.LSAnyDecisionsAvailable() && scn.LSCardPlayAvailable(scn.GetLSCard("hwga")));
    }

    @Test
    public void HereWeGoAgainNotPlayableIfOriginalBattleWasCanceled() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var trap = scn.GetLSCard("trap");
        SetupSiteBattle(scn);
        scn.MoveCardsToLSHand(trap);

        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.DSCanInitiateBattle(site));
        scn.DSUseCardAction(site, "Initiate battle");
        scn.PassForceUseResponses();
        assertTrue(scn.LSCardPlayAvailable(trap, "Cancel battle"));
        scn.LSPlayCard(trap, "Cancel battle");
        scn.PassAllResponses();
        scn.PassForceUseResponses();

        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("hwga")));
    }

    @Test
    public void HereWeGoAgainInitiateBattleCostIsPaid() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        SetupSiteBattle(scn);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        FinishBattlePayingDamage(scn);

        int before = scn.GetLSForcePileCount();
        assertTrue(before >= 1);
        PlayHwgaAndStartRebattle(scn);
        assertEquals(before - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void HereWeGoAgainDroidPresentAddsOneBattleDestinyAutomatically() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var r2 = scn.GetLSCard("r2");
        SetupSiteBattle(scn, r2);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        FinishBattlePayingDamage(scn);
        PlayHwgaAndStartRebattle(scn);

        scn.SkipToPowerSegment();
        // Luke ability 4 = 1 destiny, HWGA droid = +1 automatic
        assertEquals(2, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void HereWeGoAgainC3POPresentAddsTwoBattleDestinyTotalNotThree() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var c3po = scn.GetLSCard("c3po");
        var r2 = scn.GetLSCard("r2");
        SetupSiteBattle(scn, c3po, r2);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        FinishBattlePayingDamage(scn);
        PlayHwgaAndStartRebattle(scn);

        scn.SkipToPowerSegment();
        // Luke ability 4 = 1, HWGA C-3PO = 2 total (not 1+2)
        assertEquals(3, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void HereWeGoAgainLoserIgnoresBattleDamageThisBattle() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        SetupSiteBattle(scn);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        FinishBattlePayingDamage(scn);
        PlayHwgaAndStartRebattle(scn);

        scn.SkipToDamageSegment(false);
        // Luke (3) vs Stormtrooper (1) => DS would take BD if not ignored
        assertEquals(0, scn.GetUnpaidDSBattleDamage());
        assertEquals(0, scn.GetUnpaidLSBattleDamage());
    }

    @Test
    public void HereWeGoAgainMayNotHideFromThisBattle() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var retwin = scn.GetDSCard("retwin");
        SetupSiteBattle(scn, retwin);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        FinishBattlePayingDamage(scn);
        PlayHwgaAndStartRebattle(scn);

        // LS initiated the re-battle, so LS weapons segment is first; pass it so DS Hide can be checked.
        if (scn.AwaitingLSWeaponsSegmentActions()) {
            scn.LSPass();
        }
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertFalse(scn.DSCardActionAvailable(retwin, "Hide"));
    }

    @Test
    public void HereWeGoAgainMayNotInitiateStillBlocks() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        SetupSiteBattle(scn);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        FinishBattlePayingDamage(scn);

        scn.game().getModifiersEnvironment().addUntilEndOfTurnModifier(
                new MayNotInitiateBattleAtLocationModifier(site, site));

        assertFalse(com.gempukku.swccgo.cards.GameConditions.canInitiateBattleAtLocation(
                scn.LS, scn.game(), site, false, true, false, true));
    }

    @Test
    public void HereWeGoAgainThenCounterattackTripleChainBattlesSameTurn() {
        var scn = GetScenario();
        var site = scn.GetLSStartingLocation();
        var luke = scn.GetLSCard("luke");
        var st = scn.GetDSCard("st");
        var counterattack = scn.GetDSCard("counterattack");
        SetupSiteBattle(scn);
        scn.MoveCardsToDSHand(counterattack);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        FinishBattlePayingDamage(scn);
        PlayHwgaAndStartRebattle(scn);
        assertTrue(scn.IsParticipatingInBattle(luke, st));

        FinishBattlePayingDamage(scn);
        scn.DSActivateForceCheat(2);
        boolean found = false;
        for (int i = 0; i < 8; i++) {
            if (scn.DSAnyDecisionsAvailable()) {
                if (scn.DSCardPlayAvailable(counterattack)) {
                    found = true;
                    break;
                }
                scn.DSPass();
            } else if (scn.LSAnyDecisionsAvailable()) {
                scn.LSPass();
            } else {
                break;
            }
        }
        assertTrue(found);
        scn.DSPlayCard(counterattack);
        scn.PassAllResponses();
        scn.PassForceUseResponses();
        scn.PassBattleStartResponses();

        assertNotNull(scn.gameState().getBattleState());
        assertEquals(scn.DS, scn.gameState().getBattleState().getPlayerInitiatedBattle());
        assertTrue(scn.IsParticipatingInBattle(luke, st));
    }
}
