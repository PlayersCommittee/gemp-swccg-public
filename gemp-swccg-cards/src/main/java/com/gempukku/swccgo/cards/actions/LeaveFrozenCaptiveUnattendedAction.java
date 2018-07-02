package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.LeaveFrozenCaptiveUnattendedEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;

/**
 * The action to leave a 'frozen' captive as 'unattended'.
 */
public class LeaveFrozenCaptiveUnattendedAction extends AbstractTopLevelRuleAction {
    private boolean _choseCaptive;
    private Effect _chooseCaptiveEffect;
    private PhysicalCard _captive;
    private boolean _captiveLeft;
    private Effect _leaveCaptiveEffect;
    private Action _that;

    /**
     * Creates an action to leave a 'frozen' captive as 'unattended'.
     * @param game the game
     * @param escort the escort
     */
    public LeaveFrozenCaptiveUnattendedAction(final SwccgGame game, final PhysicalCard escort) {
        super(escort, escort.getOwner());
        _that = this;

        final PhysicalCard site = game.getModifiersQuerying().getLocationThatCardIsPresentAt(game.getGameState(), escort);
        final Collection<PhysicalCard> validFrozenCaptives = Filters.filter(game.getGameState().getCaptivesOfEscort(escort),
                game, Filters.and(Filters.frozenCaptive, Filters.notPreventedFromMoving));

        if (validFrozenCaptives.size() == 1) {
            _captive = validFrozenCaptives.iterator().next();
            _choseCaptive = true;
            _leaveCaptiveEffect = new LeaveFrozenCaptiveUnattendedEffect(_that, escort, _captive, site);
        } else {
            _chooseCaptiveEffect =
                    new ChooseCardOnTableEffect(_that, getPerformingPlayer(), "Choose frozen captive to leave unattended", validFrozenCaptives) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            _captive = selectedCard;
                            _choseCaptive = true;
                            _leaveCaptiveEffect = new LeaveFrozenCaptiveUnattendedEffect(_that, escort, _captive, site);
                        }
                    };
        }
    }

    @Override
    public String getText() {
        return "Leave frozen captive unattended";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        // Verify no costs have failed
        if (!isAnyCostFailed()) {

            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_choseCaptive) {
                return _chooseCaptiveEffect;
            }

            if (!_captiveLeft) {
                _captiveLeft = true;
                return _leaveCaptiveEffect;
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _captiveLeft && _leaveCaptiveEffect.wasCarriedOut();
    }
}
