package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.CapturedOnlyCondition;
import com.gempukku.swccgo.cards.effects.SetForRemainderOfGameDataEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
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
 * Subtype: Rebel
 * Title: Jabba's Prize (V)
 */
public class Card200_016 extends AbstractRebel {
    public Card200_016() {
        super(Side.LIGHT, 0, 0, 0, 0, 0, Title.Jabbas_Prize, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setGameText("Jabba's Prize is a Light Side card and does not count towards your deck limit. Reveal to opponent when deploying your Starting Effect. For remainder of game, you may not deploy [Maintenance] Falcon. Deploys only at start of game if Jabba's Prize is at Security Tower (replaces opponent's Jabba's Prize imprisioned in Security Tower); otherwise place out of play. May not be placed in Reserve Deck. Jabba's Prize is a persona of Corran Horn only while on table. If Jabba's Prize was just released or leaves table, place it out of play. While Jabba's Prize is at Audience Chamber, opponent's battle destiny draws there are +1.");
        setDoesNotCountTowardDeckLimit(true);
        addPersona(Persona.CORRAN_HORN);
        setCharacterPersonaOnlyWhileOnTable(true);
        addIcons(Icon.REFLECTIONS_II, Icon.VIRTUAL_SET_0);
        setMayNotBePlacedInReserveDeck(true);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredOutsideOfDeckBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, playerId, Filters.Starting_Effect)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reveal");
            action.skipInitialMessageAndAnimation();
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new SetForRemainderOfGameDataEffect(action, self, new ForRemainderOfGameData()));
            action.appendEffect(
                    new SendMessageEffect(action, playerId + " reveals " + GameUtils.getCardLink(self)));
            action.appendEffect(
                    new ShowCardOnScreenEffect(action, self));
            action.appendEffect(
                    new AddUntilEndOfGameModifierEffect(action, new MayNotDeployModifier(self, Filters.and(Filters.Falcon, Icon.MAINTENANCE), playerId), null));
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredOutsideOfDeckAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();

        // Check condition(s)
        if (GameConditions.cardHasAnyForRemainderOfGameDataSet(self)) {
            if (TriggerConditions.isStartingLocationsAndObjectivesCompletedStep(game, effectResult)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Place out of play");
                action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
                // Perform result(s)
                action.appendEffect(
                        new PlaceCardOutOfPlayFromOffTableEffect(action, self));
                return Collections.singletonList(action);
            }

            if (TriggerConditions.justDeployed(game, effectResult, Filters.Jabbas_Prize)) {
                final PhysicalCard securityTower = Filters.findFirstFromTopLocationsOnTable(game, Filters.Security_Tower);
                if (securityTower != null) {
                    PhysicalCard opponentsJabbasPrize = Filters.findFirstFromAllOnTable(game, Filters.and(Filters.opponents(self), Filters.Jabbas_Prize, Filters.at(securityTower)));
                    if (opponentsJabbasPrize != null) {

                        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                        action.setText("Deploy");
                        // Perform result(s)
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
                        return Collections.singletonList(action);
                    }
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
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachBattleDestinyModifier(self, Filters.here(self), new AndCondition(new AtCondition(self, Filters.Audience_Chamber), new CapturedOnlyCondition(self)), 1, opponent));
        return modifiers;
    }
}
