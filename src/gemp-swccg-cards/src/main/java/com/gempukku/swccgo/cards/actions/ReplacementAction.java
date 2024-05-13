package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.ReplacementEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * An action to replace cards with another card.
 */
public class ReplacementAction extends AbstractTopLevelRuleAction {
    private ReplacementAction _that;
    private PhysicalCard _card;
    private PhysicalCard _location;
    private Collection<PhysicalCard> _cardsToReplace;
    private boolean _replacementComplete;
    private ReplacementEffect _ReplacementEffect;

    /**
     * An action to replace cards with another card.
     * @param card the card
     */
    public ReplacementAction(final PhysicalCard card) {
        super(card, card.getOwner());
        _card = card;
        _text = "Replace cards";
        _that = this;

        appendTargeting(
                new PassthruEffect(_that) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {
                        final Filter replacementFilter = Filters.and(Filters.your(card), Filters.or(Filters.starfighter, Filters.character), card.getBlueprint().getReplacementFilterForSquadron());
                        final Integer replacementCount = card.getBlueprint().getReplacementCountForSquadron();

                        // Find locations that have the required number of starfighters present
                        List<PhysicalCard> validLocations = new ArrayList<PhysicalCard>();
                        Collection<PhysicalCard> locations = Filters.filterTopLocationsOnTable(game, Filters.wherePresent(card, replacementFilter));
                        for (PhysicalCard location : locations) {
                            if (Filters.canSpot(game, card, replacementCount, Filters.and(replacementFilter, Filters.present(location)))) {
                                validLocations.add(location);
                            }
                        }
                        appendTargeting(
                                new ChooseCardOnTableEffect(_that, getPerformingPlayer(), "Choose location with cards to replace", Filters.in(validLocations)) {
                                    @Override
                                    protected void cardSelected(PhysicalCard location) {
                                        _location = location;
                                        Filter starfighterFilter = Filters.and(replacementFilter, Filters.present(location));
                                        appendTargeting(
                                                new TargetCardsOnTableEffect(_that, getPerformingPlayer(), "Choose cards to replace", replacementCount, replacementCount, starfighterFilter) {
                                                    @Override
                                                    protected void cardsTargeted(int targetGroupId, Collection<PhysicalCard> starfightersToReplace) {
                                                        _cardsToReplace = new ArrayList<PhysicalCard>(starfightersToReplace);
                                                    }
                                                }
                                        );
                                    }
                                }
                        );
                    }
                }
        );
    }

    @Override
    public String getText() {
        return _text;
    }

    /**
     * Sets the text shown for the action selection on the User Interface
     * @param text the text to show for the action selection
     */
    @Override
    public void setText(String text) {
        _text = text;
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        // Verify no costs have failed
        if (!isAnyCostFailed()) {

            // Perform any costs in the queue
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            // Persona replace the character
            if (!_replacementComplete) {
                _replacementComplete = true;

                _ReplacementEffect = new ReplacementEffect(_that, _location, _cardsToReplace, _card);
                return _ReplacementEffect;
            }
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _replacementComplete && _ReplacementEffect.wasCarriedOut();
    }
}
