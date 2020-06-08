package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.FiredWeaponResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used
 * Title: Stay Sharp!
 */
public class Card7_104 extends AbstractUsedInterrupt {
    public Card7_104() {
        super(Side.LIGHT, 4, "Stay Sharp!", Uniqueness.UNIQUE);
        setLore("'Ha haaaaaa!'");
        setGameText("During your control phase, fire one of your starship weapons (for free). If Han or any gunner is aboard that starship, may add 2 to destiny draw. 'Hit' target is lost. OR If you just fired a weapon in battle, add that weapon's destiny number to your total power.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final int gameTextSourceCardId = self.getCardId();

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)) {
            Filter weaponFilter = Filters.and(Filters.your(self), Filters.starship_weapon, Filters.canBeFiredForFreeAt(self, 0, Filters.canBeTargetedBy(self)));
            if (GameConditions.canSpot(game, self, weaponFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Fire a starship weapon");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose starship weapon to fire", weaponFilter) {
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
                                                        new FireWeaponEffect(action, weapon, true, Filters.canBeTargetedBy(self)) {
                                                            @Override
                                                            protected List<ActionProxy> getWeaponFiringActionProxies(String playerId2, SwccgGame game, final PhysicalCard weapon) {
                                                                ActionProxy actionProxy = new AbstractActionProxy() {
                                                                    @Override
                                                                    public List<TriggerAction> getOptionalAfterTriggers(String playerId3, SwccgGame game, EffectResult effectResult) {
                                                                        List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                                                        // Check condition(s)
                                                                        if (playerId.equals(playerId3)
                                                                                && TriggerConditions.isWeaponDestinyJustDrawnBy(game, effectResult, playerId, weapon,
                                                                                Filters.and(Filters.starship, Filters.hasAboard(self, Filters.and(Filters.your(self), Filters.or(Filters.Han, Filters.gunner)))))) {

                                                                            final OptionalGameTextTriggerAction action1 = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                                                                            action1.setText("Add 2 to weapon destiny");
                                                                            // Perform result(s)
                                                                            action1.appendEffect(
                                                                                    new ModifyDestinyEffect(action1, 2));
                                                                            actions.add(action1);
                                                                        }
                                                                        return actions;
                                                                    }
                                                                };
                                                                return Collections.singletonList(actionProxy);
                                                            }
                                                        }
                                                );
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
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && TriggerConditions.weaponJustFired(game, effectResult, Filters.and(Filters.your(self), Filters.weapon))) {
            FiredWeaponResult weaponFiredResult = (FiredWeaponResult) effectResult;
            PhysicalCard weaponCard = (weaponFiredResult.getPermanentWeaponFired() != null) ? weaponFiredResult.getPermanentWeaponFired().getPhysicalCard(game) : weaponFiredResult.getWeaponCardFired();
            if (weaponCard != null) {
                final float weaponDestinyNumber = game.getModifiersQuerying().getDestiny(game.getGameState(), weaponCard);

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add " + GuiUtils.formatAsString(weaponDestinyNumber) + " to total power");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new ModifyTotalPowerUntilEndOfBattleEffect(action, weaponDestinyNumber, playerId,
                                                "Adds " + GuiUtils.formatAsString(weaponDestinyNumber) + " to total power"));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}