package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtOpponentsHandEffect;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.ChooseArbitraryCardsEffect;
import com.gempukku.swccgo.cards.effects.choose.ChooseAndLoseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.rules.Rule;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Lost
 * Title: Frustration
 */
public class Card4_142 extends AbstractLostInterrupt {
    public Card4_142() {
        super(Side.DARK, 3, Title.Frustration, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("'Rrraaaarrr!'");
        setGameText("During your control phase, peek at opponent's hand and target one non-Interrupt card you find there that has a deploy cost < total number of Light Side Force icons on table. Opponent must deploy a card of that title by the end of your next turn, or lose a card of that title from hand (if possible).");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
                && GameConditions.hasHand(game, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Peek at opponent's hand");
            // Allow response(s)
            action.allowResponses("Peek at opponent's hand and target a non-Interrupt card",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PeekAtOpponentsHandEffect(action, playerId) {
                                        @Override
                                        protected void cardsPeekedAt(List<PhysicalCard> peekedAtCards) {
                                            final int lightForceIcons = (int) game.getModifiersQuerying().getTotalForceIconCount(game.getGameState(), game.getLightPlayer());
                                            final Filter validTargetFilter = Filters.and(
                                                    Filters.not(Filters.Interrupt),
                                                    frustrationDeployCostLessThan(lightForceIcons));
                                            Collection<PhysicalCard> validTargets = Filters.filter(peekedAtCards, game, validTargetFilter);
                                            if (validTargets.isEmpty()) {
                                                return;
                                            }

                                            action.appendEffect(
                                                    new ChooseArbitraryCardsEffect(action, playerId, "Target a non-Interrupt card with deploy cost < " + lightForceIcons + " Light Side Force icons",
                                                            validTargets, Filters.any, 1, 1, false) {
                                                        @Override
                                                        protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                                            final PhysicalCard targetedCard = selectedCards.iterator().next();
                                                            if (targetedCard == null) {
                                                                return;
                                                            }
                                                            final String targetedTitle = targetedCard.getTitle();
                                                            final int permCardId = self.getPermanentCardId();
                                                            // Capture this play's card id so overlapping Frustration plays stay distinct
                                                            // even if the unique copy is retrieved and played again.
                                                            final int playCardId = self.getCardId();
                                                            final int nextTurnNumber = game.getGameState().getPlayersLatestTurnNumber(playerId) + 1;
                                                            // Track the obligation on the proxy itself. Frustration is a Lost Interrupt, so
                                                            // ForRemainderOfGameData on the card is not reliable after it hits the Lost Pile.
                                                            // Each play gets its own proxy + stillPending flag so a later play cannot replace this one.
                                                            // AtomicBoolean so the anonymous proxy can mutate a final captured flag.
                                                            final AtomicBoolean stillPending = new AtomicBoolean(true);
                                                            game.getGameState().sendMessage(playerId + " targets " + GameUtils.getCardLink(targetedCard)
                                                                    + ". " + opponent + " must deploy a card of that title by the end of " + playerId + "'s next turn, or lose a card of that title from hand (if possible)");

                                                            // Until-end-of-game proxy so overlapping plays are not collapsed when the
                                                            // unique copy is replayed, and so "your next turn" is per-instance.
                                                            action.appendEffect(
                                                                    new AddUntilEndOfGameActionProxyEffect(action, new AbstractActionProxy() {
                                                                        @Override
                                                                        public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                                                            List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                                                            if (!stillPending.get()) {
                                                                                return actions;
                                                                            }

                                                                            // Opponent deployed a card of the targeted title, which satisfies this Frustration play
                                                                            if (TriggerConditions.justDeployed(game, effectResult, opponent, Filters.sameTitleAs(targetedCard, true))) {
                                                                                stillPending.set(false);
                                                                                return actions;
                                                                            }

                                                                            // At the end of that next turn, lose a card of the title from hand if possible
                                                                            if (TriggerConditions.isEndOfYourTurn(game, effectResult, playerId)
                                                                                    && game.getGameState().getPlayersLatestTurnNumber(playerId) == nextTurnNumber) {
                                                                                stillPending.set(false);
                                                                                final PhysicalCard source = game.findCardByPermanentId(permCardId);
                                                                                Collection<PhysicalCard> inHand = Filters.filter(game.getGameState().getHand(opponent), game, Filters.sameTitleAs(targetedCard, true));
                                                                                if (!inHand.isEmpty()) {
                                                                                    // Rule trigger with a per-play id so retrieving/replaying unique Frustration
                                                                                    // cannot swallow an earlier play's obligation.
                                                                                    RequiredRuleTriggerAction action1 = new RequiredRuleTriggerAction(new Rule() {}, source) {
                                                                                        @Override
                                                                                        public String getTriggerIdentifier(boolean useBlueprintId) {
                                                                                            return "FrustrationObligation|" + targetedTitle + "|" + nextTurnNumber + "|" + playCardId;
                                                                                        }
                                                                                    };
                                                                                    action1.setText("Make " + opponent + " lose a " + targetedTitle + " from hand");
                                                                                    action1.setPerformingPlayer(opponent);
                                                                                    action1.appendEffect(
                                                                                            new ChooseAndLoseCardFromHandEffect(action1, opponent, Filters.sameTitleAs(targetedCard, true)));
                                                                                    actions.add(action1);
                                                                                }
                                                                            }
                                                                            return actions;
                                                                        }
                                                                    })
                                                            );
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }


    /**
     * Frustration AR: target a card and any one of its deploy costs. "Free" is not a deploy cost.
     * Ignore modifiers except global DEPLOY_COST changes (Bad Feeling Have I, Max Rebo).
     * Uses the lowest remaining printed cost. Undefined / no printed cost cannot be targeted.
     */
    private Filter frustrationDeployCostLessThan(final float lightForceIcons) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                Float cost = getFrustrationDeployCost(gameState, modifiersQuerying, physicalCard);
                return cost != null && cost < lightForceIcons;
            }
        };
    }

    /**
     * Printed deploy costs of this card, then global DEPLOY_COST modifiers.
     * Does not use getDeployCost(), which collapses an undefined cost to 0.
     */
    private Float getFrustrationDeployCost(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        Float printed = card.getBlueprint().getDeployCost();

        // Locations always report 0 from AbstractLocation.getDeployCost(). That is not a printed cost
        // (Mos Eisley, etc.). Real location costs come from this card's PRINTED_DEPLOY_COST modifiers.
        if (card.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
            printed = null;
        }

        // Admiral's Orders, Defensive Shields, and Objectives always play for free
        // (constructor 0/null plus isCardTypeAlwaysPlayedForFree). Free is not a deploy cost.
        CardCategory category = card.getBlueprint().getCardCategory();
        if (category == CardCategory.ADMIRALS_ORDER
                || category == CardCategory.DEFENSIVE_SHIELD
                || category == CardCategory.OBJECTIVE) {
            printed = null;
        }

        // GEMP encodes some "deploys free" cards as constructor deploy 0 plus DeploysFreeModifier
        // (Princess Leia Organa JP). Free is not a deploy cost, so drop that 0.
        // Any currently affecting DEPLOYS_FREE counts: own-text (Savrip with C-3PO) or external
        // (Corellian Engineering Corporation on Quad Laser Cannons).
        boolean deploysFree = false;
        for (Modifier modifier : modifiersQuerying.getModifiersAffectingCard(gameState, ModifierType.DEPLOYS_FREE, card)) {
            deploysFree = true;
            break;
        }
        // Keep PRINTED_DEPLOY_COST_TO_TARGET numeric options (Ponda/Lando).
        if (deploysFree) {
            printed = null;
        }

        Float printedDeployCost = null;
        for (Modifier modifier : modifiersQuerying.getModifiersAffectingCard(gameState, ModifierType.PRINTED_DEPLOY_COST, card)) {
            if (!isModifierFromThisCard(gameState, modifier, card)) {
                continue;
            }
            float value = modifier.getPrintedValueDefinedByGameText(gameState, modifiersQuerying, card);
            printedDeployCost = printedDeployCost == null ? value : Math.min(printedDeployCost, value);
        }

        // Split printed costs such as Luke's Hunting Rifle (1 or 3) and Ponda's blaster (free or 2).
        // Take the numeric values even with no matching target in play; free-to-target is not a number.
        // Formula X (Bowcaster, Jedi Lightsaber) is undefined until deploy: skip that evaluator and
        // also skip this card's paired PRINTED_DEPLOY_COST (GEMP's fake constant for the same X).
        Float printedDeployCostToTarget = null;
        boolean hasVariableCostToTarget = false;
        for (Modifier modifier : modifiersQuerying.getModifiersAffectingCard(gameState, ModifierType.PRINTED_DEPLOY_COST_TO_TARGET, card)) {
            if (!isModifierFromThisCard(gameState, modifier, card)) {
                continue;
            }
            if (modifier instanceof DefinedByGameTextDeployCostToTargetModifier
                    && ((DefinedByGameTextDeployCostToTargetModifier) modifier).isVariableCostDefinedByGameText()) {
                hasVariableCostToTarget = true;
                continue;
            }
            float value = modifier.getDefinedDeployCostToTarget(gameState, modifiersQuerying, card);
            printedDeployCostToTarget = printedDeployCostToTarget == null ? value : Math.min(printedDeployCostToTarget, value);
        }

        if (!deploysFree && !hasVariableCostToTarget && printedDeployCost != null) {
            printed = printed == null ? printedDeployCost : Math.min(printed, printedDeployCost);
        }
        if (printedDeployCostToTarget != null) {
            printed = printed == null ? printedDeployCostToTarget : Math.min(printed, printedDeployCostToTarget);
        }

        if (printed == null) {
            return null;
        }

        for (Modifier modifier : modifiersQuerying.getModifiersAffectingCard(gameState, ModifierType.DEPLOY_COST, card)) {
            printed += modifier.getDeployCostModifier(gameState, modifiersQuerying, card);
        }

        return Math.max(0, printed);
    }

    private boolean isModifierFromThisCard(GameState gameState, Modifier modifier, PhysicalCard card) {
        PhysicalCard source = modifier.getSource(gameState);
        return source != null && source.getCardId() == card.getCardId();
    }
}
