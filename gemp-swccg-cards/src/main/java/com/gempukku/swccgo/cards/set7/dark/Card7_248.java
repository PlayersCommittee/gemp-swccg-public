package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.InsteadOfFiringWeaponEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Coordinated Attack
 */
public class Card7_248 extends AbstractUsedOrLostInterrupt {
    public Card7_248() {
        super(Side.DARK, 2, "Coordinated Attack", Uniqueness.UNIQUE);
        setLore("'Stay in attack formation.'");
        setGameText("USED: During a battle at a system or sector, instead of firing one of your starship weapons at a target, reduce that target's power by 4 until end of turn. LOST: During a battle at a system or sector, use 3 Force to cancel one battle destiny just drawn.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.system_or_sector)) {
            List<PhysicalCard> validStarshipWeapons = new LinkedList<PhysicalCard>();
            final Collection<PhysicalCard> cardsInBattle = Filters.filterActive(game, self, Filters.participatingInBattle);
            for (PhysicalCard cardInBattle : cardsInBattle) {
                if (Filters.and(Filters.your(self), Filters.starship_weapon, Filters.canBeFiredForFreeAt(self, 0, Filters.in(cardsInBattle))).accepts(game, cardInBattle)) {
                    validStarshipWeapons.add(cardInBattle);
                }
            }
            if (!validStarshipWeapons.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Make a target power -4");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose starship weapon", Filters.in(validStarshipWeapons)) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard weapon) {
                                List<PhysicalCard> validTargets = new LinkedList<PhysicalCard>();
                                for (PhysicalCard cardInBattle : cardsInBattle) {
                                    if (Filters.canBeFiredForFreeAt(self, 0, Filters.sameCardId(cardInBattle)).accepts(game, weapon)) {
                                        validTargets.add(cardInBattle);
                                    }
                                }
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose target", Filters.in(validTargets)) {
                                            @Override
                                            protected void cardTargeted(final int targetGroupId2, PhysicalCard target) {
                                                action.addAnimationGroup(weapon);
                                                action.addAnimationGroup(target);
                                                // Allow response(s)
                                                action.allowResponses("Reduce " + GameUtils.getCardLink(target) + "'s power by 4 instead of firing " + GameUtils.getCardLink(weapon),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                                // This needs to be done in case the target(s) were changed during the responses.
                                                                PhysicalCard finalWeapon = action.getPrimaryTargetCard(targetGroupId1);
                                                                PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId2);

                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new InsteadOfFiringWeaponEffect(action, finalWeapon,
                                                                                new ModifyPowerUntilEndOfTurnEffect(action, finalTarget, -4)));
                                                            }
                                                        }
                                                );
                                            }
                                        });
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawn(game, effectResult)
                && GameConditions.isDuringBattleAt(game, Filters.system_or_sector)
                && GameConditions.canCancelDestiny(game, playerId)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Cancel battle destiny");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelDestinyEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}