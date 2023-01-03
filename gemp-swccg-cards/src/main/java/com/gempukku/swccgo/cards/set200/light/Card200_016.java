package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromOutsideDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveGameTextCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Character
 * Subtype: Alien
 * Title: The Mythrol
 */
public class Card200_016 extends AbstractAlien {
    public Card200_016() {
        super(Side.LIGHT, 0, 0, 0, 0, 0, Title.The_Mythrol, Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setGameText("The Mythrol's game text may not be canceled. If about to leave table, place out of play. " +
                "Plays only during start of game by revealing from outside your deck to replace a just-deployed Jabba's Prize imprisoned in Security Tower. If not revealed, place this card under your Starting Effect. " +
                "[Set 1] Despair targets The Mythrol instead of Jabba's Prize. Cancels Stunning Leader here. If just released, either flip this card or place it out of play.");
        addIcons(Icon.VIRTUAL_SET_0);
        setSpecies(Species.MYTHROL);
        setMayNotBePlacedInReserveDeck(true);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.isDuringStartOfGame(game);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredOutsideOfDeckAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();

        // Check condition(s)

        if (TriggerConditions.isStartingLocationsAndObjectivesCompletedStep(game, effectResult)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect))) {

            final PhysicalCard startingEffect = Filters.findFirstFromAllOnTable(game, Filters.and(Filters.your(self), Filters.Starting_Effect));

            if (startingEffect != null) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Stack on " + GameUtils.getCardLink(startingEffect));
                action.setActionMsg("Stack " + GameUtils.getCardLink(self) + " on " + GameUtils.getCardLink(startingEffect));
                // Perform result(s)
                action.appendEffect(
                        new StackCardFromOutsideDeckEffect(action, playerId, startingEffect, self));
                return Collections.singletonList(action);
            }
        }

        String opponent = game.getOpponent(playerId);
        if (TriggerConditions.justDeployed(game, effectResult, opponent, Filters.Jabbas_Prize)) {
            final PhysicalCard securityTower = Filters.findFirstFromTopLocationsOnTable(game, Filters.Security_Tower);
            if (securityTower != null) {
                final PhysicalCard opponentsJabbasPrize = Filters.findFirstFromAllOnTable(game, Filters.and(Filters.opponents(self), Filters.Jabbas_Prize, Filters.at(securityTower)));
                if (opponentsJabbasPrize != null) {

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Deploy The Mythrol");
                    // Perform result(s)
                    action.appendEffect(new PlayoutDecisionEffect(action, playerId, new YesNoDecision("Deploy The Mythrol?"){
                        @Override
                        protected void yes() {
                            action.appendEffect(
                                    new PlaceCardOutOfPlayFromTableEffect(action, opponentsJabbasPrize));
                            action.appendEffect(
                                    new PassthruEffect(action) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            PlayCardAction deployAction = self.getBlueprint().getPlayCardAction(playerId, game, self, self, true, 0, null, null,
                                                    DeployAsCaptiveOption.deployAsImprisonedFrozenCaptive(), null, null, false, 0, Filters.sameLocationId(securityTower), null);
                                            action.appendEffect(
                                                    new StackActionEffect(action, deployAction));
                                        }
                                    }
                            );
                        }
                    }));

                    return Collections.singletonList(action);
                }
            }
        }


        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersAlwaysWhenInPlay(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, self)
                && !TriggerConditions.isAboutToBePlacedOutOfPlayFromTable(game, effectResult, self)) {
            final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(self);
                            for (PhysicalCard attachedCards : game.getGameState().getAllAttachedRecursively(self)) {
                                result.getPreventableCardEffect().preventEffectOnCard(attachedCards);
                            }
                        }
                    });
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.released(game, effectResult, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Flip or place out of play");
            action.setActionMsg("Flip " + GameUtils.getCardLink(self) + " or place it out of play");

            final String FLIP = "Flip";
            final String OOP = "Place out of play";

            String[] possibleResults = {FLIP, OOP};
            String playerId = self.getOwner();

            action.appendEffect(new PlayoutDecisionEffect(action, playerId, new MultipleChoiceAwaitingDecision("Choose the fate of "+GameUtils.getCardLink(self), possibleResults) {
                @Override
                protected void validDecisionMade(int index, String result) {
                    if (FLIP.equals(result)) {
                        action.appendEffect(
                                new FlipCardEffect(action, self));
                    } else {
                        action.appendEffect(
                                new PlaceCardOutOfPlayFromTableEffect(action, self));
                    }
                }
            }));
            return Collections.singletonList(action);
        }

        return null;
    }


    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggersWhenInactiveInPlay(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Stunning_Leader)
                && GameConditions.isDuringBattleAt(game, Filters.here(self))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotHaveGameTextCanceledModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Icon.VIRTUAL_SET_1, Filters.Despair), ModifyGameTextType.THE_MYTHROL__DESPAIR_V_TARGETS_THE_MYTHROL_INSTEAD_OF_JABBAS_PRIZE));
        return modifiers;
    }
}
