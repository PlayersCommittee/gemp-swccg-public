package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Interrupt
 * Subtype: Used
 * Title: Drop Your Weapons
 */
public class Card14_102 extends AbstractUsedInterrupt {
    public Card14_102() {
        super(Side.DARK, 5, "Drop Your Weapons", Uniqueness.UNIQUE);
        setLore("In situations of confrontation, a command high on a battle droid's priority queue is to neutralize any kind of armament an opponent has.");
        setGameText("Target an opponent's weapon at same site as your battle droid. Each of that weapon's destiny draws are -1 for remainder of turn. OR If your battle droid just fired a blaster and hit a character, all opponent's weapons at same site are lost.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.opponents(self), Filters.weapon_or_character_with_permanent_weapon, Filters.at(Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.battle_droid))));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target opponent's weapon");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose opponent's weapon", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Make each of " + GameUtils.getCardLink(targetedCard) + "'s weapon destiny draws -1",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new AddUntilEndOfTurnModifierEffect(action,
                                                                new EachWeaponDestinyModifier(self, Filters.or(finalTarget, Filters.permanentWeaponOf(Filters.sameCardId(finalTarget))), -1),
                                                        "Makes each of " + GameUtils.getCardLink(finalTarget) + "'s weapon destiny draws -1"));
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
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, Filters.character, Filters.blaster, Filters.and(Filters.your(self), Filters.battle_droid))) {
            PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();
            final Filter filter = Filters.and(Filters.opponents(self), Filters.character_weapon, Filters.atSameSite(cardHit));
            if (GameConditions.canSpot(game, self, filter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Make opponent's character weapons lost");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                Collection<PhysicalCard> opponentsWeapons = Filters.filterActive(game, self, filter);
                                // Perform result(s)
                                action.appendEffect(
                                        new LoseCardsFromTableEffect(action, opponentsWeapons));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}