package com.gempukku.swccgo.cards.set213.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
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
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.*;


/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used
 * Title: Savareen Standoff
 */
public class Card213_054 extends AbstractUsedInterrupt {
    public Card213_054() {
        super(Side.LIGHT, 3, "Savareen Standoff", Uniqueness.UNIQUE);
        setLore("");
        setGameText("If a battle was just initiated, immediately fire a blaster deployed on your smuggler present. That blaster may fire again this battle. OR Your smuggler armed with a weapon card is defense value +2 for remainder of turn (even if just targeted by an Interrupt or weapon).");
        addIcons(Icon.VIRTUAL_SET_13);
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
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Your smuggler armed with a weapon card is defense value +2 for remainder of turn (even if just targeted by an Interrupt or weapon).
        Filter armedWithWeaponCard = Filters.and(Filters.armedWith(Filters.not(Filters.permanentWeaponOf(Filters.any))));
        Filter armedSmuggler = Filters.and(Filters.your(self), Filters.smuggler, armedWithWeaponCard);

        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.and(armedSmuggler, Filters.canBeTargetedBy(self)), Filters.any)) {
            Collection<PhysicalCard> targetedCards = game.getGameState().getWeaponFiringState().getTargets();
            // Generate action using common method
            PlayInterruptAction action = generatePlayInterruptAction(playerId, game, self, Filters.and(Filters.in(targetedCards), armedSmuggler));
            if (action != null) {
                actions.add(action);
            }
        }

        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.Interrupt, Filters.and(armedSmuggler, Filters.canBeTargetedBy(self)))) {
            Collection<PhysicalCard> targetedCards = new HashSet<PhysicalCard>();
            //get all of the cards targeted by the interrupt
            Map<Integer, Map<PhysicalCard, Set<TargetingReason>>> primaryTargets = ((RespondablePlayingCardEffect) effect).getTargetingAction().getAllPrimaryTargetCards();
            List<PhysicalCard> secondaryTargets = ((RespondablePlayingCardEffect) effect).getTargetingAction().getAllSecondaryTargetCards(game);
            for (Map<PhysicalCard, Set<TargetingReason>> map : primaryTargets.values()) {
                targetedCards.addAll(map.keySet());
            }
            targetedCards.addAll(secondaryTargets);

            PlayInterruptAction action = generatePlayInterruptAction(playerId, game, self, Filters.and(Filters.in(targetedCards), armedSmuggler));
            if (action != null) {
                actions.add(action);
            }
        }
        return actions;
    }


    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Your smuggler armed with a weapon card is defense value +2 for remainder of turn (even if just targeted by an Interrupt or weapon).
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