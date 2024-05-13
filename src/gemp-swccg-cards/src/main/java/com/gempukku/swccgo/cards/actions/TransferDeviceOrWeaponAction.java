package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.cards.effects.PayTransferCostEffect;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TransferDeviceOrWeaponEffect;
import com.gempukku.swccgo.logic.timing.Effect;


/**
 * An action to transfer a device or weapon from the card owner's character, vehicle, or starship to another such card present.
 */
public class TransferDeviceOrWeaponAction extends AbstractTopLevelRuleAction {
    private TransferDeviceOrWeaponAction _that;
    private PhysicalCard _cardToTransfer;
    protected String _text;
    private PhysicalCard _target;
    private boolean _forFree;
    private PlayCardOption _playCardOption;
    private boolean _forceCostApplied;
    private boolean _cardTransferred;
    private TransferDeviceOrWeaponEffect _transferCardEffect;

    /**
     * Creates an action to transfer a device or weapon from the card owner's character, vehicle, or starship to another
     * such card present.
     * @param playerId the player performing the action
     * @param cardToTransfer the card to transfer
     * @param playCardOption the play card option chosen
     * @param transferTargetFilter the filter for where the card can be transferred
     */
    public TransferDeviceOrWeaponAction(final String playerId, final PhysicalCard cardToTransfer, PlayCardOption playCardOption, boolean forFree, Filter transferTargetFilter) {
        super(cardToTransfer, playerId);
        _that = this;
        _cardToTransfer = cardToTransfer;
        _playCardOption = playCardOption;
        _forFree = forFree;
        _text = "Transfer";

        appendTargeting(
                new TargetCardOnTableEffect(_that, getPerformingPlayer(), "Choose where to transfer " + GameUtils.getCardLink(_cardToTransfer), TargetingReason.TO_BE_DEPLOYED_ON, transferTargetFilter) {
                    @Override
                    protected void cardTargeted(int targetGroupId, PhysicalCard target) {
                        _target = target;
                    }
                }
        );
    }

    @Override
    public String getText() {
        return _text;
    }

    /**
     * Sets the text shown for the action selection on the User Interface
     * @param text the text to show for the action selection
     */
    @Override
    public void setText(String text) {
        _text = text;
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        // Verify no costs have failed
        if (!isAnyCostFailed()) {

            // Perform any costs in the queue
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            // Pay the deploy cost(s)
            if (!_forceCostApplied) {
                _forceCostApplied = true;

                if (!_forFree) {
                    appendCost(new PayTransferCostEffect(_that, _cardToTransfer, _target, _playCardOption));
                    return getNextCost();
                }
            }

            // Transfer the card
            if (!_cardTransferred) {
                _cardTransferred = true;

                _transferCardEffect = new TransferDeviceOrWeaponEffect(_that, _cardToTransfer, _target, _playCardOption.getId());
                return _transferCardEffect;
            }
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _cardTransferred && _transferCardEffect.wasCarriedOut();
    }
}
