package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.SquadronReplacementEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * An action to replace starfighters with a squadron.
 */
public class SquadronReplacementAction extends AbstractTopLevelRuleAction {
    private SquadronReplacementAction _that;
    private PhysicalCard _squadron;
    private PhysicalCard _location;
    private Collection<PhysicalCard> _starfightersToReplace;
    private boolean _replacementComplete;
    private SquadronReplacementEffect _squadronReplacementEffect;

    /**
     * Creates an action to persona replace a character with another character of the same persona.
     * @param squadron the squadron
     */
    public SquadronReplacementAction(final PhysicalCard squadron) {
        super(squadron, squadron.getOwner());
        _squadron = squadron;
        _text = "Replace starfighters";
        _that = this;

        appendTargeting(
                new PassthruEffect(_that) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {
                        final Filter replacementFilter = Filters.and(Filters.your(squadron), Filters.starfighter, squadron.getBlueprint().getReplacementFilterForSquadron());
                        final Integer replacementCount = squadron.getBlueprint().getReplacementCountForSquadron();

                        // Find locations that have the required number of starfighters present
                        List<PhysicalCard> validLocations = new ArrayList<PhysicalCard>();
                        Collection<PhysicalCard> locations = Filters.filterTopLocationsOnTable(game, Filters.wherePresent(squadron, replacementFilter));
                        for (PhysicalCard location : locations) {
                            if (Filters.canSpot(game, squadron, replacementCount, Filters.and(replacementFilter, Filters.present(location)))) {
                                validLocations.add(location);
                            }
                        }
                        appendTargeting(
                                new ChooseCardOnTableEffect(_that, getPerformingPlayer(), "Choose location with starfighters to replace", Filters.in(validLocations)) {
                                    @Override
                                    protected void cardSelected(PhysicalCard location) {
                                        _location = location;
                                        Filter starfighterFilter = Filters.and(replacementFilter, Filters.present(location));
                                        appendTargeting(
                                                new TargetCardsOnTableEffect(_that, getPerformingPlayer(), "Choose starfighters to replace", replacementCount, replacementCount, starfighterFilter) {
                                                    @Override
                                                    protected void cardsTargeted(int targetGroupId, Collection<PhysicalCard> starfightersToReplace) {
                                                        _starfightersToReplace = new ArrayList<PhysicalCard>(starfightersToReplace);
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

                _squadronReplacementEffect = new SquadronReplacementEffect(_that, _location, _starfightersToReplace, _squadron);
                return _squadronReplacementEffect;
            }
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _replacementComplete && _squadronReplacementEffect.wasCarriedOut();
    }
}
