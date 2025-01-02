package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.MoveAwayAsReactEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyEachWeaponDestinyBeforeDrawingDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Interrupt
 * Subtype: Lost
 * Title: Pocket Salt
 */
public class Card304_148 extends AbstractLostInterrupt {
    public Card304_148() {
        super(Side.DARK, 3, Title.Dodge, Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("A unique tactic passed down from Stormtroopers who fought on Crait. Made popular in the Brotherhood by Master Dracaryis.");
        setGameText("If one of your characters of ability > 2 was just targeted by a weapon, subtract 3 from each destiny draw. OR If opponent just initiated a battle at a site against one of your lone characters present, move that character away as a 'react' (for free).");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.and(Filters.your(self), Filters.character, Filters.abilityMoreThan(2)), Filters.weapon)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Subtract 3 from each weapon destiny draw");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyEachWeaponDestinyBeforeDrawingDestinyEffect(action, -3));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, opponent)) {
            Filter characterFilter = Filters.and(Filters.your(self), Filters.character, Filters.alone, Filters.presentInBattle, Filters.canMoveAsReactAsActionFromOtherCard(self, true, 0, true));
            if (GameConditions.canTarget(game, self, characterFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Move character away as 'react'");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", characterFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Move " + GameUtils.getCardLink(targetedCard) + " away as a 'react'",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new MoveAwayAsReactEffect(action, finalCharacter, true));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return actions;
    }
}