package com.gempukku.swccgo.cards.set226.light;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractLostOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LightSideGoesFirstEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * Set: Set 26
 * Type: Interrupt
 * Subtype: Lost or Starting
 * Title: Something About This Boy
 */
public class Card226_027 extends AbstractLostOrStartingInterrupt {
    public Card226_027() {
        super(Side.LIGHT, 3, Title.Something_About_This_Boy, Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setLore("'What'd he mean by that?' 'I'll tell you later.'");
        setGameText("LOST: Relocate Prophecy Of The Force to a site. STARTING: If your starting location was Skywalker Hut, deploy Prophecy Of The Force there, Do, Or Do Not and Jedi Business. Light Side goes first. Place Interrupt in Reserve Deck.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_26);
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        Filter requiredStartingLocation = Filters.Skywalker_Hut;
        
        // Check condition(s)
        final PhysicalCard startingLocation = game.getModifiersQuerying().getStartingLocation(playerId);
        if (startingLocation != null && requiredStartingLocation.accepts(game, startingLocation)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
            action.setText("Deploy Prophecy Of The Force and other cards from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy Prophecy Of The Force, Do, Or Do Not, and Jedi Business from Reserve Deck.",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.Prophecy_Of_The_Force, requiredStartingLocation, true, false));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.Do_Or_Do_Not, true, false));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.Jedi_Business, true, false));
                            action.appendEffect(
                                    new LightSideGoesFirstEffect(action));
                            action.appendEffect(
                                    new PutCardFromVoidInReserveDeckEffect(action, playerId, self));
                        }
                    }
            );
            return action;
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();    

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.Prophecy_Of_The_Force)) {
            final PhysicalCard prophecyOfTheForce = Filters.findFirstActive(game, self, Filters.Prophecy_Of_The_Force);
            boolean canRelocate = GameConditions.canSpot(game, self, Filters.canRelocateEffectTo(playerId, prophecyOfTheForce));
            Collection<Modifier> modifiers = game.getModifiersQuerying().getModifiersAffecting(game.getGameState(), prophecyOfTheForce);
            for (Modifier m : modifiers) {
                if (m.getModifyGameTextType(game.getGameState(), game.getModifiersQuerying(), prophecyOfTheForce) == ModifyGameTextType.PROPHECY_OF_THE_FORCE__MAY_NOT_BE_RELOCATED)
                    canRelocate = false;
            }

            if (canRelocate) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Relocate Prophecy Of The Force");
                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose site", Filters.canRelocateEffectTo(playerId, prophecyOfTheForce)) {
                    @Override
                    protected void cardTargeted(int targetGroupId, PhysicalCard site) {

                        final PhysicalCard finalSite = action.getPrimaryTargetCard(targetGroupId);
                        action.addAnimationGroup(prophecyOfTheForce);
                        action.addAnimationGroup(finalSite);
                        action.allowResponses(new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(
                                        new AttachCardFromTableEffect(action, prophecyOfTheForce, finalSite)
                                );
                            }
                        });
                    }
                });

                actions.add(action);
            }
        }
        return actions;
    }
}
