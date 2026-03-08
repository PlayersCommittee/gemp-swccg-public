package com.gempukku.swccgo.cards.set222.dark;

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
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: Your Powers Are Weak, Old Man (V)
 */
public class Card222_018 extends AbstractUsedOrLostInterrupt {
    public Card222_018() {
        super(Side.DARK, 5, "Your Powers Are Weak, Old Man", Uniqueness.UNRESTRICTED, ExpansionSet.SET_22, Rarity.V);
        setVirtualSuffix(true);
        setLore("'You should not have come back.'");
        setGameText("USED: During battle, cancel game text of an opponent's hut or Jedi survivor. LOST: Cancel Clash Of Sabers (targeting your character of ability > 3) or Old Ben. OR Target a Jedi. Target is power - 2 for remainder of turn.");
        addIcon(Icon.VIRTUAL_SET_22);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter targetFilter = Filters.and(Filters.opponents(playerId), Filters.or(Filters.hut, Filters.Jedi_Survivor));
        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel game text of a card");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose hut or Jedi survivor", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfBattleEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        Filter filterJedi = Filters.Jedi;
        if (GameConditions.canTarget(game, self, filterJedi)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Target a Jedi to be power -2");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Jedi", filterJedi) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " power -2",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                                                    new PowerModifier(self, finalTarget, -2)
                                                    , "Makes " + GameUtils.getCardLink(finalTarget) + " power -2"));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, final SwccgGame game, Effect effect, final PhysicalCard self) {

        // Check condition(s)
        if ((TriggerConditions.isPlayingCardTargeting(game, effect, Filters.Clash_Of_Sabers, Filters.and(Filters.your(playerId), Filters.character, Filters.abilityMoreThan(3)))
                || TriggerConditions.isPlayingCard(game, effect, Filters.Old_Ben))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}
