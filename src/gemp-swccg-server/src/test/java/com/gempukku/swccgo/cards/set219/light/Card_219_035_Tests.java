package com.gempukku.swccgo.cards.set219.light;

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

public class Card_219_035_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("fenn", "219_35");
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
    public void FennRauStatsAndKeywordsAreCorrect() {
        /**
         * Title: Fenn Rau
         * Uniqueness: Unique
         * Side: Light
         * Type: Rebel / Alien
         * Destiny: 2
         * Deploy: 4
         * Power: 4
         * Ability: 3
         * Forfeit: 5
         * Armor: 5
         * Icons: Pilot, Warrior, Virtual Set 19, Alien, Rebel
         * Keywords: Scout
         * Game Text: [Pilot] 3. During battle, if another Mandalorian here, opponent's total power is -2.
         *         Once per game, at the end of a battle here, may return Fenn Rau to hand to activate 2 Force
         *         (if Fenn Rau won a battle this turn, may also retrieve 1 Force).
         * Set: Set 19
         * Rarity: V
         */
        var scn = GetScenario();
        var card = scn.GetLSCard("fenn").getBlueprint();

        assertEquals("Fenn Rau", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(5, card.getArmor(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SCOUT);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.VIRTUAL_SET_19);
            add(Icon.ALIEN);
            add(Icon.REBEL);
        }});
        assertEquals(ExpansionSet.SET_19, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void FennRauDoesNotRetrieveWhenUnableToActivateTwoForce() {
        var scn = GetScenario();

        var fenn = scn.GetLSCard("fenn");
        var stormtrooper = scn.GetDSFiller(1);
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, fenn, stormtrooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PassAllResponses();
        while (scn.GetLSReserveDeckCount() > 1) {
            scn.MoveCardsToTopOfLSUsedPile(scn.GetTopOfLSReserveDeck());
        }
        assertTrue(scn.LSCanInitiateBattle(site));
        scn.LSInitiateBattle(site);
        scn.SkipToDamageSegment(false);
        if (scn.AwaitingDSBattleDamagePayment()) {
            scn.DSPayRemainingBattleDamageFromReserveDeck();
        }

        assertFalse("Need 2 in Reserve to offer return-to-hand to activate 2",
                walkBattleEndForFennActivate(scn, fenn));
    }

    @Test
    public void FennRauMayRetrieveAfterActivatingTwoForceWhenHeWonTheBattle() {
        var scn = GetScenario();

        var fenn = scn.GetLSCard("fenn");
        var stormtrooper = scn.GetDSFiller(1);
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, fenn, stormtrooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PassAllResponses();
        assertTrue(scn.LSCanInitiateBattle(site));
        scn.LSInitiateBattle(site);
        scn.SkipToDamageSegment(false);
        if (scn.AwaitingDSBattleDamagePayment()) {
            scn.DSPayRemainingBattleDamageFromReserveDeck();
        }

        assertTrue(walkBattleEndForFennActivate(scn, fenn));
        scn.LSUseCardAction(fenn, "Activate");
        scn.PassAllResponses();

        assertEquals(Zone.HAND, fenn.getZone());
        assertTrue(scn.LSDecisionAvailable("Retrieve 1 Force"));
    }

    /**
     * Walks optional responses after the damage segment until Fenn Rau's Activate is offered,
     * or the battle-end window is gone. Does not call LSCardActionAvailable unless Light has
     * an action-choice decision (that helper NPEs on null cardId).
     */
    private boolean walkBattleEndForFennActivate(VirtualTableScenario scn, PhysicalCardImpl fenn) {
        for (int i = 0; i < 20; ++i) {
            if (lsHasActivateAction(scn, fenn)) {
                return true;
            }
            var decision = scn.GetCurrentDecision();
            if (decision != null && decision.getText() != null
                    && decision.getText().toLowerCase().contains("optional")) {
                scn.PassResponses("optional");
                continue;
            }
            return false;
        }
        return false;
    }

    private boolean lsHasActivateAction(VirtualTableScenario scn, PhysicalCardImpl fenn) {
        var decision = scn.LSGetDecision();
        if (decision == null) {
            return false;
        }
        var actionText = decision.getDecisionParameters().get("actionText");
        if (actionText == null) {
            return false;
        }
        return scn.LSCardActionAvailable(fenn, "Activate");
    }
}
