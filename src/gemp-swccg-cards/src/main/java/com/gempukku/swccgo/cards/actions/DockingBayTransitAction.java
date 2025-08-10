package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.cards.effects.PayDockingBayTransitCostEffect;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.DockingBayTransitEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * An action to perform a movement using docking bay transit.
 */
public class DockingBayTransitAction extends AbstractTopLevelRuleAction {
    private String _playerId;
    private PhysicalCard _fromDockingBay;
    private boolean _destinationChosen;
    private TargetingEffect _chooseDestinationEffect;
    private PhysicalCard _destination;
    private boolean _cardsToTransitChosen;
    private TargetingEffect _chooseCardsToTransitEffect;
    private Collection<PhysicalCard> _cardsToTransit;
    private boolean _useForceCostApplied;
    private boolean _cardsMoved;
    private Effect _moveCardsEffect;
    private boolean _forFree;
    private Action _that;

    /**
     * Creates an action to perform a movement using docking bay transit.
     * @param playerId the player
     * @param game the game
     * @param location the location
     * @param forFree true if moving for free, otherwise false
     */
    public DockingBayTransitAction(String playerId, SwccgGame game, PhysicalCard location, boolean forFree) {
        super(location, playerId);
        _playerId = playerId;
        _fromDockingBay = location;
        _forFree = forFree;
        _that = this;

        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Get cards at docking bay
        Filter cardFilter = Filters.and(Filters.your(playerId), Filters.hasNotPerformedRegularMove, Filters.or(Filters.character, Filters.vehicle, Filters.weapon), Filters.atLocation(location));
        if (gameState.getCurrentPlayerId().equals(playerId)) {
            cardFilter = Filters.and(cardFilter, Filters.not(Filters.or(Filters.undercover_spy, Filters.deploysAndMovesLikeUndercoverSpy)));
        }
        else {
            cardFilter = Filters.and(cardFilter, Filters.or(Filters.undercover_spy, Filters.deploysAndMovesLikeUndercoverSpy));
        }
        final Collection<PhysicalCard> cardsAtDockingBay = Filters.filterActive(game, null, SpotOverride.INCLUDE_UNDERCOVER, cardFilter);

        // Get other docking bays
        Collection<PhysicalCard> otherDockingBays = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.other(location), Filters.docking_bay));
        List<PhysicalCard> validDockingBays = new ArrayList<PhysicalCard>();

        // Figure out which docking bays any of the cards can transit to
        for (PhysicalCard otherDockingBay : otherDockingBays) {
            for (PhysicalCard cardAtDockingBay : cardsAtDockingBay) {
                // Check if card can move to destination card
                if (Filters.canMoveToUsingDockingBayTransit(cardAtDockingBay, false, 0).accepts(gameState, modifiersQuerying, otherDockingBay)) {
                    validDockingBays.add(otherDockingBay);
                    break;
                }
            }
        }

        // Choose destination
        _chooseDestinationEffect = new ChooseCardOnTableEffect(_that, _playerId, "Choose docking bay to transit to", validDockingBays) {
                  @Override
                  protected void cardSelected(PhysicalCard toCard) {
                      _destinationChosen = true;
                      _destination = toCard;

                      List<PhysicalCard> validToTransit = new ArrayList<PhysicalCard>();
                      for (PhysicalCard cardAtDockingBay : cardsAtDockingBay) {
                          // Check if card can move to destination card
                          if (Filters.canMoveToUsingDockingBayTransit(cardAtDockingBay, _forFree, 0).accepts(gameState, modifiersQuerying, _destination)) {
                              validToTransit.add(cardAtDockingBay);
                          }
                      }

                      // Choose cards to transit
                      _chooseCardsToTransitEffect = new ChooseCardsOnTableEffect(_that, _playerId, "Choose cards to docking bay transit to " + GameUtils.getCardLink(_destination), 1, Integer.MAX_VALUE, validToTransit) {
                          @Override
                          protected void cardsSelected(Collection<PhysicalCard> cards) {
                              _cardsToTransitChosen = true;
                              _cardsToTransit = cards;

                              // Transit cards
                              _moveCardsEffect = new DockingBayTransitEffect(_that, _cardsToTransit, _fromDockingBay, _destination);
                          }
                      };
                  }
              };
    }

    @Override
    public String getText() {
        return "Docking bay transit";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        if (!isAnyCostFailed()) {

            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_destinationChosen) {
                _destinationChosen = true;
                appendTargeting(_chooseDestinationEffect);
                return getNextCost();
            }

            if (!_cardsToTransitChosen) {
                _cardsToTransitChosen = true;
                appendTargeting(_chooseCardsToTransitEffect);
                return getNextCost();
            }

            if (!_useForceCostApplied) {
                _useForceCostApplied = true;
                if (!_forFree) {
                    appendCost(new PayDockingBayTransitCostEffect(this, getPerformingPlayer(), _cardsToTransit, _fromDockingBay, _destination, 0));
                    return getNextCost();
                }
            }

            if (!_cardsMoved) {
                _cardsMoved = true;
                return _moveCardsEffect;
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _cardsMoved;
    }
}

