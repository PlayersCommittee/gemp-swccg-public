package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleThisTurnRecord;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Used
 * Title: Apology Accepted
 */
public class Card4_137 extends AbstractUsedInterrupt {
    public Card4_137() {
        super(Side.DARK, 6, Title.Apology_Accepted, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.C);
        setLore("'I shall assume full responsibility for losing them and apologize to Lord Vader.' Needa discovered that Vader was only slightly more forgiving than the Emperor.");
        setGameText("At the end of any battle phase, lose one of your Imperials of ability < 6 who survived a battle you lost this turn. Activate Force up to either that character's forfeit value or the amount of your battle damage in that battle.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (!TriggerConditions.isEndOfEachPhase(game, effectResult, Phase.BATTLE)) {
            return null;
        }

        final Filter eligibleImperialFilter = ApologyAcceptedGetEligibleImperialFilter(game, playerId, self);
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        if (!GameConditions.canTarget(game, self, targetingReason, eligibleImperialFilter)) {
            return null;
        }
        if (!ApologyAcceptedIsPlayableGivenForce(game, playerId, eligibleImperialFilter)) {
            return null;
        }

        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Lose an Imperial to activate Force");
        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose Imperial to lose", targetingReason, eligibleImperialFilter) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedImperial) {
                        action.addAnimationGroup(targetedImperial);
                        // Capture forfeit / battle-damage values before the Imperial leaves table
                        final float activationCap = ApologyAcceptedGetActivationCap(game, playerId, targetedImperial);
                        final boolean allowsZero = ApologyAcceptedAllowsZeroActivation(game, playerId, targetedImperial);
                        // Pay cost(s) - lose the Imperial
                        action.appendCost(
                                new LoseCardFromTableEffect(action, targetedImperial));
                        // Allow response(s)
                        action.allowResponses("Activate Force using " + GameUtils.getCardLink(targetedImperial),
                                new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        ApologyAcceptedAppendActivateForce(action, game, playerId, activationCap, allowsZero);
                                    }
                                }
                        );
                    }
                }
        );
        return Collections.singletonList(action);
    }

    /**
     * Filter: your Imperials of ability < 6 that survived a battle you lost this turn (still on table).
     */
    static Filter ApologyAcceptedGetEligibleImperialFilter(final SwccgGame game, final String playerId, final PhysicalCard self) {
        return Filters.and(
                Filters.your(self),
                Filters.Imperial,
                Filters.character,
                Filters.abilityLessThan(6),
                new Filter() {
                    @Override
                    public boolean accepts(GameState gameState, com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                        return ApologyAcceptedDidSurviveLostBattleThisTurn(game, playerId, physicalCard);
                    }
                }
        );
    }

    static boolean ApologyAcceptedDidSurviveLostBattleThisTurn(SwccgGame game, String playerId, PhysicalCard card) {
        if (card == null) {
            return false;
        }
        List<BattleThisTurnRecord> lostBattles = game.getModifiersQuerying().getBattlesLostThisTurn(playerId);
        for (BattleThisTurnRecord record : lostBattles) {
            if (record.wasParticipant(card.getCardId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Max activation from "either forfeit or battle damage" across lost battles the card survived,
     * taking the highest such value (per Doc).
     */
    static float ApologyAcceptedGetActivationCap(SwccgGame game, String playerId, PhysicalCard imperial) {
        float forfeit = game.getModifiersQuerying().getForfeit(game.getGameState(), imperial);
        float bestBattleDamage = 0f;
        for (BattleThisTurnRecord record : game.getModifiersQuerying().getBattlesLostThisTurn(playerId)) {
            if (record.wasParticipant(imperial.getCardId())) {
                bestBattleDamage = Math.max(bestBattleDamage, record.getBattleDamageFor(playerId));
            }
        }
        return Math.max(forfeit, bestBattleDamage);
    }

    /**
     * True if forfeit is 0 or any survived lost-battle's battle damage is 0 (either side of "either").
     */
    static boolean ApologyAcceptedAllowsZeroActivation(SwccgGame game, String playerId, PhysicalCard imperial) {
        float forfeit = game.getModifiersQuerying().getForfeit(game.getGameState(), imperial);
        if (forfeit == 0f) {
            return true;
        }
        for (BattleThisTurnRecord record : game.getModifiersQuerying().getBattlesLostThisTurn(playerId)) {
            if (record.wasParticipant(imperial.getCardId()) && record.getBattleDamageFor(playerId) == 0f) {
                return true;
            }
        }
        return false;
    }

    static boolean ApologyAcceptedIsPlayableGivenForce(SwccgGame game, String playerId, Filter eligibleImperialFilter) {
        CollectionWithForce check = ApologyAcceptedFindPlayableTarget(game, playerId, eligibleImperialFilter);
        return check != null;
    }

    private static class CollectionWithForce {
        final PhysicalCard card;
        CollectionWithForce(PhysicalCard card) { this.card = card; }
    }

    static CollectionWithForce ApologyAcceptedFindPlayableTarget(SwccgGame game, String playerId, Filter eligibleImperialFilter) {
        for (PhysicalCard imperial : Filters.filterActive(game, null, eligibleImperialFilter)) {
            boolean allowsZero = ApologyAcceptedAllowsZeroActivation(game, playerId, imperial);
            float cap = ApologyAcceptedGetActivationCap(game, playerId, imperial);
            int reserve = game.getGameState().getReserveDeckSize(playerId);
            if (allowsZero) {
                return new CollectionWithForce(imperial);
            }
            if (cap >= 1f && reserve >= 1 && GameConditions.canActivateForce(game, playerId)) {
                return new CollectionWithForce(imperial);
            }
        }
        return null;
    }

    static void ApologyAcceptedAppendActivateForce(final PlayInterruptAction action, final SwccgGame game,
                                                   final String playerId, final float activationCap,
                                                   final boolean allowsZero) {
        final int reserve = game.getGameState().getReserveDeckSize(playerId);
        final int maxActivate = Math.min((int) Math.floor(activationCap), reserve);
        final int minActivate = allowsZero ? 0 : 1;

        if (maxActivate < minActivate) {
            // Nothing to activate (should not normally reach here if playability checked)
            return;
        }
        if (maxActivate == 0) {
            // Activate 0 Force: Used Interrupt still cycles; no Reserve cards move
            return;
        }

        // If 0 is allowed and player may choose 0..max, include 0 in the decision range
        action.appendEffect(
                new PlayoutDecisionEffect(action, playerId,
                        new IntegerAwaitingDecision("Choose amount of Force to activate", minActivate, maxActivate, maxActivate) {
                            @Override
                            public void decisionMade(final int result) throws DecisionResultInvalidException {
                                if (result > 0) {
                                    action.appendEffect(
                                            new ActivateForceEffect(action, playerId, result));
                                }
                            }
                        }
                )
        );
    }
}

