package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.StackCardFromVoidEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.DisarmCharacterEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCarryModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Lost
 * Title: Let The Wookiee Win
 */
public class Card2_053 extends AbstractLostInterrupt {
    public Card2_053() {
        super(Side.LIGHT, 5, Title.Let_The_Wookiee_Win, Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.R1);
        setLore("'It's not wise to upset a Wookiee.' 'But sir, nobody worries about upsetting a droid.' 'That's cause a droid don't pull people's arms out of their sockets when they lose.'");
        setGameText("During a battle at a holosite, add one battle destiny. OR Target an opponent's character of ability < 5 present with your Wookiee that just participated in a battle you lost; character is Disarmed (power -1 and may no longer carry weapons). Stack on that character.");
        addKeywords(Keyword.DISARMING_CARD);
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.holosite)
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
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.lostBattle(game, effectResult, playerId)) {
            Filter targetFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.abilityLessThan(5),
                    Filters.presentWith(self, Filters.and(Filters.your(self), Filters.Wookiee, Filters.participatingInBattle)));
            TargetingReason targetingReason = TargetingReason.TO_BE_DISARMED;
            if (GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Disarm character");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", targetingReason, targetFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Disarm " + GameUtils.getCardLink(targetedCard),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new StackCardFromVoidEffect(action, self, finalTarget));
                                                action.appendEffect(
                                                        new PassthruEffect(action) {
                                                            @Override
                                                            protected void doPlayEffect(SwccgGame game) {
                                                                if (Filters.hasStacked(self).accepts(game, finalTarget)) {
                                                                    action.appendEffect(
                                                                            new DisarmCharacterEffect(action, finalTarget, self));
                                                                    action.appendEffect(
                                                                            new AddUntilEndOfGameModifierEffect(action,
                                                                                    new PowerModifier(self, finalTarget, -1), null));
                                                                    action.appendEffect(
                                                                            new AddUntilEndOfGameModifierEffect(action,
                                                                                    new MayNotCarryModifier(self, finalTarget, Filters.weapon), null));
                                                                }
                                                            }
                                                });

                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}