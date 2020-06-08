package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.cards.effects.PayMoveUsingHyperspeedCostEffect;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.MoveMobileSystemUsingHyperspeedEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An action to move a mobile system using hyperspeed.
 */
public class MoveMobileSystemUsingHyperspeedAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _cardToMove;
    private boolean _forFree;
    private PhysicalCard _oldOrbit;
    private int _oldParsec;
    private PlayoutDecisionEffect _chooseParsecEffect;
    private int _newParsec;
    private boolean _parsecChosen;
    private PlayoutDecisionEffect _chooseDeepSpaceOrOrbitEffect;
    private boolean _deepSpaceOrOrbitChosen;
    private StandardEffect _chooseSystemEffect;
    private PhysicalCard _systemToOrbit;
    private boolean _systemToOrbitChosen;
    private boolean _useForceCostApplied;
    private Effect _moveMobileSystemEffect;
    private boolean _mobileSystemMoved;
    private Action _that;

    /**
     * Creates an action to move a mobile system using hyperspeed.
     * @param playerId the player
     * @param game the game
     * @param card the starship to move
     * @param forFree true if moving for free, otherwise false
     */
    public MoveMobileSystemUsingHyperspeedAction(String playerId, final SwccgGame game, final PhysicalCard card, boolean forFree) {
        super(card, playerId);
        _cardToMove = card;
        _forFree = forFree;
        _that = this;

        final GameState gameState = game.getGameState();

        _oldParsec = card.getParsec();
        if (_cardToMove.getSystemOrbited() != null) {
            _oldOrbit = Filters.findFirstFromTopLocationsOnTable(game, Filters.isOrbitedBy(card));
        }

        int hyperspeed = (int) game.getModifiersQuerying().getHyperspeed(gameState, card);
        List<String> validParsecToMoveTo = new ArrayList<String>();
        for (int i = Math.max(0, _oldParsec - hyperspeed); i <= Math.min(99, _oldParsec + hyperspeed); ++i) {
            if (i != _oldParsec
                    || _oldOrbit != null
                    || Filters.canSpotFromTopLocationsOnTable(game, Filters.and(Filters.not(card), Filters.systemAtParsec(i)))) {
                validParsecToMoveTo.add(String.valueOf(i));
            }
        }
        String[] parsecs = validParsecToMoveTo.toArray(new String[validParsecToMoveTo.size()]);

        _chooseParsecEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                                    new MultipleChoiceAwaitingDecision("Choose parsec to move to ", parsecs) {
                                        @Override
                                        protected void validDecisionMade(int index, String result) {
                                            _newParsec = Integer.valueOf(result);

                                            // Check if the mobile system can orbit another system (other than one it is already orbiting) at the chosen parsec
                                            final Collection<PhysicalCard> validToOrbit = Filters.filterTopLocationsOnTable(game,
                                                    Filters.and(Filters.not(card), Filters.systemAtParsec(_newParsec), Filters.not(Filters.isOrbitedBy(card))));
                                            if (validToOrbit.isEmpty()) {
                                                _deepSpaceOrOrbitChosen = true;
                                                _systemToOrbit = null;
                                                _systemToOrbitChosen = true;

                                                _moveMobileSystemEffect = new MoveMobileSystemUsingHyperspeedEffect(_that, _cardToMove, _oldParsec, _oldOrbit, _newParsec, null);
                                            }
                                            // Check if mobile system is moving from deep space to the same parsec and only one system to orbit
                                            else if ((_oldParsec == _newParsec && _oldOrbit == null) && validToOrbit.size() == 1) {
                                                _deepSpaceOrOrbitChosen = true;
                                                _systemToOrbit = validToOrbit.iterator().next();
                                                _systemToOrbitChosen = true;

                                                _moveMobileSystemEffect = new MoveMobileSystemUsingHyperspeedEffect(_that, _cardToMove, _oldParsec, _oldOrbit, _newParsec, _systemToOrbit);
                                            }
                                            else {
                                                // Ask if mobile system should orbit a system or be in deep space
                                                String[] destinations;
                                                if (validToOrbit.size() == 1)
                                                    destinations = new String[]{"Deep Space", "Orbit " + validToOrbit.iterator().next().getBlueprint().getTitle()};
                                                else
                                                    destinations = new String[]{"Deep Space", "Orbit a system"};

                                                _chooseDeepSpaceOrOrbitEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                                                        new MultipleChoiceAwaitingDecision("Choose destination for " + GameUtils.getCardLink(_cardToMove) + " at parsec " + _newParsec, destinations) {
                                                            @Override
                                                            protected void validDecisionMade(int index, String result) {
                                                                if (index == 0) {
                                                                    _systemToOrbit = null;
                                                                    _systemToOrbitChosen = true;

                                                                    _moveMobileSystemEffect = new MoveMobileSystemUsingHyperspeedEffect(_that, _cardToMove, _oldParsec, _oldOrbit, _newParsec, null);
                                                                }
                                                                else {
                                                                    // Choose a system to orbit
                                                                    _chooseSystemEffect =
                                                                            new ChooseCardOnTableEffect(_that, getPerformingPlayer(), "Choose system for " + GameUtils.getFullName(_cardToMove) + " to orbit", validToOrbit) {
                                                                                @Override
                                                                                protected void cardSelected(PhysicalCard selectedCard) {
                                                                                    _systemToOrbit = selectedCard;

                                                                                    _moveMobileSystemEffect = new MoveMobileSystemUsingHyperspeedEffect(_that, _cardToMove, _oldParsec, _oldOrbit, _newParsec, _systemToOrbit);
                                                                                }
                                                                            };
                                                                }
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                    }
        );
    }

    @Override
    public String getText() {
        return "Move using hyperspeed";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        if (!isAnyCostFailed()) {

            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_parsecChosen) {
                _parsecChosen = true;
                appendCost(_chooseParsecEffect);
                return getNextCost();
            }

            if (!_deepSpaceOrOrbitChosen) {
                _deepSpaceOrOrbitChosen = true;
                appendCost(_chooseDeepSpaceOrOrbitEffect);
                return getNextCost();
            }

            if (!_systemToOrbitChosen) {
                _systemToOrbitChosen = true;
                appendCost(_chooseSystemEffect);
                return getNextCost();
            }

            if (!_useForceCostApplied) {
                _useForceCostApplied = true;
                if (!_forFree) {
                    appendCost(new PayMoveUsingHyperspeedCostEffect(_that, getPerformingPlayer(), _cardToMove, null, false, 0));
                    return getNextCost();
                }
            }

            if (!_mobileSystemMoved) {
                _mobileSystemMoved = true;
                return _moveMobileSystemEffect;
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _mobileSystemMoved && _moveMobileSystemEffect.wasCarriedOut();
    }
}
