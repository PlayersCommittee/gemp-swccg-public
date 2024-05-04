package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.TakeUnattendedFrozenCaptiveIntoCustodyEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;

/**
 * The action to take an 'unattended frozen' captive into custody.
 */
public class TakeUnattendedFrozenCaptiveIntoCustodyAction extends AbstractTopLevelRuleAction {
    private boolean _choseCaptive;
    private Effect _chooseCaptiveEffect;
    private PhysicalCard _captive;
    private boolean _captiveTaken;
    private Effect _takeCaptiveIntoCustodyEffect;
    private Action _that;

    /**
     * Creates an action to have the specified escort take an 'unattended frozen' captive into custody from the site the
     * escort is present at.
     * @param game the game
     * @param escort the escort
     */
    public TakeUnattendedFrozenCaptiveIntoCustodyAction(final SwccgGame game, final PhysicalCard escort) {
        super(escort, escort.getOwner());
        _that = this;

        final PhysicalCard site = game.getModifiersQuerying().getLocationThatCardIsPresentAt(game.getGameState(), escort);
        final Collection<PhysicalCard> validFrozenCaptives = Filters.filterActive(game, null, SpotOverride.INCLUDE_CAPTIVE,
                Filters.and(Filters.unattendedFrozenCaptive, Filters.notPreventedFromMoving, Filters.atLocation(site), Filters.canBeEscortedBy(escort)));

        if (validFrozenCaptives.size() == 1) {
            _captive = validFrozenCaptives.iterator().next();
            _choseCaptive = true;
            _takeCaptiveIntoCustodyEffect = new TakeUnattendedFrozenCaptiveIntoCustodyEffect(_that, escort, _captive, false);
        } else {
            _chooseCaptiveEffect =
                    new ChooseCardOnTableEffect(_that, getPerformingPlayer(), "Choose unattended frozen captive to take into custody", validFrozenCaptives) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            _captive = selectedCard;
                            _choseCaptive = true;
                            _takeCaptiveIntoCustodyEffect = new TakeUnattendedFrozenCaptiveIntoCustodyEffect(_that, escort, _captive, false);
                        }
                    };
        }
    }

    @Override
    public String getText() {
        return "Take unattended frozen captive into custody";
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
