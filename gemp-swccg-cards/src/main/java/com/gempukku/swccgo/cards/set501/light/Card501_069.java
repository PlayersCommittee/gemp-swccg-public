package com.gempukku.swccgo.cards.set501.light;

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
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayBeFiredTwicePerBattleModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used
 * Title: Savareen Standoff
 */
public class Card501_069 extends AbstractUsedInterrupt {
    public Card501_069() {
        super(Side.LIGHT, 3, "Savareen Standoff", Uniqueness.UNIQUE);
        setLore("");
        setGameText("If a battle was just initiated, fire a blaster deployed on a smuggler. That blaster may fire again this battle. OR Your smuggler armed with a weapon card is defense value +2 for remainder of turn (Interrupt may affect the result immediately after a destiny draw targeting characters defense battle.)");
        addIcons(Icon.VIRTUAL_SET_13);
        setTestingText("Savareen Standoff");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        
        // If a battle was just initiated, fire a blaster deployed on a smuggler. That blaster may fire again this battle.
        Filter blasterOnSmuggler = Filters.and(Filters.your(self), Filters.blaster, Filters.attachedTo(Filters.smuggler));
        Filter weaponToFire = Filters.and(Filters.presentInBattle, blasterOnSmuggler, Filters.canBeFired(self, 0, Filters.canBeTargetedBy(self)));

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult) &&
                GameConditions.canSpot(game, self, weaponToFire)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Fire a weapon");
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose weapon to fire", weaponToFire) {
                        @Override
                        protected void cardSelected(final PhysicalCard weapon) {
                            action.addAnimationGroup(weapon);
                            // Allow response(s)
                            action.allowResponses("Fire " + GameUtils.getCardLink(weapon),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new FireWeaponEffect(action, weapon, false, Filters.canBeTargetedBy(self))
                                            );
                                            action.appendEffect(
                                                    new AddUntilEndOfBattleModifierEffect(action, new MayBeFiredTwicePerBattleModifier(self, weapon), null));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }


        // Your smuggler armed with a weapon card is defense value +2 for remainder of turn (Interrupt may affect the result immediately
        // after a destiny draw targeting characters defense battle.)
        Filter armedWithWeaponCard = Filters.and(Filters.armedWith(Filters.not(Filters.permanentWeaponOf(Filters.any))));
        Filter armedSmuggler = Filters.and(Filters.your(self), Filters.smuggler, armedWithWeaponCard);

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnTargetingAbilityManeuverOrDefenseValue(game, effectResult, Filters.and(armedSmuggler, Filters.canBeTargetedBy(self)))) {
            Collection<PhysicalCard> targetedCards = ((DestinyDrawnResult) effectResult).getAbilityManeuverOrDefenseValueTargeted();

            // Generate action using common method
            PlayInterruptAction action = generatePlayInterruptAction(playerId, game, self, Filters.and(Filters.in(targetedCards), armedSmuggler));
            if (action != null) {
                return Collections.singletonList(action);
            }
        }

        return null;
    }


    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {

        // Your smuggler armed with a weapon card is defense value +2 for remainder of turn (Interrupt may affect the result immediately
        // after a destiny draw targeting characters defense battle.)
        Filter armedWithWeaponCard = Filters.and(Filters.armedWith(Filters.not(Filters.permanentWeaponOf(Filters.any))));
        Filter armedSmuggler = Filters.and(Filters.your(self), Filters.smuggler, armedWithWeaponCard);

        // Check condition(s)
        if (GameConditions.canTarget(game, self, armedSmuggler)) {

            // Generate action using common method
            PlayInterruptAction action = generatePlayInterruptAction(playerId, game, self, armedSmuggler);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }


    private PlayInterruptAction generatePlayInterruptAction(final String playerId, SwccgGame game, final PhysicalCard self, Filter filter) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Add 2 to defense value");
        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose armed smuggler", filter) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        action.addAnimationGroup(targetedCard);
                        // Allow response(s)
                        action.allowResponses("Add 2 to defense value of " + GameUtils.getCardLink(targetedCard),
                                new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the final targeted card(s)
                                        final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                        // Perform result(s)
                                        action.appendEffect(
                                                new ModifyDefenseValueUntilEndOfTurnEffect(action, finalTarget, 2));
                                    }
                                }
                        );
                    }
                }
        );
        return action;
    }
}