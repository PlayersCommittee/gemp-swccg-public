package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.ExcludedFromBattleModifier;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToBeExcludedFromBattleResult;
import com.gempukku.swccgo.logic.timing.results.ExcludedFromBattleResult;

import java.util.*;

/**
 * An effect that causes a card to be excluded from battle.
 */
public class ExcludeFromBattleEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private SwccgBuiltInCardBlueprint _excludedByPermanentWeapon;
    private PhysicalCard _cardFiringWeapon;
    private Map<PhysicalCard, List<PhysicalCard>> _excludedByCardMap = new HashMap<PhysicalCard, List<PhysicalCard>>();
    private List<PhysicalCard> _excludedByRule = new ArrayList<PhysicalCard>();
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private ExcludeFromBattleEffect _that;

    /**
     * Creates an effect that causes a card to be excluded from battle.
     * @param action the action performing this effect
     * @param cardToExclude the card to exclude from battle
     */
    public ExcludeFromBattleEffect(Action action, PhysicalCard cardToExclude) {
        this(action, Collections.singleton(cardToExclude), null, null);
    }

    /**
     * Creates an effect that causes cards to be excluded from battle.
     * @param action the action performing this effect
     * @param cardsToExclude the cards exclude from battle
     */
    public ExcludeFromBattleEffect(Action action, Collection<PhysicalCard> cardsToExclude) {
        this(action, cardsToExclude, null, null);
    }

    /**
     * Creates an effect that causes cards to be excluded from battle.
     * @param action the action performing this effect
     * @param excludedByCardMap the map of cards excluded to the card that caused the exclusion
     */
    public ExcludeFromBattleEffect(Action action, Map<PhysicalCard, PhysicalCard> excludedByCardMap) {
        super(action);
        for (PhysicalCard card : excludedByCardMap.keySet()) {
            PhysicalCard cardCausingExclusion = excludedByCardMap.get(card);
            List<PhysicalCard> cardsToExclude = _excludedByCardMap.get(cardCausingExclusion);
            if (cardsToExclude == null) {
                cardsToExclude = new ArrayList<PhysicalCard>();
                _excludedByCardMap.put(cardCausingExclusion, cardsToExclude);
            }
            cardsToExclude.add(card);
        }
        _that = this;
    }

    /**
     * Creates an effect that causes a card to be excluded from battle.
     * @param action the action performing this effect
     * @param cardToExclude the card to exclude from battle
     * @param excludedByPermanentWeapon the permanent weapon built-in causing the exclude, or null
     * @param cardFiringWeapon the card firing the weapon the exclude, or null
     */
    public ExcludeFromBattleEffect(Action action, PhysicalCard cardToExclude, SwccgBuiltInCardBlueprint excludedByPermanentWeapon, PhysicalCard cardFiringWeapon) {
        this(action, Collections.singleton(cardToExclude), excludedByPermanentWeapon, cardFiringWeapon);
    }

    /**
     * Creates an effect that causes cards to be excluded from battle.
     * @param action the action performing this effect
     * @param cardsToExclude the cards exclude from battle
     * @param excludedByPermanentWeapon the permanent weapon built-in causing the exclude, or null
     * @param cardFiringWeapon the card firing the weapon the exclude, or null
     */
    public ExcludeFromBattleEffect(Action action, Collection<PhysicalCard> cardsToExclude, SwccgBuiltInCardBlueprint excludedByPermanentWeapon, PhysicalCard cardFiringWeapon) {
        super(action);
        _excludedByPermanentWeapon = excludedByPermanentWeapon;
        _cardFiringWeapon = cardFiringWeapon;
        PhysicalCard source = _action.getActionSource() != null ? _action.getActionSource() : _action.getActionAttachedToCard();
        if (source != null) {
            _excludedByCardMap.put(source, new ArrayList<PhysicalCard>(cardsToExclude));
        }
        else {
            _excludedByRule.addAll(cardsToExclude);
        }
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action);

        final GameState gameState = game.getGameState();
        final BattleState battleState = gameState.getBattleState();
        if (battleState != null) {

            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            List<PhysicalCard> allCardsToExclude = new ArrayList<PhysicalCard>();
                            if (!_excludedByCardMap.isEmpty()) {
                                for (List<PhysicalCard> cardsToExclude : _excludedByCardMap.values()) {
                                    allCardsToExclude.addAll(cardsToExclude);
                                }
                            } else {
                                allCardsToExclude.addAll(_excludedByRule);
                            }

                            Collection<PhysicalCard> cardsToAttemptToExclude = Filters.filter(allCardsToExclude, game, Filters.participatingInBattle);
                            if (!cardsToAttemptToExclude.isEmpty()) {

                                // If not excluded by rule (i.e. there is an action source), then check if cards are prevented from being excluded
                                if (!_excludedByCardMap.isEmpty()) {
                                    Collection<PhysicalCard> cardsThatMayNotBeExcluded = Filters.filter(cardsToAttemptToExclude, game, Filters.mayNotBeExcludedFromBattle);
                                    if (!cardsThatMayNotBeExcluded.isEmpty()) {
                                        gameState.sendMessage(GameUtils.getAppendedNames(cardsThatMayNotBeExcluded) + " may not be excluded from battle");
                                    }

                                    final Collection<PhysicalCard> cardsToAttemptToExcludeByCard = Filters.filter(cardsToAttemptToExclude, game, Filters.not(Filters.in(cardsThatMayNotBeExcluded)));
                                    if (!cardsToAttemptToExcludeByCard.isEmpty()) {

                                        // Trigger is "about to be excluded from battle" for cards specified cards to be excluded from battle.
                                        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being excluded from battle.
                                        List<EffectResult> effectResults = new ArrayList<EffectResult>();
                                        for (PhysicalCard sourceCard : _excludedByCardMap.keySet()) {
                                            Collection<PhysicalCard> cardsToExcludeBySource = Filters.filter(_excludedByCardMap.get(sourceCard), game, Filters.in(cardsToAttemptToExclude));
                                            for (PhysicalCard cardToExcludeBySource : cardsToExcludeBySource) {
                                                effectResults.add(new AboutToBeExcludedFromBattleResult(subAction, cardToExcludeBySource, sourceCard, _excludedByPermanentWeapon, _cardFiringWeapon, _that));
                                            }
                                        }
                                        subAction.appendEffect(new TriggeringResultsEffect(subAction, effectResults));

                                        subAction.appendEffect(
                                                new PassthruEffect(subAction) {
                                                    @Override
                                                    protected void doPlayEffect(SwccgGame game) {
                                                        boolean anyCardsExcluded = false;
                                                        String playerCausingExclusion = _action.getPerformingPlayer();
                                                        for (PhysicalCard sourceCard : _excludedByCardMap.keySet()) {
                                                            Collection<PhysicalCard> cardsToExcludeBySource = Filters.filter(_excludedByCardMap.get(sourceCard), game, Filters.and(Filters.in(cardsToAttemptToExcludeByCard), Filters.not(Filters.in(_preventedCards))));
                                                            _excludedByCardMap.put(sourceCard, new ArrayList<PhysicalCard>(cardsToExcludeBySource));
                                                            if (!cardsToExcludeBySource.isEmpty()) {
                                                                anyCardsExcluded = true;
                                                                if (playerCausingExclusion == null) {
                                                                    playerCausingExclusion = sourceCard.getOwner();
                                                                }
                                                                gameState.cardAffectsCards(playerCausingExclusion, sourceCard, cardsToExcludeBySource);
                                                                gameState.sendMessage(playerCausingExclusion + " causes " + GameUtils.getAppendedNames(cardsToExcludeBySource) + " to be excluded from battle using " + GameUtils.getCardLink(sourceCard));
                                                                game.getModifiersEnvironment().addUntilEndOfBattleModifier(new ExcludedFromBattleModifier(sourceCard, Filters.in(cardsToExcludeBySource)));
                                                            }
                                                        }
                                                        if (anyCardsExcluded) {
                                                            game.getActionsEnvironment().emitEffectResult(new ExcludedFromBattleResult(playerCausingExclusion, _excludedByCardMap, _excludedByPermanentWeapon, _cardFiringWeapon));
                                                        }
                                                    }
                                                });

                                    }
                                } else {
                                    gameState.sendMessage(GameUtils.getAppendedNames(cardsToAttemptToExclude) + " " + GameUtils.be(cardsToAttemptToExclude) + " excluded from battle");
                                    game.getModifiersEnvironment().addUntilEndOfBattleModifier(new ExcludedFromBattleModifier(null, Filters.in(cardsToAttemptToExclude)));
                                    game.getActionsEnvironment().emitEffectResult(new ExcludedFromBattleResult(null, cardsToAttemptToExclude));
                                }
                            }

                            battleState.updateParticipants(game);
                        }
                    }
            );

        }

        return subAction;
    }


    @Override
    protected boolean wasActionCarriedOut() {
        return _preventedCards.isEmpty();
    }

    /**
     * Prevents the specified card from being affected by the effect.
     * @param card the card
     */
    @Override
    public void preventEffectOnCard(PhysicalCard card) {
        _preventedCards.add(card);
    }

    /**
     * Determines if the specified card was prevented from being affected by the effect.
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean isEffectOnCardPrevented(PhysicalCard card) {
        return _preventedCards.contains(card);
    }
}
