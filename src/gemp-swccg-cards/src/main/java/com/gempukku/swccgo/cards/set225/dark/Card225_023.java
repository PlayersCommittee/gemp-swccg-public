package com.gempukku.swccgo.cards.set225.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfWeaponFiringModifierEffect;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayBeFiredTwicePerBattleModifier;
import com.gempukku.swccgo.logic.modifiers.MayTargetAdjacentSiteModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.FiredWeaponResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Interrupt
 * Subtype: Used
 * Title: More! MORE!!!!
 */

public class Card225_023 extends AbstractUsedInterrupt {
    public Card225_023() {
        super(Side.DARK, 5, "More! MORE!!!!", Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("");
        setGameText("Fire an [Episode VII] blaster or [Episode VII] cannon into a battle at an adjacent site. OR If Kylo in battle, add 1 to a just drawn destiny. OR Once per game during battle, if you just fired an [Episode VII] weapon (except a lightsaber), it may fire again this battle.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_24);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)) {
            Filter weaponFilter = Filters.and(Filters.your(self), Filters.at(Filters.adjacentSiteTo(self, Filters.battleLocation)), 
                                              Filters.and(Icon.EPISODE_VII, Filters.or(Filters.blaster, Filters.cannon)));
            if (GameConditions.canSpot(game, self, weaponFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Fire a weapon into a battle at an adjacent site");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose weapon to fire", weaponFilter) {
                            @Override
                            protected void cardSelected(final PhysicalCard weapon) {
                                action.addAnimationGroup(weapon);
                                action.allowResponses("Fire" + GameUtils.getCardLink(weapon),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                new AddUntilEndOfWeaponFiringModifierEffect(action, new MayTargetAdjacentSiteModifier(self, weapon), null)
                                            );
                                            action.appendEffect(
                                                        new FireWeaponEffect(action, weapon, false, Filters.canBeTargetedBy(self)));
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

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawn(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Kylo)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add 1 to destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyDestinyEffect(action, 1));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && TriggerConditions.weaponJustFired(game, effectResult, 
                    Filters.and(Filters.weapon, Filters.icon(Icon.EPISODE_VII), Filters.not(Filters.lightsaber)))) {

            GameTextActionId gameTextActionId = GameTextActionId.MORE_MORE__FIRE_WEAPON_AGAIN;
            FiredWeaponResult weaponFiredResult = (FiredWeaponResult) effectResult;
            final PhysicalCard weaponCard = (weaponFiredResult.getPermanentWeaponFired() != null) ? weaponFiredResult.getPermanentWeaponFired().getPhysicalCard(game) : weaponFiredResult.getWeaponCardFired();

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Allow " + GameUtils.getCardLink(weaponCard) + " to fire again");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action, new MayBeFiredTwicePerBattleModifier(self, weaponCard), null));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

}
