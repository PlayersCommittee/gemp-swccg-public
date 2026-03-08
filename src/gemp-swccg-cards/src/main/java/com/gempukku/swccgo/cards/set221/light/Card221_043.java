package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardAndAttachFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Interrupt
 * Subtype: Lost
 * Title: A Jedi's Fury
 */
public class Card221_043 extends AbstractLostInterrupt {
    public Card221_043() {
        super(Side.LIGHT, 5, "A Jedi's Fury", Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setLore("It had been decades since Vader had felt the sting of an enemy's blade.");
        setGameText("If His Destiny on table, choose: Luke steals Luke's Lightsaber. (Immune to Weapon Of A Sith.) OR Cancel Dark Strike, Force Field, or You Are Beaten. OR Cancel the game text of a Dark Jedi with Luke for remainder of turn. OR Target a Jedi. Target is power +2 for remainder of turn.");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Dark_Strike, Filters.Force_Field, Filters.You_Are_Beaten))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.canTarget(game, self, Filters.His_Destiny)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        
        TargetingReason targetingReason = TargetingReason.TO_BE_STOLEN;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, Filters.His_Destiny)) {
            final Filter characterFilter = Filters.and(Filters.character, Filters.Luke);
            final Filter weaponFilter = Filters.and(Filters.opponents(self), Filters.character_weapon, Filters.Lukes_Lightsaber, Filters.canBeStolenBy(self, characterFilter));
            if (GameConditions.canTarget(game, self, targetingReason, weaponFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setImmuneTo(Title.Weapon_Of_A_Sith);
                action.setText("Luke steals Luke's Lightsaber");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Luke's Lightsaber to steal", targetingReason, weaponFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard weapon) {
                                final Filter characterToStealWeaponFilter = Filters.and(characterFilter, Filters.canStealAndCarry(weapon));
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose Luke to steal weapon", characterToStealWeaponFilter) {
                                            @Override
                                            protected void cardTargeted(final int targetGroupId2, PhysicalCard character) {
                                                // Allow response(s)
                                                action.allowResponses("Have " + GameUtils.getCardLink(character) + " 'steal' " + GameUtils.getCardLink(weapon),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                                // This needs to be done in case the target(s) were changed during the responses.
                                                                PhysicalCard finalWeapon = action.getPrimaryTargetCard(targetGroupId1);
                                                                PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId2);

                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new StealCardAndAttachFromTableEffect(action, finalWeapon, finalCharacter));
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                    );
                            }
                        });
                    
                actions.add(action);
            }
        }

        if (GameConditions.canTarget(game, self, Filters.His_Destiny)) {
            Filter filterDarkJedi = Filters.and(Filters.opponents(self), Filters.Dark_Jedi, Filters.with(self, Filters.Luke));

            if (GameConditions.canTarget(game, self, filterDarkJedi)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Cancel game text of a Dark Jedi");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Dark Jedi", filterDarkJedi) {
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
                                                        new CancelGameTextUntilEndOfTurnEffect(action, finalTarget));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }

        Filter filterJedi = Filters.Jedi;
        if (GameConditions.canTarget(game, self, Filters.His_Destiny)
                && GameConditions.canTarget(game, self, filterJedi)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target a Jedi to be power +2");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Jedi", filterJedi) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " power +2",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                                                    new PowerModifier(self, finalTarget, 2)
                                                    , "Makes " + GameUtils.getCardLink(finalTarget) + " power +2"));
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
}
