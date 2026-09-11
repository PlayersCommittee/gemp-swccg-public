package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Innocent Scoundrel
 */
public class Card5_053 extends AbstractUsedOrLostInterrupt {
    public Card5_053() {
        super(Side.LIGHT, 3, Title.Innocent_Scoundrel, Uniqueness.UNRESTRICTED, ExpansionSet.CLOUD_CITY, Rarity.U);
        setLore("'Well, that was a long time ago. I'm sure he's forgotten about that.' Since he needed Calrissian's help, Han regretted having introduced Lando to the Tonnika sisters.");
        setGameText("USED: If your gambler was just targeted by a weapon, opponent must choose to select a new target or lose 2 Force. LOST: Cancel any Effect (except those immune to Alter) deployed on Han or your Lando.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, final SwccgGame game, final Effect effect, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        final Filter yourGambler = Filters.and(Filters.your(self), Filters.gambler);

        // USED: If your gambler was just targeted by a weapon, opponent must choose to select a new target or lose 2 Force.
        if (TriggerConditions.isTargetedByWeapon(game, effect, yourGambler, Filters.weapon)) {
            final WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();
            if (weaponFiringState == null) {
                return null;
            }
            final Collection<PhysicalCard> originalTargets = weaponFiringState.getTargets();
            final PhysicalCard oldTarget = Filters.findFirstActive(game, self, Filters.and(yourGambler, Filters.in(originalTargets)));
            if (oldTarget == null) {
                return null;
            }

            final Filter retargetFilter = Filters.weaponMayRetargetTo(oldTarget);
            final boolean canRetarget = GameConditions.canTarget(game, self, retargetFilter);

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Make opponent retarget or lose 2 Force");
            // Allow response(s)
            action.allowResponses("Make opponent select a new weapon target or lose 2 Force",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            if (canRetarget) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, opponent,
                                                new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Select a new target", "Lose 2 Force"}) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        if (index == 0) {
                                                            game.getGameState().sendMessage(opponent + " chooses to select a new target");
                                                            action.appendEffect(
                                                                    new TargetCardOnTableEffect(action, opponent, "Select new target for weapon", retargetFilter) {
                                                                        @Override
                                                                        protected void cardTargeted(final int targetGroupId, PhysicalCard newTarget) {
                                                                            action.addAnimationGroup(newTarget);
                                                                            action.appendEffect(
                                                                                    new RetargetWeaponEffect(action, oldTarget, newTarget));
                                                                        }
                                                                    }
                                                            );
                                                        } else {
                                                            game.getGameState().sendMessage(opponent + " chooses to lose 2 Force");
                                                            action.appendEffect(
                                                                    new LoseForceEffect(action, opponent, 2));
                                                        }
                                                    }
                                                }
                                        )
                                );
                            } else {
                                // No legal retarget — opponent is forced to lose 2 Force
                                game.getGameState().sendMessage(opponent + " cannot select a new target and must lose 2 Force");
                                action.appendEffect(
                                        new LoseForceEffect(action, opponent, 2));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // LOST: Cancel any Effect (except those immune to Alter) deployed on Han or your Lando
        Filter effectFilter = Filters.and(Filters.Effect, Filters.not(Filters.immune_to_Alter),
                Filters.attachedTo(Filters.or(Filters.Han, Filters.and(Filters.your(self), Filters.Lando))));

        if (GameConditions.canTargetToCancel(game, self, effectFilter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            CancelCardActionBuilder.buildCancelCardAction(action, effectFilter, "Effect deployed on Han or your Lando");
            actions.add(action);
        }
        return actions;
    }
}
