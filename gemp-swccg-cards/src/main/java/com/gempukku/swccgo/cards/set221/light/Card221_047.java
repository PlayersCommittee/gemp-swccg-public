package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractLostOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelTargetingEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.TargetingActionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Interrupt
 * Subtype: Lost or Starting
 * Title: Anakin's Destiny
 */
public class Card221_047 extends AbstractLostOrStartingInterrupt {
    public Card221_047() {
        super(Side.LIGHT, 4, "Anakin's Destiny", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setLore("'A vergence, you say?'");
        setGameText("LOST: Cancel an attempt to 'choke' (or target with Force Lightning) your character of ability > 4. " +
                "STARTING: If He Is The Chosen One on table, deploy His Destiny and two Effects that deploy for free and are always immune to Alter. Place Interrupt in Lost Pile.");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_21);
    }


    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        String opponent = game.getOpponent(playerId);
        Filter filter = Filters.and(Filters.your(self), Filters.character, Filters.abilityMoreThan(4));
        Collection<TargetingReason> targetingReasons = Arrays.asList(TargetingReason.TO_BE_CHOKED);

        // Check condition(s)
        if (TriggerConditions.isTargetedForReason(game, effect, opponent, filter, targetingReasons)
                || TriggerConditions.isPlayingCardTargeting(game, effect, Filters.Force_Lightning, filter)) {
            final RespondableEffect respondableEffect = (RespondableEffect) effect;
            final List<PhysicalCard> cardsTargeted = (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.Force_Lightning, filter)?
                    TargetingActionUtils.getCardsTargeted(game, respondableEffect.getTargetingAction(), filter) :
                    TargetingActionUtils.getCardsTargetedForReason(game, respondableEffect.getTargetingAction(), targetingReasons, filter));
            if (!cardsTargeted.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Cancel targeting");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", Filters.in(cardsTargeted)) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard target) {
                                action.addAnimationGroup(target);
                                // Allow response(s)
                                action.allowResponses("Cancel targeting of " + GameUtils.getCardLink(target),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new CancelTargetingEffect(action, respondableEffect));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.canTarget(game, self, Filters.He_Is_The_Chosen_One)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Deploy His Destiny and two Effects that deploy for free and are always immune to Alter.");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.titleContains(Title.His_Destiny),  true, false));
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.deploysForFree, Filters.immune_to_Alter), 2, 2, true, false));
                            action.appendEffect(
                                    new PutCardFromVoidInLostPileEffect(action, playerId, self));
                        }
                    }
            );
            return action;
        }
        return null;
    }
}
