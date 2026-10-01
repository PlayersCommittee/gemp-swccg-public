package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelCardResultEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelCardBeingPlayedEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.HideUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.ReleaseCaptiveEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Impressive, Most Impressive
 */
public class Card5_052 extends AbstractLostInterrupt {
    public Card5_052() {
        super(Side.LIGHT, 6, Title.Impressive_Most_Impressive, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.R);
        setLore("'Obi-Wan has taught you well.'");
        setGameText("If opponent just attempted to 'freeze' a character using Carbon-Freezing or All Too Easy, draw destiny. Add character's ability. If total destiny > 7, effect canceled. Character released (if captive) and 'hides' (may not participate in battle) for remainder of turn.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, final SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // All Too Easy being played (freeze attempt via Immediate Effect)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.All_Too_Easy)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            final PhysicalCard character = ImpressiveMostImpressiveGetTargetCharacter(game, effect);
            if (character != null) {
                actions.add(ImpressiveMostImpressiveBuildCancelFreezeAttemptAction(playerId, game, effect, self, character, true));
            }
        }

        // Carbon-Freezing control-phase freeze attempt (RespondableEffect after targeting)
        if (TriggerConditions.isPerformingGameTextAction(game, effect, Filters.Carbon_Freezing)) {
            final PhysicalCard character = ImpressiveMostImpressiveGetTargetCharacter(game, effect);
            if (character != null) {
                actions.add(ImpressiveMostImpressiveBuildCancelFreezeAttemptAction(playerId, game, effect, self, character, false));
            }
        }

        return actions.isEmpty() ? null : actions;
    }

    /**
     * Resolve the character targeted by the freeze attempt (ATE attach target or CF captive target).
     */
    static PhysicalCard ImpressiveMostImpressiveGetTargetCharacter(SwccgGame game, Effect effect) {
        Action targetingAction = null;
        if (effect instanceof RespondablePlayingCardEffect) {
            targetingAction = ((RespondablePlayingCardEffect) effect).getTargetingAction();
        }
        else if (effect.getType() == Effect.Type.RESPONDABLE_EFFECT) {
            targetingAction = effect.getAction();
            if (effect instanceof com.gempukku.swccgo.logic.effects.RespondableEffect) {
                Action fromEffect = ((com.gempukku.swccgo.logic.effects.RespondableEffect) effect).getTargetingAction();
                if (fromEffect != null) {
                    targetingAction = fromEffect;
                }
            }
        }
        if (targetingAction == null) {
            return null;
        }

        Map<Integer, Map<PhysicalCard, Set<TargetingReason>>> primaryTargets = targetingAction.getAllPrimaryTargetCards();
        if (primaryTargets == null || primaryTargets.isEmpty()) {
            return null;
        }
        for (Map<PhysicalCard, Set<TargetingReason>> map : primaryTargets.values()) {
            for (PhysicalCard card : map.keySet()) {
                if (Filters.character.accepts(game, card)) {
                    return card;
                }
            }
        }
        // Fallback: any primary target
        for (Map<PhysicalCard, Set<TargetingReason>> map : primaryTargets.values()) {
            if (!map.isEmpty()) {
                return map.keySet().iterator().next();
            }
        }
        return null;
    }

    static PlayInterruptAction ImpressiveMostImpressiveBuildCancelFreezeAttemptAction(
            final String playerId, final SwccgGame game, final Effect effectToCancel, final PhysicalCard self,
            final PhysicalCard character, final boolean cancelCardBeingPlayed) {

        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Cancel freeze attempt on " + GameUtils.getFullName(character));
        action.addAnimationGroup(character);
        // Allow response(s)
        action.allowResponses("Draw destiny to cancel freeze attempt on " + GameUtils.getCardLink(character),
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(
                                new DrawDestinyEffect(action, playerId) {
                                    @Override
                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                        ImpressiveMostImpressiveResolveDestinyDraw(action, game, effectToCancel, character, cancelCardBeingPlayed, totalDestiny);
                                    }
                                }
                        );
                    }
                }
        );
        return action;
    }

    static void ImpressiveMostImpressiveResolveDestinyDraw(
            final PlayInterruptAction action, final SwccgGame game, final Effect effectToCancel,
            final PhysicalCard character, final boolean cancelCardBeingPlayed, final Float totalDestiny) {

        final GameState gameState = game.getGameState();
        if (totalDestiny == null) {
            gameState.sendMessage("Result: Failed due to failed destiny draw");
            return;
        }

        // SpotOverride so ability still resolves if character is captive/inactive
        float ability = game.getModifiersQuerying().getAbility(gameState, character);
        float total = totalDestiny + ability;
        gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
        gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));
        gameState.sendMessage("Total: " + GuiUtils.formatAsString(total));

        if (total > 7) {
            gameState.sendMessage("Result: Succeeded");
            if (cancelCardBeingPlayed) {
                action.appendEffect(
                        new CancelCardBeingPlayedEffect(action, (RespondablePlayingCardEffect) effectToCancel));
            }
            else {
                action.appendEffect(
                        new CancelCardResultEffect(action, effectToCancel));
            }
            // Release if captive (include inactive/captive spotting for CF targets)
            if (character.isCaptive()) {
                action.appendEffect(
                        new ReleaseCaptiveEffect(action, character));
            }
            action.appendEffect(
                    new HideUntilEndOfTurnEffect(action, character));
        }
        else {
            gameState.sendMessage("Result: Failed");
        }
    }
}
