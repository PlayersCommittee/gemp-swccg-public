package com.gempukku.swccgo.cards.set207.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.effects.CancelReactEffect;
import com.gempukku.swccgo.logic.effects.ExcludeFromBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 7
 * Type: Interrupt
 * Subtype: Lost
 * Title: Help Me Obi-Wan Kenobi (V)
 */
public class Card207_012 extends AbstractLostInterrupt {
    public Card207_012() {
        super(Side.LIGHT, 3, Title.Help_Me_Obi_Wan_Kenobi, Uniqueness.UNRESTRICTED, ExpansionSet.SET_7, Rarity.V);
        setVirtualSuffix(true);
        setLore("Leia sent a hologram plea, 'General Kenobi, years ago you served my father in the Clone Wars. Now he begs you to help him in his struggle against the Empire.'");
        setGameText("During battle, target an opponent's character of ability < 2 present with your Jedi; target is excluded from battle. OR Cancel a 'react.' OR If Obi-Wan is at a battleground site, and your Rebel or Republic character (or Leia) is in battle at another location, add one battle destiny.");
        addKeywords(Keyword.HOLOGRAM);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)) {
            Filter filter = Filters.and(Filters.opponents(self), Filters.character, Filters.abilityLessThan(2), Filters.participatingInBattle,
                    Filters.presentWith(self, Filters.and(Filters.your(self), Filters.Jedi)));
            TargetingReason targetingReason = TargetingReason.TO_BE_EXCLUDED_FROM_BATTLE;
            if (GameConditions.canTarget(game, self, targetingReason, filter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Exclude character from battle");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", targetingReason, filter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Exclude " + GameUtils.getCardLink(targetedCard) + " from battle",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new ExcludeFromBattleEffect(action, finalTarget));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.or(Filters.Rebel, Filters.Republic_character, Filters.Leia)))
                && GameConditions.canSpot(game, self, Filters.and(Filters.ObiWan, Filters.at(Filters.and(Filters.battleground_site, Filters.not(Filters.battleLocation)))))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 1));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isReact(game, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel 'react'");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelReactEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}