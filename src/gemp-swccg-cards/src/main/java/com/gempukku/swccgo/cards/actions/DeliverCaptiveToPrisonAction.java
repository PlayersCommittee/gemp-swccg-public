package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.DeliverCaptiveToPrisonEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;

/**
 * The action to deliver an escorted captive to a prison.
 */
public class DeliverCaptiveToPrisonAction extends AbstractTopLevelRuleAction {
    private boolean _choseCaptive;
    private Effect _chooseCaptiveEffect;
    private PhysicalCard _captive;
    private boolean _captiveDelivered;
    private Effect _deliverCaptiveEffect;
    private Action _that;

    /**
     * Creates an action to deliver a captive of the specified escort to the prison the escort is present at.
     * @param game the game
     * @param escort the escort
     */
    public DeliverCaptiveToPrisonAction(final SwccgGame game, final PhysicalCard escort) {
        super(escort, escort.getOwner());
        _that = this;

        final PhysicalCard prison = game.getModifiersQuerying().getLocationThatCardIsPresentAt(game.getGameState(), escort);
        final Collection<PhysicalCard> validCaptives = Filters.filter(game.getGameState().getCaptivesOfEscort(escort), game, Filters.notPreventedFromMoving);

        if (validCaptives.size() == 1) {
            _captive = validCaptives.iterator().next();
            _choseCaptive = true;
            _deliverCaptiveEffect = new DeliverCaptiveToPrisonEffect(_that, escort, _captive, prison);
        } else {
            _chooseCaptiveEffect =
                    new ChooseCardOnTableEffect(_that, getPerformingPlayer(), "Choose captive to deliver to " + GameUtils.getCardLink(prison), validCaptives) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            _captive = selectedCard;
                            _choseCaptive = true;
                            _deliverCaptiveEffect = new DeliverCaptiveToPrisonEffect(_that, escort, _captive, prison);
                        }
                    };
        }
    }

    @Override
    public String getText() {
        return "Deliver captive to prison";
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

            if (!_captiveDelivered) {
                _captiveDelivered = true;
                return _deliverCaptiveEffect;
            }
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _captiveDelivered && _deliverCaptiveEffect.wasCarriedOut();
    }
}
