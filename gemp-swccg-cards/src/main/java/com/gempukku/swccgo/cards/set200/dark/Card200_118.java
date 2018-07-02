package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeFiredTwicePerBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Set 0
 * Type: Interrupt
 * Subtype: Used
 * Title: Defensive Fire (V)
 */
public class Card200_118 extends AbstractUsedInterrupt {
    public Card200_118() {
        super(Side.DARK, 3, Title.Defensive_Fire, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("275 gunners manning 60 turbolaser batteries provide a wide firing arc. Even so, asteroids are a challenge due to the sluggish recharge rates of the high-powered blasters.");
        setGameText("If opponent just initiated battle, immediately fire a non-lightsaber, non-[Permanent Weapon] weapon present (for free and each weapon destiny is +2). That weapon may fire again this battle.");
        addIcons(Icon.DAGOBAH, Icon.VIRTUAL_SET_0);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, opponent)) {
            Filter weaponFilter = Filters.and(Filters.your(self), Filters.weapon, Filters.not(Filters.or(Filters.lightsaber, Icon.PERMANENT_WEAPON)),
                    Filters.presentInBattle, Filters.canBeFiredForFreeAt(self, 0, Filters.canBeTargetedBy(self)));
            if (GameConditions.canSpot(game, self, weaponFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Fire a weapon");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose weapon to fire", weaponFilter) {
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
                                                            protected List<Modifier> getWeaponFiringModifiers(String playerId, SwccgGame game, PhysicalCard weapon) {
                                                                Modifier modifier = new EachWeaponDestinyModifier(self, weapon, 2);
                                                                return Collections.singletonList(modifier);
                                                            }
                                                        }
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
        }
        return null;
    }
}