package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.IonizationType;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.IonizedResult;

/**
 * An effect to make a starship ionized.
 */
public class IonizeStarshipEffect extends AbstractSubActionEffect {
    private PhysicalCard _cardIonized;
    private PhysicalCard _ionizedByCard;
    private boolean _ionizePower;
    private boolean _ionizeDefense;
    private boolean _ionizeHyperspeed;

    /**
     * Creates an effect to make a starship Ionized.
     * @param action the action performing this effect
     * @param cardIonized the card that is "Ionized"
     * @param ionizedByCard the card the card was "Ionized" by
     * @param ionizePower whether or not the Power of this card should be Ionized
     * @param ionizeDefense whether or not the Armor/Maneuver of this card should be Ionized
     * @param ionizeHyperspeed whether or not the Hyperspeed of this card should be Ionized
     */
    public IonizeStarshipEffect(Action action, PhysicalCard cardIonized, PhysicalCard ionizedByCard, boolean ionizePower, boolean ionizeDefense, boolean ionizeHyperspeed) {
        super(action);
        _cardIonized = cardIonized;
        _ionizedByCard = ionizedByCard;
        _ionizePower = ionizePower;
        _ionizeDefense = ionizeDefense;
        _ionizeHyperspeed = ionizeHyperspeed;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        //final GameState gameState = game.getGameState();
        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (_ionizePower)
                            _cardIonized.addIonization(IonizationType.POWER_IONIZED);
                        if (_ionizeDefense)
                            _cardIonized.addIonization(IonizationType.DEFENSE_IONIZED);
                        if (_ionizeHyperspeed)
                            _cardIonized.addIonization(IonizationType.HYPERSPEED_IONIZED);
                        if (_ionizePower && _ionizeHyperspeed) { // Currently just from Planetary Ion Cannon
                            subAction.appendEffect(new ResetPowerAndHyperspeedEffect(subAction, _cardIonized, 0));
                        }
                        else if (_ionizeDefense && _ionizeHyperspeed) { // Other Ion Cannons
                            subAction.appendEffect(new ResetArmorManeuverAndHyperspeedEffect(subAction, _cardIonized, 0));
                        }
                        subAction.appendEffect(
                                new TriggeringResultEffect(subAction, new IonizedResult(_ionizedByCard.getOwner(), _cardIonized)));
                    }
                });

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
