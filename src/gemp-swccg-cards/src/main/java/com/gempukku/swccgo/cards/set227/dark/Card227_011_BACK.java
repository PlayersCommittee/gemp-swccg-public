package com.gempukku.swccgo.cards.set227.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
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
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsIfFromHandModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ConvertLocationResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 27
 * Type: Objective
 * Title: More Systems Will Rally To Our Cause / The Galaxy Torn Apart
 */
public class Card227_011_BACK extends AbstractObjective {
    public Card227_011_BACK() {
        super(Side.DARK, 7, "The Galaxy Torn Apart", ExpansionSet.SET_27, Rarity.V);
        setGameText("May immediately retrieve up to 2 Force. While this side up, during your control phase, opponent loses X Force, where X = number of systems you control (limit 3). Once per turn, may deploy a (••• •••) card stacked here as if from hand. If you just lost a battle involving a (••• •••) card, opponent loses 1 Force. Once per turn, if your unique (•) character was just lost, may [upload] a unique (•) character. Flip this card at the end of turn if your [Clone Army] system is on table.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_27);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Filters.not(Icon.EPISODE_I), Filters.or(Filters.character, Filters.starship, Filters.vehicle)), self.getOwner()));
        modifiers.add(new MayDeployAsIfFromHandModifier(self, Filters.and(Filters.restricted_6, Filters.stackedOn(self, Filters.Droid_Racks))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        // List of action IDs
        // MORE_SYSTEMS_WILL_RALLY_TO_OUR_CAUSE__DEPLOY_BATTLEGROUND_SITE - Deploy site from Reserve Deck (from 0-side)
        // THE_GALAXY_TORN_APART__UPLOAD_UNIQUE_CHARACTER - Upload unique character
        // OTHER_CARD_ACTION_1 - Retrieve if you just flipped
        // OTHER_CARD_ACTION_2 - Control phase damage (used twice: top-level and required response)
        // OTHER_CARD_ACTION_3 - Deploy a 6-dot card from here
        // OTHER_CARD_ACTION_4 - Force loss after a losing battle
        // OTHER_CARD_ACTION_5 - Flip
        // OTHER_CARD_ACTION_6 - Auto-raise converted system  (from 0-side)

        String opponent = game.getOpponent(playerId);

        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.MORE_SYSTEMS_WILL_RALLY_TO_OUR_CAUSE__DEPLOY_BATTLEGROUND_SITE;
        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
            && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            Filter systemFilter = Filters.and(Filters.your(self), Filters.or(Icon.CLONE_ARMY, Icon.SEPARATIST), Filters.system);

            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Separatist_Command_Center)
                || GameConditions.canSpotLocation(game, systemFilter)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy a site from Reserve Deck");
                action.setActionMsg("Deploy Invisible Hand: Bridge, Separatist Command Center, or a battleground site related to your [Separatist] or [Clone Army] system from Reserve Deck");

                Filter siteFilter = Filters.none;
                for(PhysicalCard system: Filters.filterTopLocationsOnTable(game, systemFilter)) {
                    siteFilter = Filters.or(siteFilter, Filters.relatedLocationEvenWhenNotInPlay(system));
                }

                // Update usage limit(s)
                action.appendUsage(
                    new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Invisible_Hand_Bridge, Filters.Separatist_Command_Center, Filters.and(Filters.site, siteFilter)), Filters.or(Filters.Invisible_Hand_Bridge, Filters.Separatist_Command_Center, Filters.battleground), true));
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2; //Control phase damage (used twice: top-level and required response)
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            int numForce = Filters.countTopLocationsOnTable(game, Filters.and(Filters.system, Filters.controls(playerId)));
            if (numForce > 3) {
                numForce = 3;
            }
            if (numForce > 0) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make opponent lose " + numForce + " Force");
                // Update usage limit(s)
                action.appendUsage(
                    new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                    new LoseForceEffect(action, opponent, numForce));
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3; //Deploy a 6-dot card from here
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
            && GameConditions.hasStackedCards(game, self, Filters.and(Filters.restricted_6, Filters.deployable(self, null, false, 0)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a card stacked here");
            action.setActionMsg("Deploy a (••• •••) card stacked on " + GameUtils.getCardLink(self));
            // Update usage limit(s)
            action.appendUsage(
                new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                new DeployStackedCardEffect(action, self, Filters.restricted_6, false));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2; //Control phase damage (used twice: top-level and required response)
        // Check condition(s)
        // Check if reached end of each control phase and action was not performed yet.
        if (TriggerConditions.isEndOfYourPhase(game, effectResult, Phase.CONTROL, playerId)
            && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {

            int numForce = Filters.countTopLocationsOnTable(game, Filters.and(Filters.system, Filters.controls(playerId)));
            if (numForce > 3) {
                numForce = 3;
            }
            if (numForce > 0) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setPerformingPlayer(playerId);
                action.setText("Make opponent lose " + numForce + " Force");
                // Perform result(s)
                action.appendEffect(
                    new LoseForceEffect(action, opponent, numForce));
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_4; //Force loss after a losing battle
        // Check condition(s)
        if (TriggerConditions.lostBattle(game, effectResult, playerId)
            && GameConditions.canSpot(game, self, Filters.and(Filters.restricted_6, Filters.participatingInBattle))) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make opponent lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                new LoseForceEffect(action, opponent, 1));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_5; //Flip
        // Check condition(s)
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)
            && GameConditions.canBeFlipped(game, self)
            && GameConditions.canSpot(game, self, 1, Filters.and(Filters.your(playerId), Icon.CLONE_ARMY, Filters.system))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                new FlipCardEffect(action, self));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_6; //Auto-raise converted system  (from 0-side)
        //Check condition(s)
        if (TriggerConditions.justConvertedLocation(game, effectResult)) {
            PhysicalCard newLocation = ((ConvertLocationResult)effectResult).getNewLocation();
            PhysicalCard oldLocation = ((ConvertLocationResult)effectResult).getOldLocation();
            if (oldLocation != null
                && Filters.and(Filters.your(self.getOwner()), Icon.EPISODE_I, Filters.system).accepts(game, oldLocation)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Raise converted location to top");
                action.setPerformingPlayer(self.getOwner());
                action.appendEffect(
                    new ConvertLocationByRaisingToTopEffect(action, newLocation, true));
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        GameTextActionId  gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1; //Retrieve if you just flipped
        // Check condition(s)
        if (TriggerConditions.cardFlipped(game, effectResult, self)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve up to 2 Force");
            action.appendEffect(
                new PlayoutDecisionEffect(action, playerId,
                    new IntegerAwaitingDecision("Choose amount of Force to retrieve", 1, 2, 2) {
                        @Override
                        public void decisionMade(final int amountToRetrieve) throws DecisionResultInvalidException {
                            GameState gameState = game.getGameState();
                            gameState.sendMessage(playerId + " chooses to retrieve " + amountToRetrieve + " Force");
                            action.appendEffect(
                                new RetrieveForceEffect(action, playerId, amountToRetrieve));
                        }
                    }
                )
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.THE_GALAXY_TORN_APART__UPLOAD_UNIQUE_CHARACTER; //Upload unique character
        //Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.your(self), Filters.unique, Filters.character))
            && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
            && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take a unique character into hand");
            action.setText("Take a unique (•) character into hand from Reserve Deck");

            // Update usage limit(s)
            action.appendUsage(
                new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.unique, Filters.character), true));

            actions.add(action);
        }

        return actions;
    }
}
