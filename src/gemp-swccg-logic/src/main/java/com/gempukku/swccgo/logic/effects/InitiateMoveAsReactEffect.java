package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;

/**
 * Opens the optional-response window for a move as a 'react' before embarking or the regular move.
 * AR Appendix C (React - Move): other cards embark just before the reacting card leaves, which is after
 * the react can be canceled.
 */
public class InitiateMoveAsReactEffect extends AbstractSubActionEffect implements MovingAsReactEffect {
    private final PhysicalCard _cardToReact;
    private final PhysicalCard _fromLocation;
    private final PhysicalCard _toLocation;
    private final Type _type;

    public InitiateMoveAsReactEffect(Action action, PhysicalCard cardToReact, PhysicalCard fromLocation,
                                     PhysicalCard toLocation, Type type) {
        super(action);
        _cardToReact = cardToReact;
        _fromLocation = fromLocation;
        _toLocation = toLocation;
        _type = type;
    }

    @Override
    public Type getType() {
        return _type;
    }

    @Override
    public String getText(SwccgGame game) {
        StringBuilder text = new StringBuilder("Moving ");
        text.append(GameUtils.getCardLink(_cardToReact));
        if (_toLocation != null) {
            text.append(" to ").append(GameUtils.getCardLink(_toLocation));
        }
        text.append(" as a 'react'");
        return text.toString();
    }

    @Override
    public Collection<PhysicalCard> getCardsMoving() {
        return Collections.singletonList(_cardToReact);
    }

    @Override
    public PhysicalCard getMovingFrom() {
        return _fromLocation;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
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
