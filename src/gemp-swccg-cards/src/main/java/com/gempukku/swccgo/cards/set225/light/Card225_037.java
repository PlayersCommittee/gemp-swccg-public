package com.gempukku.swccgo.cards.set225.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RelocateDeviceOrWeaponBetweenCharactersEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInLostPileEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Effect
 * Title: A Good Friend
 */
public class Card225_037 extends AbstractNormalEffect {
    public Card225_037() {
        super(Side.LIGHT, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "A Good Friend", Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("");
        setGameText("If a [Skywalker] Epic Event on table, deploy on table. May [download] Be With Me, Jedi Village, or Leia's Lightsaber. Once per turn, you may relocate Anakin's Lightsaber between Rey and a Skywalker. Once per game, may exchange a Skywalker from hand with Ben Solo in Lost Pile. [Immune to Alter.]");
        addIcons(Icon.EPISODE_VII, Icon.SKYWALKER, Icon.VIRTUAL_SET_25);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.and(Filters.your(self), Icon.SKYWALKER, Filters.Epic_Event));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.A_GOOD_FRIEND__DEPLOY_CARD;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Arrays.asList(Title.Be_With_Me, Title.AhchTo_Jedi_Village, Title.Leias_Lightsaber))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy Be With Me, Jedi Village, or Leia's Lightsaber from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Be_With_Me, Filters.AhchTo_Jedi_Village, Filters.Leias_Lightsaber), true));
            actions.add(action);
        }

        Filter otherSkywalker = Filters.and(Filters.not(Filters.Rey), Filters.Skywalker);
        Filter otherSkywalkerWithSaber = Filters.and(otherSkywalker, Filters.armedWith(Filters.Anakins_Lightsaber));
        Filter reyWithSaber = Filters.and(Filters.Rey, Filters.armedWith(Filters.Anakins_Lightsaber));
        
        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTarget(game, self, Filters.Anakins_Lightsaber)
                && ((GameConditions.canTarget(game, self, reyWithSaber) && GameConditions.canTarget(game, self, otherSkywalker))
                || (GameConditions.canTarget(game, self, otherSkywalkerWithSaber) && GameConditions.canTarget(game, self, Filters.Rey)))) {

            final PhysicalCard cardWithSaber = Filters.findFirstActive(game, self, Filters.armedWith(Filters.Anakins_Lightsaber));
            final PhysicalCard anakinsLightsaber = Filters.findFirstActive(game, self, Filters.Anakins_Lightsaber);

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Relocate Anakin's Lightsaber");
            action.addAnimationGroup(anakinsLightsaber);
            action.appendUsage(
                    new OncePerTurnEffect(action));

            Filter recipientFilter = Filters.any;
            //If Rey has the lightsaber, player will target another Skywalker of their choice
            if (GameConditions.canTarget(game, self, reyWithSaber)) {
                recipientFilter = otherSkywalker;
            }
            else { //Else, player can only relocate the lightsaber to Rey
                recipientFilter = Filters.Rey;
            }
            
            // Choose target(s)
            action.appendTargeting(
                new ChooseCardOnTableEffect(action, playerId, "Choose a recipient", recipientFilter) {
                    protected void cardSelected(PhysicalCard cardWithoutSaber) {
                        // Allow response(s)
                        action.allowResponses("Relocate " + GameUtils.getCardLink(anakinsLightsaber) + " to " + GameUtils.getCardLink(cardWithoutSaber),
                                new UnrespondableEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Perform result(s)
                                        action.appendEffect(
                                                new RelocateDeviceOrWeaponBetweenCharactersEffect(action, anakinsLightsaber, cardWithSaber, cardWithoutSaber));
                                    }
                                }
                        );
                    }
                }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.A_GOOD_FRIEND__EXCHANGE_CARD;
        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.hasInHand(game, playerId, Filters.Skywalker)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Exchange card for Ben Solo in Lost Pile");
            action.setActionMsg("Exchange a Skywalker from hand with Ben Solo in Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ExchangeCardInHandWithCardInLostPileEffect(action, playerId, Filters.Skywalker, Filters.Ben_Solo));
            actions.add(action);
        }

        return actions;
    }
}
