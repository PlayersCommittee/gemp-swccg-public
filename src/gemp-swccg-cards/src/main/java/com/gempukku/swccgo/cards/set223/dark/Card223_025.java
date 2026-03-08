package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.StackCardFromVoidEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.logic.effects.RecirculateEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Interrupt
 * Subtype: Lost
 * Title: The Empire's Back (V)
 */
public class Card223_025 extends AbstractLostInterrupt {
    public Card223_025() {
        super(Side.DARK, 3, "The Empire's Back", Uniqueness.UNRESTRICTED, ExpansionSet.SET_23, Rarity.V);
        setLore("No star system will dare oppose the Emperor now.");
        setGameText(" If two Imperials (or two Black Sun agents) in battle, re-circulate and shuffle your Reserve Deck. OR Once per game, if Emperor or Tarkin on table, Allegations Of Corruption stacks opponent's just played Interrupt; this does not count towards that card's once per game limit.");
        addIcons(Icon.VIRTUAL_SET_23);
        setVirtualSuffix(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check Condition(s)
        if (GameConditions.isDuringBattle(game)
                && (GameConditions.canTarget(game, self, 2, Filters.and(Filters.Imperial, Filters.participatingInBattle))
                || GameConditions.canTarget(game, self, 2, Filters.and(Filters.Black_Sun_agent, Filters.participatingInBattle)))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Re-circulate and shuffle");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RecirculateEffect(action, playerId));
                            action.appendEffect(
                                    new ShuffleReserveDeckEffect(action, playerId));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.THE_EMPIRES_BACK_V__STACK_CARD;
        Filter grabberFilter = Filters.Allegations_Of_Corruption;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSpot(game, self, Filters.or(Filters.Emperor, Filters.Tarkin))
                && TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.opponents(self), Filters.Interrupt))
                && GameConditions.canTarget(game, self, grabberFilter)) {

            final PhysicalCard interrupt = ((RespondablePlayingCardEffect)effect).getCard();

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Stack card on Allegations Of Corruption");

            action.appendUsage(
                    new OncePerGameEffect(action));

            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose grabber to stack " + GameUtils.getFullName(interrupt) + " on", grabberFilter) {
                @Override
                protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses("Stack " + GameUtils.getFullName(interrupt) + " on " + GameUtils.getCardLink(targetedCard), new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            PhysicalCard finalGrabber = action.getPrimaryTargetCard(targetGroupId);

                            action.appendEffect(
                                    new StackCardFromVoidEffect(action, interrupt, finalGrabber));
                        }
                    });
                }
            });

            return Collections.singletonList(action);
        }
        return null;
    }
}
