package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.cards.effects.MoveCardsDuringShipdockEffect;
import com.gempukku.swccgo.cards.effects.PayShipdockingCostEffect;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Effect;

/**
 * An action to ship-dock starships.
 */
public class ShipdockingAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _starship1;
    private boolean _forFree;
    private PhysicalCard _starship2;
    private boolean _useForceCostApplied;
    private Effect _moveCardsEffect;
    private boolean _shipdockStarted;
    private boolean _shipdockCompleted;
    private ShipdockingAction _that;

    /**
     * Creates an action to ship-dock starships.
     * @param playerId the player
     * @param starship a starship to ship-dock
     * @param forFree true if moving for free, otherwise false
     * @param shipdockWithFilter the filter for starship to ship-dock with
     */
    public ShipdockingAction(final String playerId, final PhysicalCard starship, boolean forFree, final Filter shipdockWithFilter) {
        super(starship, playerId);
        _starship1 = starship;
        _forFree = forFree;
        _that = this;

        appendTargeting(
                new ChooseCardOnTableEffect(_that, playerId, "Choose starship to ship-dock " + GameUtils.getCardLink(starship) + " with", shipdockWithFilter) {
                    @Override
                    protected void cardSelected(PhysicalCard selectedCard) {
                        _starship2 = selectedCard;
                        _moveCardsEffect = new MoveCardsDuringShipdockEffect(_that, _starship1, _starship2);
                    }
                }
        );
    }

    @Override
    public String getText() {
        return "Ship-dock";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        // Verify no costs have failed
        if (!isAnyCostFailed()) {

            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_useForceCostApplied) {
                _useForceCostApplied = true;
                if (!_forFree) {
                    appendCost(new PayShipdockingCostEffect(_that, getPerformingPlayer(), _starship1, _starship2, 0));
                    return getNextCost();
                }
            }

            if (!_shipdockStarted) {
                _shipdockStarted = true;
                game.getGameState().activatedCard(getPerformingPlayer(), _starship1);
                game.getGameState().cardAffectsCard(getPerformingPlayer(), _starship1, _starship2);
                _starship1.shipdockedWith(_starship2);
                _starship2.shipdockedWith(_starship1);
                game.getGameState().sendMessage(_starship1.getOwner() + " ship-docks " + GameUtils.getCardLink(_starship1) + " and " + GameUtils.getCardLink(_starship2));
                return _moveCardsEffect;
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        if (_shipdockStarted && !_shipdockCompleted) {
            _shipdockCompleted = true;
            _starship1.shipdockedWith(null);
            _starship2.shipdockedWith(null);
            game.getGameState().sendMessage(_starship1.getOwner() + " undocks " + GameUtils.getCardLink(_starship1) + " and " + GameUtils.getCardLink(_starship2));
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _shipdockStarted && _moveCardsEffect.wasCarriedOut();
    }
}
