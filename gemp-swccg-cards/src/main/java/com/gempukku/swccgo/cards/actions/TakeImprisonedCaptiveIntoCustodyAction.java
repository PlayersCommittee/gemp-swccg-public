package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.TakeImprisonedCaptiveCustodyEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;

/**
 * The action to take an imprisoned captive into custody.
 */
public class TakeImprisonedCaptiveIntoCustodyAction extends AbstractTopLevelRuleAction {
    private boolean _choseCaptive;
    private Effect _chooseCaptiveEffect;
    private PhysicalCard _captive;
    private boolean _captiveTaken;
    private Effect _takeCaptiveIntoCustodyEffect;
    private Action _that;

    /**
     * Creates an action to have the specified escort take an imprisoned captive into custody from the prison the escort
     * is present at.
     * @param game the game
     * @param escort the escort
     */
    public TakeImprisonedCaptiveIntoCustodyAction(final SwccgGame game, final PhysicalCard escort) {
        super(escort, escort.getOwner());
        _that = this;

        final PhysicalCard prison = game.getModifiersQuerying().getLocationThatCardIsPresentAt(game.getGameState(), escort);
        final Collection<PhysicalCard> validCaptives = Filters.filter(game.getGameState().getCaptivesInPrison(prison), game,
                Filters.and(Filters.notPreventedFromMoving, Filters.canBeEscortedBy(escort)));

        if (validCaptives.size() == 1) {
            _captive = validCaptives.iterator().next();
            _choseCaptive = true;
            _takeCaptiveIntoCustodyEffect = new TakeImprisonedCaptiveCustodyEffect(_that, escort, _captive, prison);
        } else {
            _chooseCaptiveEffect =
                    new ChooseCardOnTableEffect(_that, getPerformingPlayer(), "Choose imprisoned captive to take into custody", validCaptives) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            _captive = selectedCard;
                            _choseCaptive = true;
                            _takeCaptiveIntoCustodyEffect = new TakeImprisonedCaptiveCustodyEffect(_that, escort, _captive, prison);
                        }
                    };
        }
    }

    @Override
    public String getText() {
        return "Take imprisoned captive into custody";
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

            if (!_captiveTaken) {
                _captiveTaken = true;
                return _takeCaptiveIntoCustodyEffect;
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _captiveTaken && _takeCaptiveIntoCustodyEffect.wasCarriedOut();
    }
}
