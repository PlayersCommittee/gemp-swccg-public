package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToHideFromBattleResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Used
 * Title: Nice Of You Guys To Drop By
 */
public class Card3_046 extends AbstractUsedInterrupt {
    public Card3_046() {
        super(Side.LIGHT, 5, "Nice Of You Guys To Drop By", Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.C2);
        setLore("'Echo Base, this is Rogue 2. I've found them. Repeat. I've found them.'");
        setGameText("Send one of your missing characters at same site as one of your piloted or driven vehicles to your Used Pile. OR Cancel any attempt by one character to 'hide' from a battle.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.missing, Filters.character, Filters.at(Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.or(Filters.piloted, Filters.driven), Filters.vehicle))));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_MISSING, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Send missing character to Used Pile");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose missing character", SpotOverride.INCLUDE_MISSING, filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard missingCharacter) {
                            action.addAnimationGroup(missingCharacter);
                            // Allow response(s)
                            action.allowResponses("Send " + GameUtils.getCardLink(missingCharacter) + " to Used Pile",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new PlaceCardInUsedPileFromTableEffect(action, finalTarget));
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

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isAboutToHideFromBattle(game, effectResult, Filters.character)) {
            final AboutToHideFromBattleResult result = (AboutToHideFromBattleResult) effectResult;
            final PhysicalCard cardToHide = result.getCardToHideFromBattle();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel attempt to 'hide' " + GameUtils.getFullName(cardToHide) + " from battle");
            // Allow response(s)
            action.allowResponses("Cancel attempt to 'hide' " + GameUtils.getCardLink(cardToHide) + " from battle",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            result.getPreventableCardEffect().preventEffectOnCard(cardToHide);
                            action.appendEffect(
                                    new SendMessageEffect(action, playerId + " canceled attempt to 'hide' " + GameUtils.getCardLink(cardToHide) + " from battle"));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}