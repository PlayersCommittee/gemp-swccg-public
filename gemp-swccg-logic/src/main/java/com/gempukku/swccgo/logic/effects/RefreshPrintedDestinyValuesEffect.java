package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

// This is only used during an action that may check the current destiny value of a card
// which as two destiny values for the owner to pick from (currently only R2D2 has multiple destiny values).
public abstract class RefreshPrintedDestinyValuesEffect extends AbstractSubActionEffect {
    private Collection<PhysicalCard> _remainingCards = new LinkedList<PhysicalCard>();

    public RefreshPrintedDestinyValuesEffect(Action action, PhysicalCard card) {
        this(action, Collections.singletonList(card));
    }

    public RefreshPrintedDestinyValuesEffect(Action action, Collection<? extends PhysicalCard> cards) {
        super(action);
        // Add cards with multiple destinies to the remaining list
        for (PhysicalCard card : cards) {
            if (card.getBlueprint().getDestiny() != null && !card.getBlueprint().getDestiny().equals(card.getBlueprint().getAlternateDestiny())) {
                _remainingCards.add(card);
            }
        }
    }

    @Override
    public String getText(SwccgGame game) {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    protected abstract void refreshedPrintedDestinyValues();


    private class ChooseAndRefreshNextPrintedDestinyValue extends ChooseArbitraryCardsEffect {
        private Collection<PhysicalCard> _remainingCards;
        private SubAction _subAction;
        private String _playerId;

        public ChooseAndRefreshNextPrintedDestinyValue(SubAction subAction, String playerId, Collection<PhysicalCard> remainingCards) {
            super(subAction, playerId, "Choose card to select destiny value of", remainingCards, 1, 1);
            _subAction = subAction;
            _playerId = playerId;
            _remainingCards = remainingCards;
        }

        @Override
        protected void cardsSelected(final SwccgGame game, Collection<PhysicalCard> selectedCards) {
            final PhysicalCard selectedCard = selectedCards.iterator().next();
            final SwccgCardBlueprint blueprint = selectedCard.getBlueprint();

            // Chooses which destiny value to use.
            if (game.getGameState().getForcePile(selectedCard.getOwner()).size() >= blueprint.getAlternateDestinyCost()) {
                String alternateDestinyString = GuiUtils.formatAsString(blueprint.getAlternateDestiny());
                if (blueprint.getAlternateDestinyCost() > 0) {
                    alternateDestinyString = alternateDestinyString + " (must use " + blueprint.getAlternateDestinyCost() + " force)";
                }

                _subAction.appendEffect(
                        new PlayoutDecisionEffect(_subAction, selectedCard.getOwner(),
                                new MultipleChoiceAwaitingDecision("Choose destiny value for " + GameUtils.getCardLink(selectedCard),
                                        new String[]{GuiUtils.formatAsString(blueprint.getDestiny()), alternateDestinyString}) {
                                    @Override
                                    protected void validDecisionMade(int index, String result) {
                                        float chosenDestiny;
                                        if (index == 0) {
                                            chosenDestiny = blueprint.getDestiny();
                                        } else {
                                            if (blueprint.getAlternateDestinyCost() > 0) {
                                                _subAction.appendEffect(
                                                        new UseForceEffect(_subAction, selectedCard.getOwner(), blueprint.getAlternateDestinyCost())
                                                );
                                            }
                                            chosenDestiny = blueprint.getAlternateDestiny();
                                        }

                                        if (selectedCard.getZone() != Zone.SABACC_HAND)
                                            game.getGameState().sendMessage(selectedCard.getOwner() + " chooses to use " + GuiUtils.formatAsString(chosenDestiny) + " as destiny value for " + GameUtils.getCardLink(selectedCard));
                                        selectedCard.setDestinyValueToUse(chosenDestiny);

                                        _remainingCards.remove(selectedCard);
                                        if (!_remainingCards.isEmpty()) {
                                            _subAction.appendEffect(
                                                    new ChooseAndRefreshNextPrintedDestinyValue(_subAction, _playerId, _remainingCards));
                                        } else {
                                            refreshedPrintedDestinyValues();
                                        }
                                    }
                                }
                        )
                );
            } else {
                selectedCard.setDestinyValueToUse(blueprint.getDestiny());
            }
        }
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);
        if (_remainingCards.isEmpty()) {
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            refreshedPrintedDestinyValues();
                        }
                    }
            );
        }
        else {
            subAction.appendEffect(
                    new ChooseAndRefreshNextPrintedDestinyValue(subAction, _remainingCards.iterator().next().getOwner(), _remainingCards));
        }
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
