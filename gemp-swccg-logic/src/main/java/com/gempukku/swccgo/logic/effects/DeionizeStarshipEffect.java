package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.IonizationType;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;


/**
 * An effect to make a starship no longer Ionized.
 */
public class DeionizeStarshipEffect extends AbstractSubActionEffect {
    private PhysicalCard _cardDeionized;
    private boolean _deionizePower;
    private boolean _deionizeDefense;
    private boolean _deionizeHyperspeed;

    /**
     * Creates an effect to make a character no longer ionized.
     *
     * @param action        the action performing this effect
     * @param cardDeionized the card that is deionized
     */
    public DeionizeStarshipEffect(Action action, PhysicalCard cardDeionized, boolean deionizePower, boolean deionizeDefense, boolean deionizeHyperspeed) {
        super(action);
        _cardDeionized = cardDeionized;
        _deionizePower = deionizePower;
        _deionizeDefense = deionizeDefense;
        _deionizeHyperspeed = deionizeHyperspeed;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (_deionizePower) {
                            _cardDeionized.removeIonization(IonizationType.POWER_IONIZED);
                        }
                        if (_deionizeDefense) {
                            _cardDeionized.removeIonization(IonizationType.DEFENSE_IONIZED);
                        }
                        if (_deionizeHyperspeed) {
                            _cardDeionized.removeIonization(IonizationType.HYPERSPEED_IONIZED);
                        }
                        if (_deionizeDefense && _deionizeHyperspeed) {
                            subAction.appendEffect(
                                    new RestoreArmorManeuverHyperspeedToNormalEffect(_action, _cardDeionized));
                        }
                    }
                }
        );

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}