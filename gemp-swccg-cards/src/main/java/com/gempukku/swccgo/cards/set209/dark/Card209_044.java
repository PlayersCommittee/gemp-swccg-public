package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Effect
 * Title: They Will Be No Match For You (V)
 */
public class Card209_044 extends AbstractNormalEffect {
    public Card209_044() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.They_Will_Be_No_Match_For_You, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("'At last we will reveal ourselves to the Jedi.'");
        setGameText("Deploy on table. Once per turn, may [download] Maul's Lightsaber. At start of opponent's control phase, may relocate Maul to same site as a Jedi. May place this Effect out of play to deploy Maul's Lightsaber from Lost Pile. Immune to Alter.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.VIRTUAL_SET_9);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.THEY_WILL_BE_NO_MATCH_FOR_YOU__DOWNLOAD_MAULS_LIGHTSABER;
        GameTextActionId gameTextActionId2 = GameTextActionId.THEY_WILL_BE_NO_MATCH_FOR_YOU__DEPLOY_MAULS_LIGHTSABER_LOST_PILE;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.MAULS_DOUBLE_BLADED_LIGHTSABER)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Maul's Lightsaber from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Mauls_Lightsaber, true));
            actions.add(action);
        }

        if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId2, Persona.MAULS_DOUBLE_BLADED_LIGHTSABER)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Place OOP to deploy Maul's Lightsaber from Lost Pile");
            action.setActionMsg("Deploy Maul's Lightsaber from Lost Pile");
            // Pay cost(s)
            action.appendCost(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromLostPileEffect(action, Filters.Mauls_Lightsaber, false));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        // Card action 1
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        //Need to find Maul
        final PhysicalCard maulCard = Filters.findFirstActive(game, self, Filters.Maul);

        //Need to find Jedi
        final Filter jediSiteFilter = Filters.and(Filters.Jedi, Filters.at(Filters.site));

        // Check condition(s)
        if (TriggerConditions.isStartOfOpponentsPhase(game, self, effectResult, Phase.CONTROL)
                && GameConditions.canTarget(game, self, Filters.Maul)
                && GameConditions.canSpot(game, self, jediSiteFilter)
                && !GameConditions.isAboardAnyStarship(game, maulCard)
                && !GameConditions.isAboard(game, maulCard, Filters.vehicle)) {
            Filter siteFilter = Filters.and(Filters.site, Filters.occupiesWith(game.getOpponent(playerId), self, Filters.Jedi));

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Relocate Maul to same site as a Jedi");

            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate Maul", siteFilter) {
                        @Override
                        protected void cardSelected(final PhysicalCard selectedCard) {
                            action.addAnimationGroup(selectedCard);
                            // Allow response(s)
                            action.allowResponses("Relocate Maul to " + GameUtils.getCardLink(selectedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new RelocateBetweenLocationsEffect(action, maulCard, selectedCard));
                                        }
                                    }
                            );
                        }
                    }
            );

            actions.add(action);
        }

        return actions;
    }

}
