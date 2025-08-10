package com.gempukku.swccgo.cards.set225.dark;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnForcePileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

/**
 * Set: Set 25
 * Type: Effect
  * Title: No Bargain (V)
 */
public class Card225_004 extends AbstractNormalEffect {
    public Card225_004() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.No_Bargain, Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("Zeet tu seet. Jabba no tuzindy honkabee.");
        setGameText("Deploy on Audience Chamber. May [download] Salacious Crumb here. Once per turn, may choose: Raise your converted Audience Chamber to the top or, if opponent just deployed a character here, place a card from hand on your Force Pile. [Immune to Alter.]");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.SPECIAL_EDITION, Icon.JABBAS_PALACE, Icon.VIRTUAL_SET_25);
        addImmuneToCardTitle(Title.Alter);
        setVirtualSuffix(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Audience_Chamber;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        
        GameTextActionId gameTextActionId = GameTextActionId.NO_BARGAIN__DOWNLOAD_SALACIOUS_CRUMB;
        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Salacious_Crumb)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy Salacious Crumb here from Reserve Deck");

            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.title(Title.Salacious_Crumb), Filters.here(self), true));

            actions.add(action);
        }
        
        Filter raisableAudienceChamber = Filters.and(Filters.canBeConvertedByRaisingYourLocationToTop(playerId), Filters.Audience_Chamber);

        // Intentionally shared gameTextActionId with the hand-to-Force-Pile action
        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        // Check condition(s)
        if (GameConditions.canTarget(game, self, raisableAudienceChamber)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            final PhysicalCard audienceChamberCard = Filters.findFirstActive(game, self, raisableAudienceChamber);
            if (audienceChamberCard != null) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Raise Audience Chamber to the top");
                action.setActionMsg("Raise converted Audience Chamber to the top");
                action.addAnimationGroup(audienceChamberCard);
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new ConvertLocationByRaisingToTopEffect(action, audienceChamberCard, true));
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {

        // Intentionally shared gameTextActionId with the Raise-Audience-Chamber action
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        // Check condition(s)
        if(TriggerConditions.justDeployedToLocation(game, effectResult, game.getOpponent(playerId), Filters.character, Filters.hasAttached(self))
                && GameConditions.hasHand(game, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            PhysicalCard cardDeployed = playCardResult.getPlayedCard();

            if (GameConditions.canTarget(game, self, cardDeployed)) {
                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Place card from hand on Force Pile");
                action.setActionMsg("Place a card from hand on Force Pile");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(new PutCardFromHandOnForcePileEffect(action, playerId));

                return Collections.singletonList(action);
            }
        }
        return null;
    }
}