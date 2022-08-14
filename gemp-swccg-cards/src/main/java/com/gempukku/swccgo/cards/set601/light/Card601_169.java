package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.RevealTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 6
 * Type: Interrupt
 * Subtype: Lost
 * Title: I've Decided To Go Back (V)
 */
public class Card601_169 extends AbstractLostInterrupt {
    public Card601_169() {
        super(Side.LIGHT, 4, "I've Decided To Go Back", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("'This is your arena. I feel I must return to mine.'");
        setGameText("Use 1 Force to deploy (or retrieve) non-[Virtual] Amidala, Panaka, or Ric Olie from Reserve Deck; reshuffle.  OR  If your ability = 3 character defending a battle alone at a site (except Senate), character is power +2, adds one destiny to power and may draw one battle destiny if not able to otherwise. (Immune to Sense.)");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        Filter nonVirtual = Filters.not(Filters.or(Icon.LEGACY_BLOCK_1, Icon.LEGACY_BLOCK_2, Icon.LEGACY_BLOCK_3, Icon.LEGACY_BLOCK_4, Icon.LEGACY_BLOCK_5, Icon.LEGACY_BLOCK_6, Icon.LEGACY_BLOCK_7, Icon.LEGACY_BLOCK_8, Icon.LEGACY_BLOCK_9, Icon.LEGACY_BLOCK_D));

        final Filter filter = Filters.and(nonVirtual, Filters.or(Filters.Amidala, Filters.Panaka, Filters.Ric));

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__IVE_DECIDED_TO_GO_BACK_V__DEPLOY_OR_RETRIEVE_CARD;

        // Check condition(s)
        if ((GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.AMIDALA)
                || GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.PANAKA)
                || GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.RIC))
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setImmuneTo(Title.Sense);
            action.setText("Deploy a card");
            action.setActionMsg("Deploy non-[Virtual] Amidala, Panaka, or Ric Olie from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses("Deploy non-[Virtual] Amidala, Panaka, or Ric Olie from Reserve Deck" ,
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, filter, true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.hasLostPile(game, playerId)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Sense);
            action.setText("Retrieve a card");
            action.setActionMsg("Retrieve non-[Virtual] Amidala, Panaka, or Ric Olie");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses("Deploy non-[Virtual] Amidala, Panaka, or Ric Olie" ,
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveCardEffect(action, playerId, filter));
                        }
                    }
            );
            actions.add(action);
        }

        // If your ability = 3 character defending a battle alone at a site (except Senate), character is power +2, adds one destiny to power and may draw one battle destiny if not able to otherwise. (Immune to Sense.)
        Filter targetFilter = Filters.and(Filters.your(self), Filters.character, Filters.abilityEqualTo(3), Filters.defendingBattle, Filters.alone);

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.and(Filters.site, Filters.not(Filters.Galactic_Senate)))
                && GameConditions.canTarget(game, self, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Sense);
            action.setText("Make character power +2 and add destiny to total power");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);

                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " power +2, add one destiny to power, and draw battle destiny if unable to otherwise",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AddUntilEndOfBattleModifierEffect(action,
                                                            new PowerModifier(self, finalTarget, 2),
                                                            "Makes " + GameUtils.getCardLink(finalTarget) + " power +2"));

                                            if (GameConditions.canAddDestinyDrawsToPower(game, playerId)) {
                                                action.appendEffect(
                                                        new AddDestinyToTotalPowerEffect(action, 1));
                                            }

                                            action.appendEffect(
                                                    new AddUntilEndOfBattleModifierEffect(action,
                                                            new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, finalTarget, 1),
                                                            "Makes " + GameUtils.getCardLink(finalTarget) + " draw battle destiny if unable to otherwise"));

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
            actions.add(action);
        }
        return actions;
    }
}