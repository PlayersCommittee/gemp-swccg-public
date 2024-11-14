package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelWeaponTargetingEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: The Great Hutt Expansion
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Do A Backflip Derrin
 */
public class Card304_069 extends AbstractUsedOrLostInterrupt {
    public Card304_069() {
        super(Side.DARK, 4, Title.Do_A_Backflip_Derrin, Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Sometimes in life you have to do what you're told. For some that leads to success. For others, it ends face down failing to do a backflip.");
        setGameText("USED: If Thran or a member of Thran's Personal Squad was just targeted by a weapon, cancel the targeting and place weapon (unless a [Permanent Weapon]) in owner's Used Pile. LOST: If one of your [CSP] characters was just targeted by a weapon during a battle, use 2 Force to cancel the targeting.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, final SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.or(Filters.Thran, Filters.THRAN_GUARD), Filters.any)) {
            final PhysicalCard weapon = game.getGameState().getWeaponFiringState().getCardFiring();

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel weapon targeting");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelWeaponTargetingEffect(action));
                            if (weapon != null && Filters.not(Icon.PERMANENT_WEAPON).accepts(game, weapon)) {
                                action.appendEffect(
                                        new PlaceCardInUsedPileFromTableEffect(action, weapon));
                            }
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.and(Filters.your(self), Filters.and(Filters.character, Filters.CSP)), Filters.weapon)
                && GameConditions.isDuringBattle(game)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Cancel weapon targeting");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelWeaponTargetingEffect(action));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}