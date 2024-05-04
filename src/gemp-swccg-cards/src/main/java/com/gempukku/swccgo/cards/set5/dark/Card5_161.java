package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.EnhanceForceDrainResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Weapon Of An Ungrateful Son
 */
public class Card5_161 extends AbstractUsedOrLostInterrupt {
    public Card5_161() {
        super(Side.DARK, 5, "Weapon Of An Ungrateful Son", Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.U);
        setLore("When Vader dueled Luke at Cloud City, he fought a son trained by his former master and a weapon constructed by his former self.");
        setGameText("USED: If a lightsaber was just used to enhance a Force drain, place it in Owner's Used Pile. LOST: Place any or all of your devices and character weapons on table in your Used Pile.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.forceDrainEnhancedByWeapon(game, effectResult, Filters.lightsaber)) {
            final PhysicalCard weapon = ((EnhanceForceDrainResult) effectResult).getWeapon();

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Place " + GameUtils.getFullName(weapon) + " in Used Pile");
            // Allow response(s)
            action.allowResponses("Place " + GameUtils.getCardLink(weapon) + " in Used Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PlaceCardInUsedPileFromTableEffect(action, weapon));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.or(Filters.and(Filters.device, Filters.not(Filters.deviceMayNotBeRemovedUnlessDisarmed)), Filters.character_weapon));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Place devices and weapons in Used Pile");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardsOnTableEffect(action, playerId, "Choose devices and character weapons", 1, Integer.MAX_VALUE, filter) {
                        @Override
                        protected void cardsTargeted(final int targetGroupId, final Collection<PhysicalCard> cardsTargeted) {
                            action.addAnimationGroup(cardsTargeted);
                            // Allow response(s)
                            action.allowResponses("Place " + GameUtils.getAppendedNames(cardsTargeted) + " in Used Pile",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            Collection<PhysicalCard> finalTargets = action.getPrimaryTargetCards(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new PlaceCardsInUsedPileFromTableEffect(action, finalTargets));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}