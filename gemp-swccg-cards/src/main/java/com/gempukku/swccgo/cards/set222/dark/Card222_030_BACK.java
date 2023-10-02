package com.gempukku.swccgo.cards.set222.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtAndReorderTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.LostInterruptModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Objective
 * Title: The Shield Will Be Down In Moments / Imperial Troops Have Entered The Base! (AI)
 */
public class Card222_030_BACK extends AbstractObjective {
    public Card222_030_BACK() {
        super(Side.DARK, 7, Title.Imperial_Troops_Have_Entered_The_Base, ExpansionSet.SET_22, Rarity.V);
        setAlternateImageSuffix(true);
        setGameText("While this side up, attrition against opponent is +1 for each Imperial leader in battle. Rebel Leadership and We're Doomed are Lost Interrupts. Once per turn, may deploy a snowtrooper from Reserve Deck; reshuffle. Once during your control phase, may choose: retrieve bottom card of Lost Pile into hand or peek at X cards from the top of opponent's Reserve Deck, where X = number of Hoth locations you control; replace in any order. " +
                "Place out of play if you do not occupy a Hoth site with an AT-AT, Imperial leader, or snowtrooper.");
        addIcons(Icon.HOTH, Icon.VIRTUAL_SET_22);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new LostInterruptModifier(self, Filters.or(Filters.title("Rebel Leadership"), Filters.Were_Doomed)));
        modifiers.add(new AttritionModifier(self, new InBattleCondition(self, Filters.Imperial_leader), new OnTableEvaluator(self, Filters.and(Filters.participatingInBattle, Filters.Imperial_leader)), opponent));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.IMPERIAL_TROOPS_HAVE_ENTERED_THE_BASE__CONTROL_PHASE_ACTION;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.hasLostPile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve bottom card of Lost Pile into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardIntoHandEffect(action, playerId, Filters.bottomOfLostPile(playerId)));

            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.hasReserveDeck(game, opponent)) {

            int x = (int) game.getModifiersQuerying().getVariableValue(game.getGameState(), self, Variable.X, Filters.countTopLocationsOnTable(game, Filters.and(Filters.Hoth_location, Filters.controls(playerId))));

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Peek at top " + x + " cards of opponent's Reserve Deck");
            action.setActionMsg("Peek at top " + x + " cards of opponent's Reserve Deck and replace them in any order");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PeekAtAndReorderTopCardsOfReserveDeckEffect(action, opponent, x));

            actions.add(action);
        }


        // Once per turn, may deploy a snowtrooper from Reserve Deck; reshuffle.
        gameTextActionId = GameTextActionId.IMPERIAL_TROOPS_HAVE_ENTERED_THE_BASE__DEPLOY_SNOWTROOPER;
        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a snowtrooper from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.snowtrooper, true));

            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBePlacedOutOfPlay(game, self)
                && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.Hoth_site, Filters.occupiesWith(playerId, self, Filters.or(Filters.and(Filters.AT_AT, Filters.piloted), Filters.Imperial_leader, Filters.snowtrooper))))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
