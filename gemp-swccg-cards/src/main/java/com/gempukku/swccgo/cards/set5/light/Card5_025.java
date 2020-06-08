package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableUsingForfeitValueEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Effect
 * Title: Hero Of A Thousand Devices
 */
public class Card5_025 extends AbstractNormalEffect {
    public Card5_025() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, Title.Hero_Of_A_Thousand_Devices);
        setLore("Left to his own devices, Artoo used his spunk and creativity to save his companions' lives time and time again.");
        setGameText("Deploy on one of your droids. During your deploy phase, may use 1 Force to search your Reserve Deck, take one device that deploys on a droid into hand and reshuffle. Also, you may forfeit devices deployed on this droid (forfeit value = destiny number).");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.droid);
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.droid;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.HERO_OF_A_THOUSAND_DEVICES__UPLOAD_DEVICE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take device into hand from Reserve Deck");
            action.setActionMsg("Take a device that deploys on a droid into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.device_that_deploys_on_droids, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && (GameConditions.isBattleDamageRemaining(game, playerId) || GameConditions.isAttritionRemaining(game, playerId))) {
            PhysicalCard attachedTo = self.getAttachedTo();
            if (attachedTo != null && GameConditions.isInBattle(game, attachedTo)) {
                Collection<PhysicalCard> devicesToForfeit = Filters.filterActive(game, self,
                        Filters.and(Filters.your(self), Filters.device, Filters.attachedTo(attachedTo), Filters.not(Filters.deviceMayNotBeRemovedUnlessDisarmed)));
                if (!devicesToForfeit.isEmpty()) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Forfeit a device");
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, playerId, "Choose device to forfeit", devicesToForfeit) {
                                @Override
                                protected void cardSelected(PhysicalCard selectedCard) {
                                    action.addAnimationGroup(selectedCard);
                                    action.setActionMsg("Forfeit " + GameUtils.getCardLink(selectedCard));
                                    // Perform result(s)
                                    float forfeitValue = game.getModifiersQuerying().getDestiny(game.getGameState(), selectedCard);
                                    action.appendEffect(
                                            new ForfeitCardFromTableUsingForfeitValueEffect(action, selectedCard, forfeitValue));
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}