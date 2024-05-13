package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.MoveToRelatedStarshipOrVehicleSiteEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

/**
 * An action to move to a starship or vehicle site from its related starship or vehicle.
 */
public class MoveToRelatedStarshipOrVehicleSiteAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _cardToMove;
    private Effect _moveCardEffect;
    private boolean _cardMoved;
    private Action _that;

    /**
     * Creates an action to move to a starship or vehicle site from a related starship or vehicle.
     * @param playerId the player
     * @param card the card to move
     * @param moveTargetFilter the filter for where the card can be move
     */
    public MoveToRelatedStarshipOrVehicleSiteAction(final String playerId, final PhysicalCard card, final Filter moveTargetFilter) {
        super(card, playerId);
        _cardToMove = card;
        _that = this;

        appendTargeting(
                new ChooseCardOnTableEffect(_that, playerId, "Choose where to move " + GameUtils.getCardLink(card), moveTargetFilter) {
                    @Override
                    protected void cardSelected(PhysicalCard selectedCard) {

                        _moveCardEffect = new MoveToRelatedStarshipOrVehicleSiteEffect(_that, card, selectedCard);
                    }
                }
        );
    }

    @Override
    public String getText() {
        return "Move to related starship or vehicle site";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        // Verify no costs have failed
        if (!isAnyCostFailed()) {

            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_cardMoved) {
                _cardMoved = true;
                return _moveCardEffect;
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _cardMoved && _moveCardEffect.wasCarriedOut();
    }
}
