package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: For the Republic!
 */
public class Card215_010 extends AbstractUsedOrLostInterrupt {
    public Card215_010() {
        super(Side.LIGHT, 5, "For the Republic!", Uniqueness.UNIQUE);
        setLore("");
        setGameText("USED: Deploy Cloning Cylinders for free from hand (or Reserve Deck; reshuffle). " +
                "LOST: If a battle was just initiated at a site, each of your clones present is power +1 (+2 and immune to attrition if a Jedi there) for remainder of turn.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_15);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final Filter filter = Filters.and(Filters.your(self), Filters.clone, Filters.presentInBattle);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.site)
                && GameConditions.isDuringBattleWithParticipant(game, filter)) {

            final int modifierAmount = (GameConditions.isDuringBattleWithParticipant(game, Filters.Jedi) ? 2 : 1);

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Add power and immunity to your clones");
            // Allow response(s)
            action.allowResponses("Make clones present power +" + modifierAmount + " and immune to attrition",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            final Collection<PhysicalCard> clones = Filters.filterActive(game, self, filter);
                            if (!clones.isEmpty()) {

                                // Perform result(s)
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                new PowerModifier(self, Filters.in(clones), modifierAmount),
                                                "Makes " + GameUtils.getAppendedNames(clones) + " power +" + modifierAmount));
                                action.appendEffect(
                                        new AddUntilEndOfTurnModifierEffect(action,
                                                new ImmuneToAttritionModifier(self, Filters.in(clones)),
                                                "Makes " + GameUtils.getAppendedNames(clones) + " immune to attrition"));
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
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.FOR_THE_REPUBLIC__DEPLOY_CLONING_CYLINDERS_FROM_HAND_OR_RESERVE_DECK;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)) {

            final Filter cloningCylinders = Filters.title(Title.Cloning_Cylinders);

            if (GameConditions.hasInHand(game, playerId, Filters.and(cloningCylinders, Filters.deployable(self, null, true, 0)))) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
                action.setText("Deploy Cloning Cylinders from hand");
                action.allowResponses("Deploy Cloning Cylinders from hand",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(
                                        new DeployCardFromHandEffect(action, playerId, cloningCylinders, true));
                            }
                        });
                actions.add(action);
            }

            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Cloning_Cylinders)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
                action.setText("Deploy Cloning Cylinders from Reserve Deck");
                // Allow response(s)
                action.allowResponses("Deploy Cloning Cylinders from Reserve Deck",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new DeployCardFromReserveDeckEffect(action, cloningCylinders, true, true));
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }
}
