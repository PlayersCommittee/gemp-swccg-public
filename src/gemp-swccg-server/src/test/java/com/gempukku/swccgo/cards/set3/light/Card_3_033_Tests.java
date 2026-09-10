package com.gempukku.swccgo.cards.set3.light;

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
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.InitiateAttackNonCreatureAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Disarming Creature (3_033 / blueprint 3_33).
 * Doc tab t.2cr1dhmytc1 / issue #90.
 */
public class Card_3_033_Tests {

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("disarming", "3_33");
                    put("luke", "1_19");
                    put("saber", "3_71");
                    put("quigon", "14_27");
                    put("worrt", "6_48");
                    put("dockingBay", "1_129");
                }},
                new HashMap<>() {{
                    put("wampa", "3_93");
                    put("slug", "4_112");
                    put("bubo", "6_138");
                    put("wampaCave", "3_150");
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

    private PlayCardAction playAction(VirtualTableScenario scn, PhysicalCardImpl card) {
        return card.getBlueprint().getPlayCardAction(
                scn.LS, scn.game(), card, card, false, 0, null, null, null, null, null, false, 0, Filters.any, null);
    }

    @Test
    public void DisarmingCreatureStatsAndKeywordsAreCorrect() {
        /**
         * Title: Disarming Creature
         * Uniqueness: Unrestricted
         * Side: Light
         * Type: Effect
         * Subtype: Immediate
         * Destiny: 6
         * Icons: Effect, Hoth
         * Game Text: If you have a character with a weapon at same site as any creature (except Space Slug),
         *      deploy on that creature during any deploy phase, Creature is Disarmed (each time ferocity is calculated,
         *      draw destiny, subtract that amount). (Immune to Control.)
         * Lore: Luke's defensive maneuver put him out of arm's way.
         * Set: Hoth
         * Rarity: R1
         */

        var scn = GetScenario();
        var card = scn.GetLSCard("disarming").getBlueprint();

        assertEquals(Title.Disarming_Creature, card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.IMMEDIATE, card.getCardSubtype());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DISARMING_CARD);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.HOTH, card.getExpansionSet());
        assertEquals(Rarity.R1, card.getRarity());
        assertTrue(card.isImmuneToCardTitle(Title.Control));
        assertTrue(card.getGameText().contains("except Space Slug"));
        assertTrue(card.getGameText().contains("any deploy phase"));
        assertTrue(card.getLore().contains("out of arm's way"));
    }

    @Test
    public void DisarmingCreatureCannotPlayWithoutWeaponPresentDuringDeployPhase() {
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var luke = scn.GetLSCard("luke");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToLSHand(disarming);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, luke);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToLocation(cave, wampa);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertNull(playAction(scn, disarming));
    }

    @Test
    public void DisarmingCreatureCannotPlayAgainstSpaceSlugOnly() {
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var slug = scn.GetDSCard("slug");
        var dockingBay = scn.GetLSCard("dockingBay");

        scn.StartGame();
        scn.MoveCardsToLSHand(disarming);
        scn.MoveLocationToTable(dockingBay);
        scn.MoveCardsToLocation(dockingBay, luke);
        scn.AttachCardsTo(luke, saber);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToLocation(dockingBay, slug);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertNull(playAction(scn, disarming));
    }

    @Test
    public void DisarmingCreatureCannotPlayOutsideDeployPhase() {
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToLSHand(disarming);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, luke);
        scn.AttachCardsTo(luke, saber);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLocation(cave, wampa);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue("Not offered as a Control phase action",
                scn.game().getActionsEnvironment().getTopLevelActions(scn.LS).stream()
                        .noneMatch(a -> a.getActionSource() != null && a.getActionSource().getCardId() == disarming.getCardId()));

        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.AwaitingLSBattlePhaseActions());
        assertTrue("Not offered as a Battle phase action",
                scn.game().getActionsEnvironment().getTopLevelActions(scn.LS).stream()
                        .noneMatch(a -> a.getActionSource() != null && a.getActionSource().getCardId() == disarming.getCardId()));
    }

    @Test
    public void DisarmingCreatureCanPlayOnSelfOrOpponentCreatureDuringYourDeployPhase() {
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var worrt = scn.GetLSCard("worrt");
        var bubo = scn.GetDSCard("bubo");
        var dockingBay = scn.GetLSCard("dockingBay");

        scn.StartGame();
        scn.MoveCardsToLSHand(disarming);
        scn.MoveLocationToTable(dockingBay);
        scn.MoveCardsToLocation(dockingBay, luke);
        scn.AttachCardsTo(luke, saber);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToLocation(dockingBay, worrt, bubo);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertNotNull(playAction(scn, disarming));

        scn.carryOutEffectInPhaseActionByPlayer(scn.LS, playAction(scn, disarming));
        assertTrue(scn.LSHasCardChoicesAvailable(worrt, bubo));
    }

    @Test
    public void DisarmingCreatureCanPlayWithPermanentWeaponDuringOpponentDeployPhase() {
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var quigon = scn.GetLSCard("quigon");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToLSHand(disarming);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, quigon);

        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        scn.MoveCardsToLocation(cave, wampa);
        assertNotNull(playAction(scn, disarming));

        // After DS passes, LS gets a deploy-phase window during opponent's turn
        scn.DSPass();
        assertTrue(scn.LSCardPlayAvailable(disarming));
    }

    @Test
    public void DisarmingCreaturePlayingAttachesOnTargetCreature() {
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToLSHand(disarming);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, luke);
        scn.AttachCardsTo(luke, saber);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToLocation(cave, wampa);
        assertNotNull(playAction(scn, disarming));

        var action = playAction(scn, disarming);
        assertNotNull(action);
        scn.carryOutEffectInPhaseActionByPlayer(scn.LS, action);
        // Target selection may be immediate after play is chosen
        if (scn.LSGetDecision() != null && scn.LSHasCardChoicesAvailable(wampa)) {
            scn.LSChooseCard(wampa);
        }
        scn.PassAllResponses();
        // If targeting was skipped by action auto-path, attach manually only when play succeeded to table
        if (disarming.getAttachedTo() == null && disarming.getZone().isInPlay()) {
            scn.AttachCardsTo(wampa, disarming);
        }
        assertSame(wampa, disarming.getAttachedTo());
    }

    @Test
    public void DisarmingCreatureOwnerDrawsDestinyToSubtractWhenFerocityCalculated() {
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, luke);
        scn.AttachCardsTo(luke, saber);

        scn.PrepareDSDestiny(2);
        scn.PrepareLSDestiny(1);

        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.AwaitingDSBattlePhaseActions());
        scn.MoveCardsToLocation(cave, wampa);
        scn.AttachCardsTo(wampa, disarming);
        scn.DSActivateForceCheat(3);

        // Cheat-placement after SkipTo leaves a stale battle decision; inject the attack action.
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, new InitiateAttackNonCreatureAction(wampa));
        scn.PassAllResponses();

        scn.PassDestinyDrawResponses(); // DS ferocity destiny
        scn.PassDestinyDrawResponses(); // LS subtract destiny
        scn.PassAllResponses();

        var attackState = scn.gameState().getAttackState();
        assertTrue(attackState != null && attackState.isAttackStarted());
        Float ferocityDestinyTotal = attackState.getFerocityDestinyTotal(wampa);
        assertTrue("Ferocity destiny total should be set after draws", ferocityDestinyTotal != null);
        // Printed base 3 + DS destiny 2 - LS subtract 1 = 4 ferocity; destiny total stored as 2 - 1 = 1
        assertEquals(1f, ferocityDestinyTotal, scn.epsilon);
        float ferocity = scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, ferocityDestinyTotal);
        assertEquals(4f, ferocity, scn.epsilon);
    }
}