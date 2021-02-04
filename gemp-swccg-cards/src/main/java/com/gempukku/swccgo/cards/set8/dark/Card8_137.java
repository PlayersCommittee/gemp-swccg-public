package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.MayNotUseWeaponsModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.FiredWeaponResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used
 * Title: Compact Firepower
 */
public class Card8_137 extends AbstractUsedInterrupt {
    public Card8_137() {
        super(Side.DARK, 4, "Compact Firepower", Uniqueness.UNIQUE);
        setLore("The small blasters used by biker scouts can be fired when piloting a vehicle at high speed.");
        setGameText("If your warrior just fired a DH-17 blaster or scout blaster during a battle, draw destiny: (0) no effect, (1-3) target may not use weapons this turn, (4+) target is power and forfeit = 0 this battle.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && TriggerConditions.weaponJustFiredBy(game, effectResult, Filters.or(Filters.DH17_blaster, Filters.scout_blaster), Filters.and(Filters.your(self), Filters.warrior))) {
            FiredWeaponResult firedWeaponResult = (FiredWeaponResult) effectResult;

            final Collection<PhysicalCard> targets = firedWeaponResult.getTargets();
            final Filter targetFilter = Filters.in(targets);
            if (GameConditions.canTarget(game, self, targetFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Draw destiny");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose target", targetFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Draw destiny while targeting " + GameUtils.getCardLink(targetedCard),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new DrawDestinyEffect(action, playerId) {
                                                            @Override
                                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                GameState gameState = game.getGameState();
                                                                if (totalDestiny == null) {
                                                                    gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                    return;
                                                                }

                                                                if (totalDestiny >= 1 && totalDestiny <= 3) {
                                                                    gameState.sendMessage("Result: Destiny between 1 and 3");
                                                                    action.appendEffect(
                                                                            new AddUntilEndOfTurnModifierEffect(action,
                                                                                    new MayNotUseWeaponsModifier(self, finalTarget),
                                                                                    GameUtils.getAppendedNames(targets) + " may not use weapons"));
                                                                } else if (totalDestiny >= 4) {
                                                                    gameState.sendMessage("Result: Destiny is 4 or greater");
                                                                    action.appendEffect(
                                                                            new ResetPowerAndForfeitUntilEndOfBattleEffect(action, finalTarget, 0));
                                                                } else {
                                                                    gameState.sendMessage("Result: No effect");
                                                                }
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );
                            }
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}