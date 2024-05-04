package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Objective
 * Title: The Galaxy May Need A Legend / We Need Luke Skywalker
 */
public class Card211_036 extends AbstractObjective {
    public Card211_036() {
        super(Side.LIGHT, 0, Title.The_Galaxy_May_Need_A_Legend, ExpansionSet.SET_11, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Ahch-To system and any [Episode VII] battleground.\n" +
                "For remainder of game, Luke deploys only to Ahch-To. You may not Force drain on Ahch-To. You may not deploy Jedi while a Jedi on table. You may not deploy [Episode I] locations or non-[Episode VII] Luke. Once per game, may take any one card into hand from Force Pile; reshuffle.\n" +
                "While this side up, once per turn, may use 1 Force to [download] an Ahch-To location or [Episode VII] battleground.\n" +
                "May flip this card if Luke on Ahch-To and a battle was just initiated involving a Resistance character.");
        addIcons(Icon.VIRTUAL_SET_11, Icon.EPISODE_VII);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Ahch_To_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Ahch-To System to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.battleground, Icon.EPISODE_VII), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose [Episode VII] battleground to deploy";
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotForceDrainAtLocationModifier(self, Filters.on(Title.Ahch_To), playerId), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployModifier(self, Filters.or(Filters.and(Filters.location, Icon.EPISODE_I), Filters.and(Filters.Luke, Filters.not(Icon.EPISODE_VII))), playerId), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployModifier(self, Filters.Jedi, new OnTableCondition(self, Filters.Jedi), playerId), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployToLocationModifier(self, Filters.Luke, Filters.not(Filters.AhchTo_location)), null));
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId1 = GameTextActionId.THE_GALAXY_MAY_NEED_A_LEGEND_FORCE_PILE_UPLOAD;
        GameTextActionId gameTextActionId2 = GameTextActionId.THE_GALAXY_MAY_NEED_A_LEGEND_DEPLOY_AHCH_TO_LOCATION;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId1)
                && GameConditions.canTakeCardsIntoHandFromForcePile(game, playerId, self, gameTextActionId1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId1);
            action.setText("Take card into hand from Force Pile");
            action.setActionMsg("Take a card into hand from Force Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromForcePileEffect(action, playerId, true));
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            self.setWhileInPlayData(new WhileInPlayData(true));
                        }
                    });
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId2, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId2)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId2);
            action.setText("Deploy location from Reserve Deck");
            action.setActionMsg("Deploy location from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.AhchTo_location, Filters.and(Filters.battleground, Icon.EPISODE_VII)), true));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter lukeOnAhchTo = Filters.and(Filters.Luke, Filters.on(Title.Ahch_To));

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Resistance_character)
                && GameConditions.canSpot(game, self, lukeOnAhchTo)) {

            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}