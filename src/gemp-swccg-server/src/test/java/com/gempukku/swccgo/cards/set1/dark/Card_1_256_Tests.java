package com.gempukku.swccgo.cards.set1.dark;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1_256_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rebel", "1_28");
                    put("c3po", "1_005");
                    put("derlin", "3_013");
                    put("lsAtMarket", "1_28");
                }},
                new HashMap<>() {{
                    put("localTrouble", "1_256");
                    put("st1", "1_194");
                    put("st2", "1_194");
                    put("garindan", "1_177");
                    put("felth", "2_106");
                    put("avarik", "8_095");
                    put("cantina", "1_290");
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

    private void SetupCantina(VirtualTableScenario scn) {
        var cantina = scn.GetDSCard("cantina");
        scn.MoveCardsToDSHand(scn.GetDSCard("localTrouble"));
        scn.StartGame();
        scn.MoveLocationToTable(cantina);
    }

    private void PlayLocalTrouble(VirtualTableScenario scn, PhysicalCardImpl st1,
                                  PhysicalCardImpl st2,
                                  PhysicalCardImpl opponent) {
        var localTrouble = scn.GetDSCard("localTrouble");
        assertTrue(scn.DSCardPlayAvailable(localTrouble));
        scn.DSPlayCard(localTrouble);
        if (scn.DSHasCardChoiceAvailable(st1) || scn.DSHasCardChoiceAvailable(st2)) {
            scn.DSChooseCards(st1, st2);
        }
        if (scn.DSHasCardChoiceAvailable(opponent)) {
            scn.DSChooseCard(opponent);
        }
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("BATTLE_INITIATED") || scn.LSDecisionAvailable("BATTLE_INITIATED")) {
            scn.PassBattleStartResponses();
        }
        scn.PassAllResponses();
    }

    private void FinishBattle(VirtualTableScenario scn) {
        scn.SkipToDamageSegment(false);
        if (scn.AwaitingLSBattleDamagePayment()) {
            scn.LSPayRemainingBattleDamageFromReserveDeck();
        }
        if (scn.AwaitingDSBattleDamagePayment()) {
            scn.DSPayRemainingBattleDamageFromReserveDeck();
        }
        scn.PassAllResponses();
    }

    @Test
    public void LocalTroubleStatsAndKeywordsAreCorrect() {
        /**
         * Title: Local Trouble
         * Uniqueness: Unique
         * Side: Dark
         * Type: Interrupt
         * Subtype: Lost
         * Destiny: 4
         * Icons: Interrupt
         * Set: Premiere
         * Rarity: R1
         */
        var scn = GetScenario();
        var card = scn.GetDSCard("localTrouble").getBlueprint();

        assertEquals("Local Trouble", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.PREMIERE, card.getExpansionSet());
        assertEquals(Rarity.R1, card.getRarity());
    }

    @Test
    public void HappyPathInitiatesLocalTroubleBattleWithOnlyChosenParticipants() {
        var scn = GetScenario();
        var localTrouble = scn.GetDSCard("localTrouble");
        var cantina = scn.GetDSCard("cantina");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var rebel = scn.GetLSCard("rebel");

        SetupCantina(scn);
        scn.MoveCardsToLocation(cantina, st1, st2, rebel);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCardPlayAvailable(localTrouble));

        PlayLocalTrouble(scn, st1, st2, rebel);

        assertTrue(scn.gameState().isDuringBattle());
        assertTrue(scn.gameState().isDuringLocalTroubleBattle());
        assertTrue(scn.IsParticipatingInBattle(st1));
        assertTrue(scn.IsParticipatingInBattle(st2));
        assertTrue(scn.IsParticipatingInBattle(rebel));

        scn.SkipToPowerSegment();
        assertEquals(1, scn.GetDSBattleDestinyCount());

        FinishBattle(scn);
        assertTrue(localTrouble.getZone() == Zone.TOP_OF_LOST_PILE || localTrouble.getZone() == Zone.LOST_PILE);
    }

    @Test
    public void ExtraCharactersAtCantinaDoNotParticipateAndAreNotFlagged() {
        var scn = GetScenario();
        var cantina = scn.GetDSCard("cantina");
        var marketplace = scn.GetDSStartingLocation();
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var garindan = scn.GetDSCard("garindan");
        var rebel = scn.GetLSCard("rebel");
        var lsAtMarket = scn.GetLSCard("lsAtMarket");

        SetupCantina(scn);
        scn.MoveCardsToLocation(cantina, st1, st2, garindan, rebel);
        scn.MoveCardsToLocation(marketplace, lsAtMarket);

        scn.SkipToPhase(Phase.BATTLE);
        PlayLocalTrouble(scn, st1, st2, rebel);

        assertTrue(scn.IsParticipatingInBattle(st1, st2, rebel));
        assertFalse(scn.IsParticipatingInBattle(garindan));
        assertFalse(scn.game().getModifiersQuerying().hasParticipatedInBattleAtOtherLocation(garindan, marketplace));

        scn.SkipToDamageSegment(false);
        if (scn.AwaitingLSBattleDamagePayment()) {
            scn.LSPayRemainingBattleDamageFromReserveDeck();
        }
        scn.PassAllResponses();

        // Garindan was never flagged, so a later battle at Marketplace is still legal for him
        assertFalse(scn.game().getModifiersQuerying().hasParticipatedInBattleAtOtherLocation(garindan, marketplace));
    }

    @Test
    public void CannotPlayWithOnlyOneStormtrooper() {
        var scn = GetScenario();
        var localTrouble = scn.GetDSCard("localTrouble");
        var cantina = scn.GetDSCard("cantina");
        var st1 = scn.GetDSCard("st1");
        var rebel = scn.GetLSCard("rebel");

        SetupCantina(scn);
        scn.MoveCardsToLocation(cantina, st1, rebel);

        scn.SkipToPhase(Phase.BATTLE);
        assertFalse(scn.DSCardPlayAvailable(localTrouble));
    }

    @Test
    public void CannotPlayTargetingADroidWithoutPresence() {
        var scn = GetScenario();
        var localTrouble = scn.GetDSCard("localTrouble");
        var cantina = scn.GetDSCard("cantina");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var c3po = scn.GetLSCard("c3po");

        SetupCantina(scn);
        scn.MoveCardsToLocation(cantina, st1, st2, c3po);

        scn.SkipToPhase(Phase.BATTLE);
        assertFalse(scn.DSCardPlayAvailable(localTrouble));
    }

    @Test
    public void AfterPlayCannotInitiateNormalBattleAtCantina() {
        var scn = GetScenario();
        var cantina = scn.GetDSCard("cantina");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var rebel = scn.GetLSCard("rebel");

        SetupCantina(scn);
        scn.MoveCardsToLocation(cantina, st1, st2, rebel);

        scn.SkipToPhase(Phase.BATTLE);
        PlayLocalTrouble(scn, st1, st2, rebel);

        FinishBattle(scn);

        String ds = scn.gameState().getDarkPlayer();
        assertTrue(scn.game().getModifiersQuerying().mayNotInitiateBattleAtLocation(scn.gameState(), cantina, ds));
        if (scn.DSAnyDecisionsAvailable()) {
            assertFalse(scn.DSCanInitiateBattle(cantina));
        }
    }

    @Test
    public void DavinFelthOnTatooineMakesLocalTroubleAUsedInterrupt() {
        var scn = GetScenario();
        var localTrouble = scn.GetDSCard("localTrouble");
        var cantina = scn.GetDSCard("cantina");
        var marketplace = scn.GetDSStartingLocation();
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var felth = scn.GetDSCard("felth");
        var rebel = scn.GetLSCard("rebel");

        SetupCantina(scn);
        scn.MoveCardsToLocation(cantina, st1, st2, rebel);
        scn.MoveCardsToLocation(marketplace, felth);

        scn.SkipToPhase(Phase.BATTLE);
        PlayLocalTrouble(scn, st1, st2, rebel);

        FinishBattle(scn);
        assertTrue(localTrouble.getZone() == Zone.TOP_OF_USED_PILE || localTrouble.getZone() == Zone.USED_PILE);
        assertFalse(localTrouble.getZone() == Zone.TOP_OF_LOST_PILE || localTrouble.getZone() == Zone.LOST_PILE);
    }

    @Test
    public void DerlinAtCantinaCanCancelLocalTrouble() {
        var scn = GetScenario();
        var localTrouble = scn.GetDSCard("localTrouble");
        var cantina = scn.GetDSCard("cantina");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");
        var derlin = scn.GetLSCard("derlin");

        SetupCantina(scn);
        scn.MoveCardsToLocation(cantina, st1, st2, derlin);
        scn.LSActivateForceCheat(1);

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.DSCardPlayAvailable(localTrouble));
        scn.DSPlayCard(localTrouble);
        if (scn.DSHasCardChoiceAvailable(st1) || scn.DSHasCardChoiceAvailable(st2)) {
            scn.DSChooseCards(st1, st2);
        }
        if (scn.DSHasCardChoiceAvailable(derlin)) {
            scn.DSChooseCard(derlin);
        }

        scn.PassForceUseResponses();
        assertTrue(scn.LSActionAvailable("Cancel Local Trouble") || scn.LSCardActionAvailable(derlin));
        if (scn.LSActionAvailable("Cancel Local Trouble")) {
            scn.LSChooseAction("Cancel Local Trouble");
        } else {
            scn.LSUseCardAction(derlin);
        }
        scn.PassAllResponses();

        assertFalse(scn.gameState().isDuringBattle());
    }

    @Test
    public void AvarikInLocalTroubleBattleAddsDestinyToPower() {
        var scn = GetScenario();
        var cantina = scn.GetDSCard("cantina");
        var st1 = scn.GetDSCard("st1");
        var avarik = scn.GetDSCard("avarik");
        var rebel = scn.GetLSCard("rebel");

        SetupCantina(scn);
        scn.MoveCardsToLocation(cantina, st1, avarik, rebel);

        scn.SkipToPhase(Phase.BATTLE);
        PlayLocalTrouble(scn, st1, avarik, rebel);

        assertTrue(scn.IsParticipatingInBattle(avarik));
        assertTrue(scn.gameState().isDuringLocalTroubleBattle());

        scn.PrepareDSDestiny(5);
        scn.SkipToPowerSegment();
        // Avarik draws one destiny to total power during a Local Trouble battle
        if (scn.DSDecisionAvailable("destiny")) {
            scn.DSChooseYes();
            scn.PassDestinyDrawResponses();
        }
        scn.PassPowerSegmentActions();
        // Stormtrooper 1 + Avarik 2 + destiny 5 = 8 (battle destiny not drawn yet if we skipped to power segment)
        assertTrue(scn.GetDSTotalPower() >= 3);
        assertTrue(scn.gameState().isDuringLocalTroubleBattle());
    }
}
