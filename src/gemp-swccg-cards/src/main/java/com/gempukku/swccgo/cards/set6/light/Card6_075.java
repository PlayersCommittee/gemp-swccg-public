package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.ReleaseCaptiveEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Interrupt
 * Subtype: Used
 * Title: Someone Who Loves You
 */
public class Card6_075 extends AbstractUsedInterrupt {
    public Card6_075() {
        super(Side.LIGHT, 4, Title.Someone_Who_Loves_You, Uniqueness.UNRESTRICTED, ExpansionSet.JABBAS_PALACE, Rarity.U);
        setLore("Han's appreciation toward his mysterious liberator was rather quickly overcome.");
        setGameText("If Han was just lost and Leia is on table (or vice versa), place the just lost character on your Used Pile instead. OR If Leia is present at a site where Han is captive, release Han. OR Cancel a Force drain at a prison.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if ((TriggerConditions.justLost(game, effectResult, Filters.Han) && GameConditions.canSpot(game, self, Filters.Leia))
                || (TriggerConditions.justLost(game, effectResult, Filters.Leia) && GameConditions.canSpot(game, self, Filters.Han))) {
            final PhysicalCard cardLost = ((LostFromTableResult) effectResult).getCard();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Place " + GameUtils.getFullName(cardLost) + " in Used Pile");
            // Allow response(s)
            action.allowResponses("Place " + GameUtils.getCardLink(cardLost) + " in Used Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PutCardFromLostPileInUsedPileEffect(action, playerId, cardLost, true));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, Filters.prison)
                && GameConditions.canCancelForceDrain(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel Force drain");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelForceDrainEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter hanFilter = Filters.and(Filters.Han, Filters.captive, Filters.at(Filters.wherePresent(self, Filters.Leia)));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, hanFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Release Han");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Han", SpotOverride.INCLUDE_CAPTIVE, hanFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Release " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ReleaseCaptiveEffect(action, finalTarget));
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