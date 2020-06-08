package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerCaptiveEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardsIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.DeliveredCaptiveToPrisonResult;
import com.gempukku.swccgo.logic.timing.results.SpecialDeliveryCompletedResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Effect
 * Title: Special Delivery
 */
public class Card5_123 extends AbstractNormalEffect {
    public Card5_123() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Special_Delivery, Uniqueness.UNIQUE);
        setLore("Because bounty hunters are untrustworthy, the Empire relies on its troopers for efficient prisoner delivery.");
        setGameText("Deploy on a prison. When one of your troopers 'delivers' (imprisons) a captive here, you may search your Lost Pile, take any one card into hand and then lose effect. (Each captive may be 'delivered' only once until they are released or leave table)");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.prison;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SPECIAL_DELIVERY__SEARCH_LOST_PILE;

        // Check condition(s)
        if (TriggerConditions.captiveDeliveredToPrisonBy(game, effectResult, Filters.and(Filters.your(self), Filters.trooper), Filters.here(self))) {
            PhysicalCard captive = ((DeliveredCaptiveToPrisonResult) effectResult).getCaptive();
            PhysicalCard escort = ((DeliveredCaptiveToPrisonResult) effectResult).getEscort();
            if (GameConditions.isOncePerCaptive(game, self, captive, gameTextActionId)
                    && GameConditions.canTakeCardsIntoHandFromLostPile(game, playerId, self, gameTextActionId)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Take card into hand from Lost Pile");
                action.setActionMsg("Take a card into hand from Lost Pile");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerCaptiveEffect(action, captive));
                // Perform result(s)
                action.appendEffect(
                        new TriggeringResultEffect(action, new SpecialDeliveryCompletedResult(playerId, action, self, escort)));
                action.appendEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                int numCards = getMaxNumCardsToTakeIntoHand(game, self);
                                action.appendEffect(
                                        new TakeCardsIntoHandFromLostPileEffect(action, playerId, 1, numCards, false));
                                action.appendEffect(
                                        new LoseCardFromTableEffect(action, self));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    private int getMaxNumCardsToTakeIntoHand(SwccgGame game, PhysicalCard self) {
        return 1 + (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.SPECIAL_DELIVERY__TAKE_TWO_ADDITIONAL_CARDS_INTO_HAND) ? 2 : 0);
    }
}