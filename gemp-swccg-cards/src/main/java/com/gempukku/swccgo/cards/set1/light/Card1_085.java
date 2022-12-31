package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfWeaponFiringModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Hear Me Baby, Hold Together
 */
public class Card1_085 extends AbstractUsedInterrupt {
    public Card1_085() {
        super(Side.LIGHT, 5, "Hear Me Baby, Hold Together", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Smuggler and Rebel starships use black market armor plating and deflector shields to withstand enemy fire. Expensive but life-saving modifications.");
        setGameText("If opponent just targeted your starship with a starship weapon, subtract 2 from each of that weapon's destiny draws.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.and(Filters.your(self), Filters.starship), Filters.starship_weapon)) {
            WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();
            Filter starshipFilter = Filters.and(Filters.your(self), Filters.starship, Filters.in(weaponFiringState.getTargets()));
            final Filter starshipWeaponFilter = Filters.sameCardId(weaponFiringState.getCardFiring());
            if (GameConditions.canTarget(game, self, starshipFilter)
                    && GameConditions.canTarget(game, self, starshipWeaponFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Subtract 2 from weapon destiny draws");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose starship", starshipFilter) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard starshipTargeted) {
                                action.addAnimationGroup(starshipTargeted);
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose starship weapon", starshipWeaponFilter) {
                                            @Override
                                            protected boolean getUseShortcut() {
                                                return true;
                                            }
                                            @Override
                                            protected void cardTargeted(final int targetGroupId2, final PhysicalCard starshipWeaponTargeted) {
                                                action.addAnimationGroup(starshipWeaponTargeted);
                                                // Allow response(s)
                                                action.allowResponses("Subtract 2 from " + GameUtils.getCardLink(starshipWeaponTargeted) + "'s weapon destiny draws targeting " + GameUtils.getCardLink(starshipTargeted),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Get the final targeted card(s)
                                                                PhysicalCard finalStarshipWeapon = action.getPrimaryTargetCard(targetGroupId2);
                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new AddUntilEndOfWeaponFiringModifierEffect(action,
                                                                                new EachWeaponDestinyModifier(self, Filters.samePermanentCardId(finalStarshipWeapon), -2),
                                                                                "Subtracts 2 from weapon destiny draws"));
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
}