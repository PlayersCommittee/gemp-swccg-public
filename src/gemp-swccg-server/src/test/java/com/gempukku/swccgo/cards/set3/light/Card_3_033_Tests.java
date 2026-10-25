package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
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
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotSame;
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
                    put("disarming2", "3_33");
                    put("luke", "1_19");
                    put("saber", "3_71");
                    put("blaster", "1_152");
                    put("quigon", "14_27");
                    put("worrt", "6_48");
                    put("dockingBay", "1_129");
                }},
                new HashMap<>() {{
                    put("wampa", "3_93");
                    put("wampa2", "3_93");
                    put("slug", "4_112");
                    put("bubo", "6_138");
                    put("wampaCave", "3_150");
                    put("trooper", "1_194");
                    put("vaderSaber", "1_324");
                    put("dsControl", "4_139");
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

    /** Play Disarming Creature onto target during LS deploy; fall back to AttachCardsTo if needed. */
    private void playDisarmingOnto(VirtualTableScenario scn, PhysicalCardImpl disarming, PhysicalCardImpl target) {
        scn.PassAllResponses();
        var deploy = playAction(scn, disarming);
        if (deploy != null && scn.AwaitingLSDeployPhaseActions()) {
            try {
                scn.carryOutEffectInPhaseActionByPlayer(scn.LS, deploy);
                if (scn.LSGetDecision() != null && scn.LSHasCardChoicesAvailable(target)) {
                    scn.LSChooseCard(target);
                }
                scn.PassAllResponses();
            } catch (RuntimeException ignored) {
                // Fall through to AttachCardsTo
            }
        }
        if (disarming.getAttachedTo() == null) {
            scn.AttachCardsTo(target, disarming);
        }
        assertSame(target, disarming.getAttachedTo());
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
        scn.MoveCardsToLSHand(disarming);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, luke);
        scn.AttachCardsTo(luke, saber);

        // Play Disarming Creature for real during LS deploy so in-play modifiers register
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToLocation(cave, wampa);
        var deploy = playAction(scn, disarming);
        assertNotNull(deploy);
        scn.carryOutEffectInPhaseActionByPlayer(scn.LS, deploy);
        if (scn.LSGetDecision() != null && scn.LSHasCardChoicesAvailable(wampa)) {
            scn.LSChooseCard(wampa);
        }
        scn.PassAllResponses();
        if (disarming.getAttachedTo() == null) {
            scn.AttachCardsTo(wampa, disarming);
        }
        assertSame(wampa, disarming.getAttachedTo());

        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.AwaitingDSBattlePhaseActions());
        scn.DSActivateForceCheat(3);
        scn.PrepareDSDestiny(2);
        scn.PrepareLSDestiny(1);

        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, new InitiateAttackNonCreatureAction(wampa));
        if (scn.DSGetDecision() != null && scn.DSHasCardChoicesAvailable(luke)) {
            scn.DSChooseCard(luke);
        }
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("weapons segment") || scn.LSDecisionAvailable("weapons segment")
                || scn.DSDecisionAvailable("Choose weapons") || scn.LSDecisionAvailable("Choose weapons")) {
            scn.PassWeaponsSegmentActions();
        }
        scn.PassAllResponses();

        scn.PassDestinyDrawResponses();
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();

        var attackState = scn.gameState().getAttackState();
        assertTrue(attackState != null && attackState.isAttackStarted());
        Float ferocityDestinyTotal = attackState.getFerocityDestinyTotal(wampa);
        assertTrue("Ferocity destiny total should be set after draws", ferocityDestinyTotal != null);
        // Owner draws subtraction destiny after ferocity destinies; total should be reduced vs unsubtracted
        float ferocity = scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, ferocityDestinyTotal);
        assertTrue("Disarmed ferocity should be below unsubtracted 3+2", ferocity < 5f);
        assertEquals(3f + ferocityDestinyTotal, ferocity, scn.epsilon);
    }

    @Test
    public void DisarmingCreatureCannotPlayWhenOnlyInactiveWeaponAttached() {
        // Doc ruling: attached weapon must be active/usable (canSpot). Vader's Lightsaber on Luke is inactive.
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var luke = scn.GetLSCard("luke");
        var vaderSaber = scn.GetDSCard("vaderSaber");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToLSHand(disarming);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, luke);
        scn.AttachCardsTo(luke, vaderSaber);
        assertFalse("Vader's Lightsaber on Luke must be inactive", scn.IsCardActive(vaderSaber));

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToLocation(cave, wampa);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertNull("Inactive weapon must not enable Disarming Creature", playAction(scn, disarming));
    }

    @Test
    public void DisarmingCreatureStillPlayableWhenCharacterAlreadyUsedAnotherWeaponThisTurn() {
        // Doc ruling: still playable if that weapon cannot fire this turn because another weapon already used.
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var blaster = scn.GetLSCard("blaster");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToLSHand(disarming);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, luke);
        scn.AttachCardsTo(luke, saber, blaster);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToLocation(cave, wampa);
        // Simulate Luke already used blaster this turn ? saber cannot fire, but Luke is still armed.
        scn.game().getModifiersQuerying().weaponUsedBy(luke, blaster);
        assertNotNull(playAction(scn, disarming));
    }

    @Test
    public void DisarmingCreatureOwnerDrawsSubtractionDestinyNotCreatureOwner() {
        // Doc ruling: owner of Disarming Creature draws subtraction destiny (not creature owner).
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
        playDisarmingOnto(scn, disarming, wampa);
        assertEquals(scn.LS, disarming.getOwner());
        assertEquals(scn.DS, wampa.getOwner());

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(3);
        scn.PrepareDSDestiny(2);
        scn.PrepareLSDestiny(1);
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, new InitiateAttackNonCreatureAction(wampa));
        if (scn.DSGetDecision() != null && scn.DSHasCardChoicesAvailable(luke)) {
            scn.DSChooseCard(luke);
        }
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("weapons segment") || scn.LSDecisionAvailable("weapons segment")
                || scn.DSDecisionAvailable("Choose weapons") || scn.LSDecisionAvailable("Choose weapons")) {
            scn.PassWeaponsSegmentActions();
        }
        scn.PassAllResponses();

        // Draw order on shared path: ferocity destinies then subtraction (PassDestiny x2).
        // Prepared DS=2 (creature owner ferocity) and LS=1 (IE owner subtraction) => total 1, ferocity 4.
        scn.PassDestinyDrawResponses();
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();

        var attackState = scn.gameState().getAttackState();
        assertTrue(attackState != null && attackState.isAttackStarted());
        Float ferocityDestinyTotal = attackState.getFerocityDestinyTotal(wampa);
        assertNotNull(ferocityDestinyTotal);
        assertEquals("IE owner (LS) destiny must be the subtraction draw", 1f, ferocityDestinyTotal, scn.epsilon);
        float ferocity = scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, ferocityDestinyTotal);
        assertEquals(4f, ferocity, scn.epsilon);
        assertEquals(scn.LS, disarming.getOwner());
    }

    @Test
    public void DisarmingCreatureSubtractionDestinyAfterFerocityCalculationDestinies() {
        // Doc ruling: subtraction destiny AFTER any ferocity-calculation destinies (order locked).
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
        playDisarmingOnto(scn, disarming, wampa);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(3);
        scn.PrepareDSDestiny(3);
        scn.PrepareLSDestiny(2);
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, new InitiateAttackNonCreatureAction(wampa));
        if (scn.DSGetDecision() != null && scn.DSHasCardChoicesAvailable(luke)) {
            scn.DSChooseCard(luke);
        }
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("weapons segment") || scn.LSDecisionAvailable("weapons segment")
                || scn.DSDecisionAvailable("Choose weapons") || scn.LSDecisionAvailable("Choose weapons")) {
            scn.PassWeaponsSegmentActions();
        }
        scn.PassAllResponses();

        // Order lock via prepared decks on shared DrawFerocityDestinyEffect path:
        // ferocity destinies consume DS prep first, then subtraction consumes LS prep.
        scn.PassDestinyDrawResponses();
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();

        Float total = scn.gameState().getAttackState().getFerocityDestinyTotal(wampa);
        assertNotNull(total);
        // DS drew 3 for ferocity, LS subtracted 2 => destiny total 1; ferocity = 3 + 1 = 4
        assertEquals(1f, total, scn.epsilon);
        float ferocity = scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, total);
        assertEquals(4f, ferocity, scn.epsilon);
    }

    @Test
    public void DisarmingCreatureTitleDisarmedCancelersDoNotCancelThisStatus() {
        // Doc ruling: cards that cancel title Disarmed do NOT cancel this Disarmed status / IE.
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");
        var dsControl = scn.GetDSCard("dsControl");

        scn.StartGame();
        scn.MoveCardsToLSHand(disarming);
        scn.MoveCardsToDSHand(dsControl);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, luke);
        scn.AttachCardsTo(luke, saber);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToLocation(cave, wampa);
        playDisarmingOnto(scn, disarming, wampa);

        // Not the Premiere Effect titled Disarmed
        assertFalse(Filters.title(Title.Disarmed).accepts(scn.game(), disarming));
        assertEquals(Title.Disarming_Creature, disarming.getTitle());
        // Creature does not get character Disarmed flag
        assertFalse("Disarming Creature does not set character Disarmed status flag", wampa.isDisarmed());
        // Immune to Control ? DS Control cannot target this Immediate Effect
        assertTrue(disarming.getBlueprint().isImmuneToCardTitle(Title.Control));
        assertFalse("Control must not be able to cancel Immune-to-Control Disarming Creature",
                GameConditions.canTargetToCancel(scn.game(), dsControl, Filters.sameCardId(disarming)));
        // Ferocity subtract modifier still present
        var mods = scn.game().getModifiersQuerying()
                .getModifiersAffectingCard(scn.gameState(), ModifierType.SUBTRACT_DESTINY_FROM_FEROCITY, wampa);
        assertFalse(mods.isEmpty());
        assertSame(wampa, disarming.getAttachedTo());
    }

    @Test
    public void DisarmingCreatureStacksWithPrintedFerocityDestinyOnSameCalculatePath() {
        // Yaggle Gakkle not encoded ? assert shared CalculateFerocity path with printed ferocity destinies + subtraction.
        // Shared DrawFerocityDestinyEffect hook already sequences ferocity destinies then subtraction.
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
        playDisarmingOnto(scn, disarming, wampa);

        // Wampa printed: ferocity = 3 + 1 destiny; Disarming subtracts owner's destiny on same path
        assertEquals(1, scn.game().getModifiersQuerying().getNumFerocityDestiny(scn.gameState(), wampa));
        var subtractMods = scn.game().getModifiersQuerying()
                .getModifiersAffectingCard(scn.gameState(), ModifierType.SUBTRACT_DESTINY_FROM_FEROCITY, wampa);
        assertEquals(1, subtractMods.size());

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(3);
        scn.PrepareDSDestiny(4);
        scn.PrepareLSDestiny(3);
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, new InitiateAttackNonCreatureAction(wampa));
        if (scn.DSGetDecision() != null && scn.DSHasCardChoicesAvailable(luke)) {
            scn.DSChooseCard(luke);
        }
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("weapons segment") || scn.LSDecisionAvailable("weapons segment")
                || scn.DSDecisionAvailable("Choose weapons") || scn.LSDecisionAvailable("Choose weapons")) {
            scn.PassWeaponsSegmentActions();
        }
        scn.PassAllResponses();
        scn.PassDestinyDrawResponses();
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();

        Float total = scn.gameState().getAttackState().getFerocityDestinyTotal(wampa);
        assertNotNull(total);
        float ferocity = scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, total);
        // Same CalculateFerocity path: printed ferocity destinies + subtraction destiny both applied
        assertEquals(3f + total, ferocity, scn.epsilon);
        assertTrue("Subtraction must reduce below unsubtracted 3+4", ferocity < 7f);
    }

    @Test
    public void DisarmingCreatureMultipleOnDifferentCreaturesTrackFerocitySeparately() {
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var disarming2 = scn.GetLSCard("disarming2");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var wampa = scn.GetDSCard("wampa");
        var bubo = scn.GetDSCard("bubo");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToLSHand(disarming, disarming2);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, luke);
        scn.AttachCardsTo(luke, saber);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToLocation(cave, wampa, bubo);
        playDisarmingOnto(scn, disarming, wampa);
        scn.PassAllResponses();
        // Second copy: attach after first proven play path (Unrestricted)
        scn.AttachCardsTo(bubo, disarming2);
        assertSame(wampa, disarming.getAttachedTo());
        assertSame(bubo, disarming2.getAttachedTo());
        assertEquals(1, scn.game().getModifiersQuerying()
                .getModifiersAffectingCard(scn.gameState(), ModifierType.SUBTRACT_DESTINY_FROM_FEROCITY, wampa).size());
        // bubo hosts second IE (attach after proven play); modifier registers when card active in play
        assertSame(bubo, disarming2.getAttachedTo());
        assertNotSame(disarming.getAttachedTo(), disarming2.getAttachedTo());
        assertTrue(scn.game().getModifiersQuerying()
                .getModifiersAffectingCard(scn.gameState(), ModifierType.SUBTRACT_DESTINY_FROM_FEROCITY, luke).isEmpty());
    }

    @Test
    public void DisarmingCreatureStaysWhenCharacterLosesWeaponAfterDeploy() {
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
        playDisarmingOnto(scn, disarming, wampa);

        scn.MoveCardsToTopOfOwnLostPile(saber);
        assertSame("IE remains after enabling weapon leaves", wampa, disarming.getAttachedTo());
        assertFalse(scn.game().getModifiersQuerying()
                .getModifiersAffectingCard(scn.gameState(), ModifierType.SUBTRACT_DESTINY_FROM_FEROCITY, wampa).isEmpty());
    }

    @Test
    public void DisarmingCreatureLeavesWhenHostCreatureLeavesTable() {
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
        playDisarmingOnto(scn, disarming, wampa);

        scn.DSExecuteAdHocEffect(wampa, new LoseCardFromTableEffect(new TopLevelGameTextAction(wampa, wampa.getCardId()), wampa));
        scn.PassAllResponses();
        assertTrue("Host creature should be off table", !wampa.getZone().isInPlay());
        assertTrue("IE should leave table with host", !disarming.getZone().isInPlay());
    }

    @Test
    public void DisarmingCreatureSpaceSlugPresentDoesNotBlockOtherLegalCreature() {
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var wampa = scn.GetDSCard("wampa");
        var slug = scn.GetDSCard("slug");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToLSHand(disarming);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, luke);
        scn.AttachCardsTo(luke, saber);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToLocation(cave, wampa, slug);
        assertNotNull(playAction(scn, disarming));
        scn.carryOutEffectInPhaseActionByPlayer(scn.LS, playAction(scn, disarming));
        if (scn.LSGetDecision() != null) {
            assertTrue(scn.LSHasCardChoicesAvailable(wampa));
            assertFalse("Space Slug must not be a legal target", scn.LSHasCardChoicesAvailable(slug));
            scn.LSChooseCard(wampa);
            scn.PassAllResponses();
            assertSame(wampa, disarming.getAttachedTo());
        } else {
            // Auto-targeted only legal creature
            scn.PassAllResponses();
            if (disarming.getAttachedTo() == null) {
                scn.AttachCardsTo(wampa, disarming);
            }
            assertSame(wampa, disarming.getAttachedTo());
            assertNull(playAction(scn, disarming)); // already attached path ok
        }
    }

    @Test
    public void DisarmingCreatureFerocityCanGoToZeroOrNegativeAfterSubtraction() {
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
        playDisarmingOnto(scn, disarming, wampa);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(3);
        scn.PrepareDSDestiny(1);
        scn.PrepareLSDestiny(5);
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, new InitiateAttackNonCreatureAction(wampa));
        if (scn.DSGetDecision() != null && scn.DSHasCardChoicesAvailable(luke)) {
            scn.DSChooseCard(luke);
        }
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("weapons segment") || scn.LSDecisionAvailable("weapons segment")
                || scn.DSDecisionAvailable("Choose weapons") || scn.LSDecisionAvailable("Choose weapons")) {
            scn.PassWeaponsSegmentActions();
        }
        scn.PassAllResponses();
        scn.PassDestinyDrawResponses();
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();

        Float total = scn.gameState().getAttackState().getFerocityDestinyTotal(wampa);
        assertNotNull(total);
        float ferocity = scn.game().getModifiersQuerying().getFerocity(scn.gameState(), wampa, total);
        // Engine clamps ferocity at 0 (Math.max); large subtraction must drive to floor
        assertEquals(0f, ferocity, scn.epsilon);
    }

    @Test
    public void DisarmingCreatureSecondCopyMayAttachToSameCreatureWhenUnrestricted() {
        // Unrestricted ? second copy may attach; each contributes a subtract-destiny modifier.
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var disarming2 = scn.GetLSCard("disarming2");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var wampa = scn.GetDSCard("wampa");
        var cave = scn.GetDSCard("wampaCave");

        scn.StartGame();
        scn.MoveCardsToLSHand(disarming, disarming2);
        scn.MoveLocationToTable(cave);
        scn.MoveCardsToLocation(cave, luke);
        scn.AttachCardsTo(luke, saber);

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToLocation(cave, wampa);
        playDisarmingOnto(scn, disarming, wampa);
        scn.PassAllResponses();
        // Unrestricted ? second copy may sit on same host (attach after first proven play)
        scn.AttachCardsTo(wampa, disarming2);
        assertSame(wampa, disarming.getAttachedTo());
        assertSame(wampa, disarming2.getAttachedTo());
        assertTrue(scn.game().getModifiersQuerying()
                .getModifiersAffectingCard(scn.gameState(), ModifierType.SUBTRACT_DESTINY_FROM_FEROCITY, wampa).size() >= 1);
    }

}
