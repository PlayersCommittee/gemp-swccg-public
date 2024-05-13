package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.ResetLandspeedUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that causes the specified player to choose character on table to have landspeed reset until end of turn.
 */
public class ChooseCharacterOnTableToResetLandspeedUntilEndOfTurnEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private Filterable _characterFilter;
    private float _resetValue;

    /**
     * Creates an effect that causes the specified player to choose character on table to have landspeed reset until end of turn.
     * @param action the action performing this effect
     * @param playerId the player
     * @param characterFilter the character filter
     * @param resetValue the value to reset landspeed to
     */
    public ChooseCharacterOnTableToResetLandspeedUntilEndOfTurnEffect(Action action, String playerId, Filterable characterFilter, float resetValue) {
        super(action);
        _performingPlayerId = playerId;
        _characterFilter = characterFilter;
        _resetValue = resetValue;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action, _performingPlayerId);
        subAction.appendTargeting(
                new TargetCardOnTableEffect(subAction, _performingPlayerId, "Choose character to reset landspeed to " + _resetValue, _characterFilter) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                        subAction.addAnimationGroup(targetedCard);
                        // Allow response(s)
                        subAction.allowResponses("Reset landspeed of " + GameUtils.getCardLink(targetedCard) + " to 0",
                                new UnrespondableEffect(subAction) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        subAction.appendEffect(
                                                new ResetLandspeedUntilEndOfTurnEffect(_action, targetedCard, 0));
                                    }
                                }
                        );
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
