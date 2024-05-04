package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelWeaponTargetingEffect;
import com.gempukku.swccgo.cards.effects.RetargetWeaponEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Jabba's Palace
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Blaster Deflection
 */
public class Card6_061 extends AbstractUsedOrLostInterrupt {
    public Card6_061() {
        super(Side.LIGHT, 4, Title.Blaster_Deflection, Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setLore("A Jedi can anticipate the actions of his opponent and let the Force control his actions, causing him to effortlessly deflect an opponent's attacks.");
        setGameText("USED: Cancel an attempt to use a character weapon to target your character of ability > 4. LOST: If your character of ability > 4 with a lightsaber was just targeted by a blaster, use 3 Force to re-target that blaster to an opponent's character present.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.and(Filters.your(self), Filters.character, Filters.abilityMoreThan(4)), Filters.character_weapon)) {

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
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.and(Filters.your(self), Filters.character_with_a_lightsaber, Filters.abilityMoreThan(4)), Filters.blaster)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)) {
            final WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();
            final Collection<PhysicalCard> oldTargets = weaponFiringState.getTargets();
            Filter retargetFilter = Filters.none;
            for (PhysicalCard originalTarget : oldTargets) {
                retargetFilter = Filters.or(retargetFilter, Filters.and(Filters.present(originalTarget), Filters.weaponMayRetargetTo(originalTarget)));
            }
            final PhysicalCard weapon = weaponFiringState.getPermanentWeaponFiring() != null ? weaponFiringState.getCardFiringWeapon() : weaponFiringState.getCardFiring();
            Filter filter = Filters.and(Filters.opponents(self), Filters.character, retargetFilter);
            if (GameConditions.canTarget(game, self, filter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Re-target blaster");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose character to re-target from", Filters.in(oldTargets)) {
                            @Override
                            protected void cardSelected(final PhysicalCard oldTarget) {
                                Filter filter = Filters.and(Filters.opponents(self), Filters.character, Filters.present(oldTarget), Filters.weaponMayRetargetTo(oldTarget));
                                // Choose target(s)
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose character to re-target to", filter) {
                                            @Override
                                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                                action.addAnimationGroup(targetedCard);
                                                // Pay cost(s)
                                                action.appendCost(
                                                        new UseForceEffect(action, playerId, 3));
                                                // Allow response(s)
                                                action.allowResponses("Re-target " + GameUtils.getCardLink(weapon) + " to " + GameUtils.getCardLink(targetedCard),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                                // This needs to be done in case the target(s) were changed during the responses.
                                                                PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new RetargetWeaponEffect(action, oldTarget, finalTarget));
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }
}